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
        buildClusterHealtStatus( json );
        buildIndexStatus( json );
    }

    private void buildIndexStatus( final ObjectNode json )
    {
        final IndexStatus indexStatus;
        try
        {
            indexStatus = elasticSearchIndexService.getIndexStatus( "cms" );

            if ( indexStatus == null )
            {
                json.put( "documents", "error : not able to fetch indexStatus" );
                json.put( "primaryShardsStoreSize", "error : not able to fetch indexStatus" );
                json.put( "totalStoreSize", "error : not able to fetch indexStatus" );
            }
            else
            {
                json.put( "documents", indexStatus.getDocs() == null
                    ? "error: not able to get number of documents"
                    : indexStatus.getDocs().getNumDocs() + "" );
                json.put( "primaryShardsStoreSize", indexStatus.getPrimaryStoreSize() == null
                    ? "error: not able to get primaryStoreSize"
                    : indexStatus.getPrimaryStoreSize().toString() );
                json.put( "totalStoreSize",
                          indexStatus.getStoreSize() == null ? "error: not able to get storeSize" : indexStatus.getStoreSize().toString() );
            }
        }
        catch ( Exception e )
        {
            json.put( "exception", exceptionToString( e ) );
        }

    }

    private void buildClusterHealtStatus( final ObjectNode json )
    {
        final ClusterHealthResponse clusterHealthResponse;
        try
        {
            clusterHealthResponse = elasticSearchIndexService.getClusterHealth( "cms", false );
            if ( clusterHealthResponse == null )
            {
                json.put( "status", "error : not able to fetch clusterHealthResponse" );
                json.put( "activeShards", "error : not able to fetch clusterHealthResponse" );
                json.put( "activePrimaryShards", "error : not able to fetch clusterHealthResponse" );
                json.put( "activeReplicas", "error : not able to fetch clusterHealthResponse" );
                json.put( "unassignedShards", "error : not able to fetch clusterHealthResponse" );
                json.put( "relocatingShards", "error : not able to fetch clusterHealthResponse" );
                json.put( "initializingShards", "error : not able to fetch clusterHealthResponse" );
            }
            else
            {
                json.put( "status", clusterHealthResponse.getStatus().toString() );
                json.put( "activeShards", clusterHealthResponse.getActiveShards() );
                json.put( "activePrimaryShards", clusterHealthResponse.getActivePrimaryShards() );
                json.put( "activeReplicas", clusterHealthResponse.getActiveShards() - clusterHealthResponse.getActivePrimaryShards() );
                json.put( "unassignedShards", clusterHealthResponse.getUnassignedShards() );
                json.put( "relocatingShards", clusterHealthResponse.getRelocatingShards() );
                json.put( "initializingShards", clusterHealthResponse.getInitializingShards() );
            }
        }
        catch ( Exception e )
        {
            json.put( "exception", exceptionToString( e ) );  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
