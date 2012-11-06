package com.enonic.cms.core.search;

import java.util.Map;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/23/12
 * Time: 10:17 AM
 */
public interface ElasticSearchIndexService
{
    public void createIndex( String indexName );

    public void deleteIndex( String indexName );

    public void putMapping( String indexName, String indexType, String mapping );

    public void deleteMapping( String indexName, IndexType indexType );

    public boolean delete( String indexName, IndexType indexType, ContentKey contentKey );

    public void index( String indexName, ContentIndexData contentIndexData );

    public void index( IndexRequest request );

    public boolean get( String indexName, IndexType indexType, ContentKey contentKey );

    public long count( String indexName, String indexType, SearchSourceBuilder sourceBuilder );

    public void optimize( String indexName );

    public SearchResponse search( String indexName, String indexType, SearchSourceBuilder sourceBuilder );

    public SearchResponse search( String indexName, String indexType, String sourceBuilder );

    public Map<String, GetField> search( String indexName, IndexType indexType, ContentKey contentKey );

    public void flush( String indexName );

    public boolean indexExists( String indexName );

    public ClusterHealthResponse getClusterHealth( String indexName, boolean waitForYellow );

}


