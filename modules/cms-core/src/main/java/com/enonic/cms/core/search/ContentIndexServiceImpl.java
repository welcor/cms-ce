package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchOperationThreading;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.AggregatedQuery;
import com.enonic.cms.core.content.index.AggregatedResult;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentEntityFetcherImpl;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.search.builder.ContentIndexDataBuilder;
import com.enonic.cms.core.search.index.ContentIndexData;
import com.enonic.cms.core.search.query.IndexQueryException;
import com.enonic.cms.core.search.query.QueryTranslator;
import com.enonic.cms.store.dao.ContentDao;

/**
 * This class implements the content index service based on elasticsearch
 */
public class ContentIndexServiceImpl
    implements ContentIndexService
{
    public final static String INDEX_NAME = "cms";

    protected static final SearchOperationThreading OPERATION_THREADING = SearchOperationThreading.NO_THREADS;

    protected static final SearchType SEARCH_TYPE = SearchType.QUERY_THEN_FETCH;

    private IndexMappingProvider mappingProvider;

    private Client client;

    private IndexRequestCreator indexRequestCreator;

    private Logger LOG = Logger.getLogger( ContentIndexServiceImpl.class.getName() );

    private ContentIndexDataBuilder indexDataBuilder;

    private QueryTranslator translator;

    private ContentDao contentDao;

    private static final boolean DEBUG_EXEC_TIME = false;

    @PostConstruct
    public void startIndex()
    {
        LOG.info( "Setting up index" );

        indexRequestCreator = new IndexRequestCreator( INDEX_NAME );

        try
        {
            initalizeIndex( false );
            optimize();
        }
        catch ( Exception e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createIndex()
    {
        LOG.info( "creating index: " + INDEX_NAME );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( INDEX_NAME );

        ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.loadFromSource( IndexAnalyzerSettingsBuilder.buildAnalyserSettings() );

        //TODO: Other settings

        createIndexRequest.settings( settings );
        client.admin().indices().create( createIndexRequest ).actionGet();

        addMapping();
    }

    private void addMapping()
    {
        doAddMapping( INDEX_NAME, IndexType.Content );
        doAddMapping( INDEX_NAME, IndexType.Binaries );
    }

    private PutMappingResponse doAddMapping( String indexName, IndexType indexType )
    {
        String mapping = mappingProvider.getMapping( indexName, indexType );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType.toString() ).source( mapping );

        return this.client.admin().indices().putMapping( mappingRequest ).actionGet();
    }

    public int remove( ContentKey contentKey )
    {
        // TODO : Delete children aswell
        DeleteRequest deleteRequest = new DeleteRequest( INDEX_NAME, IndexType.Content.toString(), contentKey.toString() );

        DeleteResponse response = this.client.delete( deleteRequest ).actionGet();

        if ( response.notFound() )
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    public void removeByCategory( CategoryKey categoryKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setCategoryFilter( Arrays.asList( new CategoryKey[]{categoryKey} ) );
        doRemoveByQuery( contentIndexQuery );
    }

    public void removeByContentType( ContentTypeKey contentTypeKey )
    {
        ContentIndexQuery contentIndexQuery = new ContentIndexQuery( "" );
        contentIndexQuery.setContentTypeFilter( Arrays.asList( new ContentTypeKey[]{contentTypeKey} ) );
        doRemoveByQuery( contentIndexQuery );
    }

    private void doRemoveByQuery( ContentIndexQuery contentIndexQuery )
    {
        //TODO: How to handle the count?
        contentIndexQuery.setCount( 100 );

        final SearchSourceBuilder build;

        try
        {
            build = translator.build( contentIndexQuery );
        }
        catch ( Exception e )
        {
            throw new ContentIndexException( "Failed to build query: " + contentIndexQuery.toString(), e );
        }

        SearchHits hits = doExecuteSearchRequest( build );

        final int entriesToDelete = hits.getHits().length;
        LOG.info( "Prepare to delete: " + entriesToDelete + " entries from index " + INDEX_NAME );

        for ( SearchHit hit : hits )
        {
            DeleteRequest deleteRequest = new DeleteRequest( INDEX_NAME, IndexType.Content.toString(), hit.getId() );
            DeleteResponse response = this.client.delete( deleteRequest ).actionGet();
        }

        LOG.info( "Deleted from index " + INDEX_NAME + ", " + entriesToDelete + " entries successfully" );
    }


    public void index( ContentDocument doc, boolean deleteExisting )
    {
        ContentIndexData contentIndexData = indexDataBuilder.build( doc, ContentIndexDataBuilderSpecification.createBuildAllConfig() );

        Set<IndexRequest> indexRequests = indexRequestCreator.createIndexRequests( contentIndexData );

        for ( IndexRequest indexRequest : indexRequests )
        {
            doIndex( indexRequest );
        }
    }

    public void indexBulk( List<ContentDocument> docs )
    {
        BulkRequest bulkRequest = new BulkRequest();

        for ( ContentDocument doc : docs )
        {
            ContentIndexData contentIndexData = indexDataBuilder.build( doc, ContentIndexDataBuilderSpecification.createBuildAllConfig() );

            Set<IndexRequest> indexRequests = indexRequestCreator.createIndexRequests( contentIndexData );

            for ( IndexRequest indexRequest : indexRequests )
            {
                bulkRequest.add( indexRequest );
            }
        }

        BulkResponse resp = this.client.bulk( bulkRequest ).actionGet();
        LOG.info( "Bulk index done in " + resp.getTookInMillis() + " ms" );
    }


    private void doIndex( IndexRequest request )
    {
        this.client.index( request ).actionGet();
    }


    public boolean isIndexed( ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( INDEX_NAME, IndexType.Content.toString(), contentKey.toString() );

        GetResponse response = this.client.get( getRequest ).actionGet();

        return response.exists();
    }

    // TODO: We dont implement this one yet
    public IndexValueResultSet query( IndexValueQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // TODO: We dont implement this one yet
    public AggregatedResult query( AggregatedQuery query )
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private void initalizeIndex( boolean force )
        throws Exception
    {

        final boolean indexExists = indexExists();

        if ( indexExists && !force )
        {
            return;
        }
        else if ( indexExists )
        {
            deleteIndex();
        }

        createIndex();
    }

    public void optimize()
    {
        OptimizeRequest request = new OptimizeRequest( INDEX_NAME ).maxNumSegments( 1 ).waitForMerge( true );

        long start = System.currentTimeMillis();

        OptimizeResponse response = this.client.admin().indices().optimize( request ).actionGet();

        long finished = System.currentTimeMillis();

        LOG.info( "Optimized index for " + response.successfulShards() + " shards in " + ( finished - start ) + " ms" );

    }

    public void deleteIndex()
    {

        this.client.admin().indices().delete( new DeleteIndexRequest( INDEX_NAME ) ).actionGet();
    }

    public boolean indexExists()
    {
        try
        {
            this.client.admin().indices().status( new IndicesStatusRequest( INDEX_NAME ) ).actionGet();
            return true;
        }
        catch ( ElasticSearchException e )
        {
            return false;
        }
    }

    public ContentResultSet query( ContentIndexQuery query )
    {
        StopWatch timer = new StopWatch();

        timer.start( "build query" );

        final SearchSourceBuilder build;
        try
        {
            build = this.translator.build( query );
        }
        catch ( Exception e )
        {
            throw new IndexQueryException( "Failed to translate query: " + query.getQuery(), e );
        }
        finally
        {
            timer.stop();
        }

        final SearchHits hits = doExecuteSearchRequest( build );

        final int queryResultTotalSize = new Long( hits.getTotalHits() ).intValue();

        //ContentIndexQueryTracer.traceMatchCount( queryResultTotalSize, trace );
        if ( query.getIndex() > queryResultTotalSize )
        {
            final ContentResultSetNonLazy rs = new ContentResultSetNonLazy( query.getIndex() );
            rs.addError( "Index greater than result count: " + query.getIndex() + " greater than " + queryResultTotalSize );
            return rs;
        }

        final int fromIndex = Math.max( query.getIndex(), 0 );

        final ArrayList<ContentKey> keys = new ArrayList<ContentKey>();

        for ( SearchHit hit : hits )
        {
            keys.add( new ContentKey( hit.getId() ) );
        }

        return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), keys, fromIndex, queryResultTotalSize );
    }

    private SearchHits doExecuteSearchRequest( SearchSourceBuilder build )
    {
        StopWatch timer = new StopWatch();
        timer.start( "doQuery" );

        final SearchRequest req = Requests.searchRequest( INDEX_NAME )
            .types( IndexType.Content.toString() )
            .operationThreading( OPERATION_THREADING )
            .searchType( SEARCH_TYPE )
            .source( build );

        final SearchResponse res;

        try
        {
            res = this.client.search( req ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new ContentIndexException( "Failed to execute search: ", e );
        }
        timer.stop();

        parseSearchResultFailures( res );

        if ( DEBUG_EXEC_TIME )
        {
            System.out.println( timer.prettyPrint() );
        }

        return res.getHits();
    }


    //TODO: How should this be handled
    private void parseSearchResultFailures( SearchResponse res )
    {
        if ( res.getFailedShards() > 0 )
        {
            final ShardSearchFailure[] shardFailures = res.getShardFailures();

            for ( ShardSearchFailure failure : shardFailures )
            {
                final String reason = failure.reason();
                LOG.severe( "Status: " + failure.status() + " - Search failed on shard: " + reason );
                throw new ContentIndexException( "Search failed: " + reason );
            }
        }
    }

    @Autowired
    public void setMappingProvider( IndexMappingProvider mappingProvider )
    {
        this.mappingProvider = mappingProvider;
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setTranslator( QueryTranslator translator )
    {
        this.translator = translator;
    }

    @Autowired
    public void setIndexDataBuilder( ContentIndexDataBuilder indexDataBuilder )
    {
        this.indexDataBuilder = indexDataBuilder;
    }

}