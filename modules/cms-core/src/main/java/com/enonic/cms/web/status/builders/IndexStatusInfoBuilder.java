package com.enonic.cms.web.status.builders;

import org.codehaus.jackson.node.ObjectNode;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.web.status.StatusInfoBuilder;

@Component
public class IndexStatusInfoBuilder
    extends StatusInfoBuilder
{
    @Autowired
    private ElasticSearchIndexService elasticSearchIndexService;

    public IndexStatusInfoBuilder()
    {
        super( "index" );
    }

    @Override
    protected void build( final ObjectNode json )
    {
        final IndexStatus indexStatus = elasticSearchIndexService.getIndexStatus( "cms" );

        final ClusterHealthResponse clusterHealthResponse = elasticSearchIndexService.getClusterHealth( "cms", false );

        if ( clusterHealthResponse == null )
        {
            json.put( "error", "not able to get cluster health response" );
            return;
        }

        json.put( "status", clusterHealthResponse.getStatus().toString() );
        json.put( "activeShards", clusterHealthResponse.getActiveShards() );
        json.put( "activePrimaryShards", clusterHealthResponse.getActivePrimaryShards() );
        json.put( "activeReplicas", clusterHealthResponse.getActiveShards() - clusterHealthResponse.getActivePrimaryShards() );
        json.put( "unassignedShards", clusterHealthResponse.getUnassignedShards() );
        json.put( "relocatingShards", clusterHealthResponse.getRelocatingShards() );
        json.put( "initializingShards", clusterHealthResponse.getInitializingShards() );

        json.put( "documents", indexStatus.getDocs().getNumDocs() );
        json.put( "primaryShardsStoreSize", indexStatus.getPrimaryStoreSize().toString() );
        json.put( "totalStoreSize", indexStatus.getStoreSize().toString() );

    }

}
