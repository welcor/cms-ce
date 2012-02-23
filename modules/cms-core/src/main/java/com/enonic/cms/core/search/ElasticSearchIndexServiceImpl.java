package com.enonic.cms.core.search;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsResponse;
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
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 10:26 AM
 */
public class ElasticSearchIndexServiceImpl
    implements ElasticSearchIndexService
{
    private Client client;

    protected static final SearchOperationThreading OPERATION_THREADING = SearchOperationThreading.NO_THREADS;


    @Override
    public CreateIndexResponse createIndex( CreateIndexRequest createIndexRequest )
    {
        return client.admin().indices().create( createIndexRequest ).actionGet();
    }

    @Override
    public UpdateSettingsResponse updateIndexSettings( UpdateSettingsRequest updateSettingsRequest )
    {
        return client.admin().indices().updateSettings( updateSettingsRequest ).actionGet();
    }

    @Override
    public PutMappingResponse putMapping( PutMappingRequest putMappingRequest )
    {
        return this.client.admin().indices().putMapping( putMappingRequest ).actionGet();
    }

    @Override
    public DeleteResponse delete( DeleteRequest deleteRequest )
    {
        return this.client.delete( deleteRequest ).actionGet();
    }

    @Override
    public BulkResponse bulk( BulkRequest bulkRequest )
    {
        return this.client.bulk( bulkRequest ).actionGet();
    }

    @Override
    public IndexResponse index( IndexRequest indexRequest )
    {
        return this.client.index( indexRequest ).actionGet();
    }

    @Override
    public GetResponse get( GetRequest getRequest )
    {
        return this.client.get( getRequest ).actionGet();
    }

    @Override
    public OptimizeResponse optimize( OptimizeRequest optimizeRequest )
    {
        return this.client.admin().indices().optimize( optimizeRequest ).actionGet();
    }

    @Override
    public DeleteIndexResponse deleteIndex( DeleteIndexRequest deleteIndexRequest )
    {
        return this.client.admin().indices().delete( deleteIndexRequest ).actionGet();
    }

    @Override
    public SearchResponse search( SearchRequest searchRequest )
    {
        return this.client.search( searchRequest ).actionGet();
    }

    @Override
    public FlushResponse flush( FlushRequest flushRequest )
    {
        return client.admin().indices().flush( flushRequest ).actionGet();
    }

    @Override
    public IndicesStatusResponse status( IndicesStatusRequest indicesStatusRequest )
    {
        return this.client.admin().indices().status( indicesStatusRequest ).actionGet();
    }

    @Autowired
    public void setClient( Client client )
    {
        this.client = client;
    }
}


