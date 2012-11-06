package com.enonic.cms.core.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 10:26 AM
 */
@Service
public class ElasticSearchIndexServiceImpl
    implements ElasticSearchIndexService
{
    private static final SearchType DEFAULT_SEARCH_TYPE = SearchType.QUERY_THEN_FETCH;

    private static final int MAX_NUM_SEGMENTS = 1;

    private static final boolean WAIT_FOR_MERGE = true;

    public static final int CLUSTER_HEALTH_TIMEOUT_SECONDS = 10;

    public static final TimeValue INDEX_REQUEST_TIMEOUT_SECONDS = TimeValue.timeValueSeconds( 10 );

    private IndexSettingBuilder indexSettingBuilder;

    private ContentIndexRequestCreator contentIndexRequestCreator;

    private Client client;

    private final Logger LOG = Logger.getLogger( ElasticSearchIndexServiceImpl.class.getName() );

    @Override
    public void createIndex( String indexName )
    {
        LOG.fine( "creating index: " + indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettingBuilder.buildIndexSettings() );

        client.admin().indices().create( createIndexRequest ).actionGet();

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
        updateSettingsRequest.settings( indexSettingBuilder.buildIndexSettings() );

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
        final IndexResponse indexResponse = this.client.index( indexRequest ).actionGet( INDEX_REQUEST_TIMEOUT_SECONDS );
        return indexResponse;
    }

    @Override
    public boolean get( String indexName, IndexType indexType, ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( indexName, indexType.toString(), contentKey.toString() );

        final GetResponse getResponse = this.client.get( getRequest ).actionGet();

        return getResponse.exists();
    }

    public long count( String indexName, String indexType, SearchSourceBuilder sourceBuilder )
    {
        // TODO: This should be optimized to use count, but then get rid of the sourceBuilder-stuff first

        final SearchRequest searchRequest =
            Requests.searchRequest( indexName ).types( indexType ).searchType( DEFAULT_SEARCH_TYPE ).source( sourceBuilder );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        parseSearchResultFailures( searchResponse );

        return searchResponse.getHits().getTotalHits();
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
        getRequest.fields( "_source" );

        final GetResponse getResponse = this.client.get( getRequest ).actionGet();

        final Map<String, Object> fieldValues = getResponse.getSource();
        final Map<String, GetField> fields = new HashMap<String, GetField>();
        if ( fieldValues != null )
        {
            for ( String key : fieldValues.keySet() )
            {
                final Object value = fieldValues.get( key );
                if ( value instanceof List )
                {
                    fields.put( key, new GetField( key, (List) value ) );
                }
                else
                {
                    fields.put( key, new GetField( key, Lists.newArrayList( value ) ) );
                }
            }
        }
        return fields;
    }

    private SearchResponse doSearchRequest( SearchRequest searchRequest )
    {
        return this.client.search( searchRequest ).actionGet();
    }

    private void parseSearchResultFailures( SearchResponse res )
    {
        if ( res.getFailedShards() > 0 )
        {
            final ShardSearchFailure[] shardFailures = res.getShardFailures();

            StringBuilder reasonBuilder = new StringBuilder();

            for ( ShardSearchFailure failure : shardFailures )
            {
                final String reason = failure.reason();
                LOG.severe( "Status: " + failure.status() + " - Search failed on shard: " + reason );
                reasonBuilder.append( reason );
            }

            throw new ContentIndexException( "Search failed: " + reasonBuilder.toString() );
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
        final ClusterHealthRequest clusterHealthRequest =
            new ClusterHealthRequest( indexName ).timeout( TimeValue.timeValueSeconds( 60 ) ).waitForYellowStatus();
        final ClusterHealthResponse clusterHealth = client.admin().cluster().health( clusterHealthRequest ).actionGet();
        if ( clusterHealth.timedOut() )
        {
            LOG.warning( "ElasticSearch cluster health timed out" );
        }
        else
        {
            LOG.info( "ElasticSearch cluster health: Status " + clusterHealth.status().name() + "; " +
                          clusterHealth.getNumberOfNodes() + " nodes; " + clusterHealth.getActiveShards() + " active shards." );
        }

        final IndicesExistsResponse exists = this.client.admin().indices().exists( new IndicesExistsRequest( indexName ) ).actionGet();
        return exists.exists();
    }

    @Override
    public ClusterHealthResponse getClusterHealth( String indexName )
    {

        ClusterHealthRequest request = new ClusterHealthRequest( indexName );
        request.waitForYellowStatus();
        request.timeout( TimeValue.timeValueSeconds( CLUSTER_HEALTH_TIMEOUT_SECONDS ) );

        final ClusterHealthResponse clusterHealthResponse = this.client.admin().cluster().health( request ).actionGet();

        return clusterHealthResponse;
    }

    @Autowired
    public void setIndexSettingBuilder( final IndexSettingBuilder indexSettingBuilder )
    {
        this.indexSettingBuilder = indexSettingBuilder;
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }

    @Autowired
    public void setContentIndexRequestCreator( ContentIndexRequestCreator contentIndexRequestCreator )
    {
        this.contentIndexRequestCreator = contentIndexRequestCreator;
    }
}


