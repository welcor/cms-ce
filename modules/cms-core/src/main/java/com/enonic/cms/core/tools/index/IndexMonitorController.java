package com.enonic.cms.core.tools.index;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.tools.AbstractToolController;

public final class IndexMonitorController
    extends AbstractToolController
{

    public static final String getAllQUery = "{\n" +
        "  \"from\" : 0,\n" +
        "  \"size\" : 0,\n" +
        "  \"query\" : {\n" +
        "    \"match_all\" : {\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "";

    private ElasticSearchIndexService elasticSearchIndexService;

    private ContentIndexService contentIndexService;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final HashMap<String, Object> model = new HashMap<String, Object>();

        if ( req.getParameter( "recreateIndex" ) != null )
        {
            elasticSearchIndexService.deleteIndex( "cms" );
            contentIndexService.createIndex();
            redirectToReferrer( req, res );
        }

        model.put( "baseUrl", getBaseUrl( req ) );

        ClusterHealthResponse clusterHealthResponse = elasticSearchIndexService.getClusterHealth( "cms", false );
        model.put( "activeShards", clusterHealthResponse.getActiveShards() );
        final ClusterHealthStatus status = clusterHealthResponse.getStatus();
        model.put( "clusterStatus", status.toString() );
        model.put( "relocatingShards", clusterHealthResponse.getRelocatingShards() );
        model.put( "activePrimaryShards", clusterHealthResponse.getActivePrimaryShards() );
        model.put( "numberOfNodes", clusterHealthResponse.getNumberOfNodes() );
        model.put( "unassignedShards", clusterHealthResponse.getUnassignedShards() );
        final List<String> allValidationFailures = clusterHealthResponse.getAllValidationFailures();
        model.put( "validationFailures", allValidationFailures.isEmpty() ? Lists.newArrayList( "None" ) : allValidationFailures );

        final boolean indexExists = elasticSearchIndexService.indexExists( "cms" );
        model.put( "indexExists", indexExists );

        if ( status.equals( ClusterHealthStatus.RED ) )
        {
            renderView( req, res, model, "indexMonitorPage" );
            return;
        }

        model.put( "numberOfContent", getTotalHitsContent() );
        model.put( "numberOfBinaries", getTotalHitsBinaries() );
        renderView( req, res, model, "indexMonitorPage" );
    }

    private String getTotalHitsBinaries()
    {
        try
        {
            final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Binaries.toString(), getAllQUery );

            return "" + response.getHits().getTotalHits();
        }
        catch ( Exception e )
        {
            return "Not able to get total binaries: " + e.getMessage();
        }

    }

    private String getTotalHitsContent()
    {
        try
        {
            final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Content.toString(), getAllQUery );

            return "" + response.getHits().getTotalHits();
        }
        catch ( Exception e )
        {
            return "Not able to get total content: " + e.getMessage();
        }
    }

    @Autowired
    public void setElasticSearchIndexService( ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }

    @Autowired
    public void setContentIndexService( final ContentIndexService contentIndexService )
    {
        this.contentIndexService = contentIndexService;
    }
}
