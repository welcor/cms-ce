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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 10:17 AM
 */
public interface ElasticSearchIndexService
{


    public CreateIndexResponse createIndex( CreateIndexRequest createIndexRequest );

    public DeleteIndexResponse deleteIndex( DeleteIndexRequest deleteIndexRequest );

    public UpdateSettingsResponse updateIndexSettings( UpdateSettingsRequest updateSettingsRequest );

    public PutMappingResponse putMapping( PutMappingRequest putMappingRequest );

    public DeleteResponse delete( DeleteRequest deleteRequest );

    public BulkResponse bulk( BulkRequest bulkRequest );

    public IndexResponse index( IndexRequest indexRequest );

    public GetResponse get( GetRequest getRequest );

    public OptimizeResponse optimize( OptimizeRequest optimizeRequest );

    public SearchResponse search( SearchRequest searchRequest );

    public FlushResponse flush( FlushRequest flushRequest );

    public IndicesStatusResponse status( IndicesStatusRequest indicesStatusRequest );

}


