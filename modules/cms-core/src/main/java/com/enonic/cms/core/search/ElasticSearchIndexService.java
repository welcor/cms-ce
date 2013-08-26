/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.Map;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
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
    public Map<String, String> getIndexSettings( final String indexName );

    public NodeInfo getLocalNodeInfo();

    public NodesInfoResponse getNodesInfo( final String[] nodeIds );

    public Map<String, String> getClusterSettings();

    public ClusterStateResponse getClusterState();

    public void updateIndexSetting( final String indexName, final String setting, final String value );

    public void updateClusterSettings( final String setting, final String value );

    public void createIndex( String indexName );

    public void deleteIndex( String indexName );

    public void putMapping( String indexName, String indexType, String mapping );

    public void deleteMapping( String indexName, IndexType indexType );

    public boolean delete( String indexName, IndexType indexType, ContentKey contentKey );

    public void index( String indexName, ContentIndexData contentIndexData );

    public void index( IndexRequest request );

    public boolean get( String indexName, IndexType indexType, ContentKey contentKey );

    public long count( String indexName, String indexType, SearchSourceBuilder sourceBuilder );

    public long count( String indexName, String indexType );

    public void optimize( String indexName );

    public SearchResponse search( String indexName, String indexType, SearchSourceBuilder sourceBuilder );

    public SearchResponse search( String indexName, String indexType, String sourceBuilder );

    public Map<String, GetField> search( String indexName, IndexType indexType, ContentKey contentKey );

    public void flush( String indexName );

    public boolean indexExists( String indexName );

    public ClusterHealthResponse getClusterHealth( String indexName, boolean waitForYellow );

    public Client getClient();

    public IndexStatus getIndexStatus( final String indexName );

}


