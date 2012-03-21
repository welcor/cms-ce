package com.enonic.cms.core.search;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchOperationThreading;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.index.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 10:26 AM
 */
@Component
public class ElasticSearchIndexServiceImpl
    implements ElasticSearchIndexService
{

    protected static final SearchType DEFAULT_SEARCH_TYPE = SearchType.QUERY_THEN_FETCH;

    protected static final int MAX_NUM_SEGMENTS = 1;

    protected static final boolean WAIT_FOR_MERGE = true;

    private IndexSettingsBuilder indexSettingsBuilder;

    private ContentIndexRequestCreator contentIndexRequestCreator;

    private Client client;

    protected static final SearchOperationThreading OPERATION_THREADING = SearchOperationThreading.NO_THREADS;

    private Logger LOG = Logger.getLogger( ElasticSearchIndexServiceImpl.class.getName() );

    @Override
    public Client getClient()
    {
        return this.client;
    }

    public void initalizeIndex( String indexName, boolean forceDelete )
    {
        final boolean indexExists = indexExists( indexName );

        if ( indexExists && !forceDelete )
        {
            return;
        }
        else if ( indexExists )
        {
            deleteIndex( indexName );
        }

        createIndex( indexName );
    }

    @Override
    public void createIndex( String indexName )
    {
        LOG.fine( "creating index: " + indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );

        createIndexRequest.settings( indexSettingsBuilder.buildSettings() );

        client.admin().indices().create( createIndexRequest ).actionGet();
    }

    @Override
    public void updateIndexSettings( String indexName )
    {
        LOG.fine( "Refresh settings for index: " + indexName );
        UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest( indexName );
        updateSettingsRequest.settings( indexSettingsBuilder.buildSettings() );

        client.admin().indices().updateSettings( updateSettingsRequest ).actionGet();
    }

    @Override
    public void putMapping( String indexName, IndexType indexType, String mapping )
    {
        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType.toString() ).source( mapping );

        this.client.admin().indices().putMapping( mappingRequest ).actionGet();
    }

    @Override
    public boolean delete( String indexName, IndexType indexType, ContentKey contentKey )
    {
        DeleteRequest deleteRequest = new DeleteRequest( indexName, indexType.toString(), contentKey.toString() );

        final DeleteResponse deleteResponse = this.client.delete( deleteRequest ).actionGet();

        return !deleteResponse.notFound();
    }

    @Override
    public void index( String indexName, Collection<ContentIndexData> contentIndexDatas )
    {

        BulkRequest bulkRequest = new BulkRequest();

        for ( ContentIndexData contentIndexData : contentIndexDatas )
        {
            Set<IndexRequest> indexRequests = contentIndexRequestCreator.createIndexRequests( indexName, contentIndexData );

            for ( IndexRequest indexRequest : indexRequests )
            {
                bulkRequest.add( indexRequest );
            }
        }

        BulkResponse resp = this.client.bulk( bulkRequest ).actionGet();

        LOG.info( "Bulk index of " + contentIndexDatas.size() + " done in " + resp.getTookInMillis() + " ms" );
    }

    @Override
    public void index( String indexName, ContentIndexData contentIndexData )
    {
        Set<IndexRequest> indexRequests = contentIndexRequestCreator.createIndexRequests( indexName, contentIndexData );

        for ( IndexRequest indexRequest : indexRequests )
        {
            final IndexResponse indexResponse = doIndex( indexRequest );
            LOG.finest( "Content indexed with id: " + indexResponse.getId() );
        }
    }

    private IndexResponse doIndex( IndexRequest indexRequest )
    {
        return this.client.index( indexRequest ).actionGet();
    }

    @Override
    public boolean get( String indexName, IndexType indexType, ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( indexName, IndexType.Content.toString(), contentKey.toString() );

        final GetResponse getResponse = this.client.get( getRequest ).actionGet();

        return getResponse.exists();
    }

    @Override
    public void optimize( String indexName )
    {
        OptimizeRequest optimizeRequest =
            new OptimizeRequest( indexName ).maxNumSegments( MAX_NUM_SEGMENTS ).waitForMerge( WAIT_FOR_MERGE );

        long start = System.currentTimeMillis();
        final OptimizeResponse optimizeResponse = this.client.admin().indices().optimize( optimizeRequest ).actionGet();
        long finished = System.currentTimeMillis();

        LOG.fine( "Optimized index for " + optimizeResponse.successfulShards() + " shards in " + ( finished - start ) + " ms" );
    }

    @Override
    public void deleteIndex( String indexName )
    {
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest( indexName );

        final DeleteIndexResponse deleteIndexResponse = this.client.admin().indices().delete( deleteIndexRequest ).actionGet();

        if ( !deleteIndexResponse.acknowledged() )
        {
            LOG.warning( "Index " + indexName + " not deleted" );
        }
        else
        {
            LOG.fine( "Index " + indexName + " deleted" );
        }
    }

    @Override
    public SearchResponse search( String indexName, IndexType indexType, SearchSourceBuilder sourceBuilder )
    {
        final SearchRequest searchRequest =
            Requests.searchRequest( indexName ).types( indexType.toString() ).searchType( DEFAULT_SEARCH_TYPE ).source( sourceBuilder );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        parseSearchResultFailures( searchResponse );

        return searchResponse;
    }

    @Override
    public SearchResponse search( String indexName, IndexType indexType, String sourceBuilder )
    {
        SearchRequest searchRequest = new SearchRequest( "cms" ).types( indexType.toString() ).source( sourceBuilder );

        return doSearchRequest( searchRequest );
    }


    @Override
    public Map<String, GetField> search( String indexName, IndexType indexType, ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( indexName, indexType.toString(), contentKey.toString() );
        getRequest.fields( "*" );

        final GetResponse getResponse = this.client.get( getRequest ).actionGet();

        final Map<String, GetField> fields = getResponse.getFields();
        return fields;
    }

    private SearchResponse doSearchRequest( SearchRequest searchRequest )
    {
        return this.client.search( searchRequest ).actionGet();
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

    @Override
    public void flush( String indexName )
    {
        final FlushRequest flushRequest = Requests.flushRequest( indexName ).refresh( true );
        final FlushResponse flushResponse = client.admin().indices().flush( flushRequest ).actionGet();

        LOG.fine( "Flush request executed with " + flushResponse.getSuccessfulShards() + " successfull shards" );
    }

    @Override
    public boolean indexExists( String indexName )
    {
        try
        {
            final IndicesStatusRequest indicesStatusRequest = new IndicesStatusRequest( indexName );
            final IndicesStatusResponse indicesStatusResponse = this.client.admin().indices().status( indicesStatusRequest ).actionGet();

            LOG.fine( "Index " + indexName + " status ok with " + indicesStatusResponse.getSuccessfulShards() + " shards" );

            return true;
        }
        catch ( ElasticSearchException e )
        {
            return false;
        }
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }

    @Autowired
    public void setIndexSettingsBuilder( IndexSettingsBuilder indexSettingsBuilder )
    {
        this.indexSettingsBuilder = indexSettingsBuilder;
    }

    @Autowired
    public void setContentIndexRequestCreator( ContentIndexRequestCreator contentIndexRequestCreator )
    {
        this.contentIndexRequestCreator = contentIndexRequestCreator;
    }
}


