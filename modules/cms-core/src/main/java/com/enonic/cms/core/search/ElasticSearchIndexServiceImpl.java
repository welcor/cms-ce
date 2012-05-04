package com.enonic.cms.core.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
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
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

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
    public void createIndex( String indexName )
    {
        LOG.fine( "creating index: " + indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettingsBuilder.buildSettings() );

        final CreateIndexResponse createIndexResponse = client.admin().indices().create( createIndexRequest ).actionGet();

        LOG.info( "Created index: " + indexName );
    }

    public Map<String, Object> getMapping( IndexType indexType, String indexName )
    {
        ClusterState clusterState = client.admin().cluster().prepareState().setFilterIndices( indexName ).execute().actionGet().getState();
        IndexMetaData indexMetaData = clusterState.getMetaData().index( indexName );

        if ( indexMetaData == null )
        {
            LOG.warning( "Not able to load existing mapping for index: " + indexName + ", type: " + indexType.toString() );
            return null;
        }

        MappingMetaData mappingMetaData = indexMetaData.mapping( indexType.toString() );
        try
        {
            return mappingMetaData.getSourceAsMap();
        }
        catch ( IOException e )
        {
            LOG.warning( "Not able to load existing mapping for index: " + indexName + ", type: " + indexType.toString() );
            return null;
        }
    }

    @Override
    public void updateIndexSettings( String indexName )
    {
        UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest( indexName );
        updateSettingsRequest.settings( indexSettingsBuilder.buildSettings() );

        client.admin().indices().updateSettings( updateSettingsRequest ).actionGet();

        LOG.info( "Settings updated for index: " + indexName );
    }

    @Override
    public void putMapping( String indexName, String indexType, String mapping )
    {
        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType ).source( mapping );

        this.client.admin().indices().putMapping( mappingRequest ).actionGet();

        LOG.info( "Mapping for index " + indexName + ", index-type: " + indexType + " deleted" );
    }

    @Override
    public void deleteMapping( String indexName, IndexType indexType )
    {
        DeleteMappingRequest deleteMappingRequest = new DeleteMappingRequest( indexName ).type( indexType.toString() );

        this.client.admin().indices().deleteMapping( deleteMappingRequest ).actionGet();

        LOG.info( "Mapping for index " + indexName + ", index-type: " + indexType + " deleted" );
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

    public void index( IndexRequest request )
    {
        doIndex( request );
    }

    private IndexResponse doIndex( IndexRequest indexRequest )
    {
        return this.client.index( indexRequest ).actionGet();
    }

    @Override
    public boolean get( String indexName, IndexType indexType, ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( indexName, indexType.toString(), contentKey.toString() );

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
    public SearchResponse search( String indexName, String indexType, SearchSourceBuilder sourceBuilder )
    {
        final SearchRequest searchRequest =
            Requests.searchRequest( indexName ).types( indexType ).searchType( DEFAULT_SEARCH_TYPE ).source( sourceBuilder );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        parseSearchResultFailures( searchResponse );

        return searchResponse;
    }

    @Override
    public SearchResponse search( String indexName, String indexType, String sourceBuilder )
    {
        SearchRequest searchRequest = new SearchRequest( indexName ).types( indexType ).source( sourceBuilder );

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


