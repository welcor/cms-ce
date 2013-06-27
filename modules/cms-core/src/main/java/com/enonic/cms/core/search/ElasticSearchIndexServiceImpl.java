/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.ElasticSearchException;
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
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public static final TimeValue INDEX_REQUEST_TIMEOUT_SECONDS = TimeValue.timeValueSeconds( 60 );

    public static final TimeValue DELETE_FROM_INDEX_TIMEOUT_SECONDS = TimeValue.timeValueSeconds( 60 );

    private int statusTimeout;

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    private IndexSettingBuilder indexSettingBuilder;

    private ContentIndexRequestCreator contentIndexRequestCreator;

    private Client client;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticSearchIndexServiceImpl.class );


    public Client getClient()
    {
        return client;
    }

    @Override
    public void createIndex( String indexName )
    {
        LOG.debug( "creating index: " + indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettingBuilder.buildIndexSettings() );

        try
        {
            client.admin().indices().create( createIndexRequest ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to create index:" + indexName, e );
        }

        LOG.info( "Created index: " + indexName );
    }

    @Override
    public void putMapping( String indexName, String indexType, String mapping )
    {
        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType ).source( mapping );

        try
        {
            this.client.admin().indices().putMapping( mappingRequest ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to apply mapping to index: " + indexName, e );
        }

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

        final DeleteResponse deleteResponse;
        try
        {
            deleteResponse = this.client.delete( deleteRequest ).actionGet( DELETE_FROM_INDEX_TIMEOUT_SECONDS );
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to delete content with key: " + contentKey, e );
        }

        return !deleteResponse.notFound();
    }

    @Override
    public void index( String indexName, ContentIndexData contentIndexData )
    {
        Set<IndexRequest> indexRequests = contentIndexRequestCreator.createIndexRequests( indexName, contentIndexData );

        for ( IndexRequest indexRequest : indexRequests )
        {
            final IndexResponse indexResponse = doIndex( indexRequest );
            LOG.trace( "Content indexed with id: " + indexResponse.getId() );
        }
    }

    public void index( IndexRequest request )
    {
        doIndex( request );
    }

    private IndexResponse doIndex( IndexRequest indexRequest )
    {
        try
        {
            return this.client.index( indexRequest ).actionGet( INDEX_REQUEST_TIMEOUT_SECONDS );
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to index content with id: " + indexRequest.id(), e );
        }
    }

    @Override
    public boolean get( String indexName, IndexType indexType, ContentKey contentKey )
    {
        final GetRequest getRequest = new GetRequest( indexName, indexType.toString(), contentKey.toString() );

        final GetResponse getResponse;
        try
        {
            getResponse = this.client.get( getRequest ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to get contentKey with id " + contentKey.toString(), e );
        }

        return getResponse.exists();
    }

    public long count( String indexName, String indexType, SearchSourceBuilder sourceBuilder )
    {
        final SearchRequest searchRequest =
            Requests.searchRequest( indexName ).types( indexType ).searchType( DEFAULT_SEARCH_TYPE ).source( sourceBuilder );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        parseSearchResultFailures( searchResponse );

        return searchResponse.getHits().getTotalHits();
    }

    public long count( String indexName, String indexType )
    {
        final CountRequestBuilder countRequestBuilder = new CountRequestBuilder( this.client );
        countRequestBuilder.setIndices( indexName );
        countRequestBuilder.setTypes( indexType );

        final CountResponse countResponse = this.client.count( countRequestBuilder.request() ).actionGet();

        return countResponse.count();
    }

    @Override
    public void optimize( String indexName )
    {
        OptimizeRequest optimizeRequest =
            new OptimizeRequest( indexName ).maxNumSegments( MAX_NUM_SEGMENTS ).waitForMerge( WAIT_FOR_MERGE );

        long start = System.currentTimeMillis();
        final OptimizeResponse optimizeResponse = this.client.admin().indices().optimize( optimizeRequest ).actionGet();
        long finished = System.currentTimeMillis();

        LOG.debug( "Optimized index for " + optimizeResponse.successfulShards() + " shards in " + ( finished - start ) + " ms" );
    }

    @Override
    public void deleteIndex( String indexName )
    {
        final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest( indexName );

        final DeleteIndexResponse deleteIndexResponse = this.client.admin().indices().delete( deleteIndexRequest ).actionGet();

        if ( !deleteIndexResponse.acknowledged() )
        {
            LOG.warn( "Index " + indexName + " not deleted" );
        }
        else
        {
            LOG.debug( "Index " + indexName + " deleted" );
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
                LOG.error( "Status: " + failure.status() + " - Search failed on shard: " + reason );
                reasonBuilder.append( reason );
            }

            throw new IndexException( "Search failed: " + reasonBuilder.toString() );
        }
    }

    @Override
    public void flush( String indexName )
    {
        final FlushRequest flushRequest = Requests.flushRequest( indexName ).refresh( true );
        final FlushResponse flushResponse = client.admin().indices().flush( flushRequest ).actionGet();

        LOG.debug( "Flush request executed with " + flushResponse.getSuccessfulShards() + " successfull shards" );
    }

    @Override
    public boolean indexExists( String indexName )
    {
        final IndicesExistsResponse exists = this.client.admin().indices().exists( new IndicesExistsRequest( indexName ) ).actionGet();
        return exists.exists();
    }

    @Override
    public ClusterHealthResponse getClusterHealth( String indexName, boolean waitForYellow )
    {
        ClusterHealthRequest request = new ClusterHealthRequest( indexName );

        if ( waitForYellow )
        {
            request.waitForYellowStatus().timeout( TimeValue.timeValueSeconds( statusTimeout ) );
        }
        else
        {
            request.timeout( CLUSTER_NOWAIT_TIMEOUT );
        }

        final ClusterHealthResponse clusterHealthResponse = this.client.admin().cluster().health( request ).actionGet();

        if ( clusterHealthResponse.timedOut() )
        {
            LOG.warn( "ElasticSearch cluster health timed out" );
        }
        else
        {
            LOG.trace( "ElasticSearch cluster health: Status " + clusterHealthResponse.status().name() + "; " +
                           clusterHealthResponse.getNumberOfNodes() + " nodes; " + clusterHealthResponse.getActiveShards() +
                           " active shards." );
        }

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

    @Value("${cms.index.statusTimeout}")
    public void setStatusTimeout( final int statusTimeout )
    {
        this.statusTimeout = statusTimeout;
    }
}


