/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.tools.index;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.Maps;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.core.search.ElasticSearchIndexService;
import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.tools.AbstractToolController;

public final class IndexMonitorController
    extends AbstractToolController
{

    protected static final PeriodFormatter HOURS_MINUTES_MILLIS =
        new PeriodFormatterBuilder().appendHours().appendSuffix( " h ", " h " ).appendMinutes().appendSuffix( " m ",
                                                                                                              " m " ).appendSeconds().appendSuffix(
            " s ", " s " ).appendMillis().appendSuffix( " ms", " ms" ).toFormatter();

    private static final DateTimeFormatter SIMPLE_DATE_FORMAT = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm" );

    private static final String GET_ALL_QUERY = "{\n" +
        "  \"from\" : 0,\n" +
        "  \"size\" : 0,\n" +
        "  \"query\" : {\n" +
        "    \"match_all\" : {\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "";

    private ElasticSearchIndexService elasticSearchIndexService;

    private ReindexContentToolService reindexContentToolService;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final Map<String, Object> model = Maps.newHashMap();
        model.put( "baseUrl", getBaseUrl( req ) );

        final String op = req.getParameter( "op" );
        if ( !"info".equals( op ) )
        {
            renderView( req, res, model, "indexMonitorPage" );
            return;
        }

        final ClusterHealthResponse clusterHealthResponse = elasticSearchIndexService.getClusterHealth( "cms", false );
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
        model.put( "numberOfContent", getTotalHitsContent( status ) );
        model.put( "numberOfBinaries", getTotalHitsBinaries( status ) );

        model.put( "reindexInProgress", reindexContentToolService.isReIndexInProgress() );

        if ( reindexContentToolService.getLastReindexTime() != null )
        {
            model.put( "lastReindexTime", getLastIndexTimeString() );
            model.put( "lastReindexTimeUsed", getTimeUsedString() );
        }

        renderView( req, res, model, "indexMonitorPage_info" );
    }

    private String getLastIndexTimeString()
    {
        return SIMPLE_DATE_FORMAT.print( reindexContentToolService.getLastReindexTime() );
    }

    private String getTimeUsedString()
    {
        final long lastReindexTimeUsed = reindexContentToolService.getLastReindexTimeUsed();

        return HOURS_MINUTES_MILLIS.print( new Period( lastReindexTimeUsed ) ).trim();
    }

    private long getTotalHitsBinaries( final ClusterHealthStatus status )
    {
        if ( !status.equals( ClusterHealthStatus.RED ) )
        {
            final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Binaries.toString(), GET_ALL_QUERY );
            return response.getHits().getTotalHits();
        }
        else
        {
            return -1;
        }
    }

    private long getTotalHitsContent( final ClusterHealthStatus status )
    {
        if ( !status.equals( ClusterHealthStatus.RED ) )
        {
            final SearchResponse response = elasticSearchIndexService.search( "cms", IndexType.Content.toString(), GET_ALL_QUERY );
            return response.getHits().getTotalHits();
        }
        else
        {
            return -1;
        }
    }

    @Autowired
    public void setElasticSearchIndexService( ElasticSearchIndexService elasticSearchIndexService )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
    }

    @Autowired
    public void setReindexContentToolService( final ReindexContentToolService reindexContentToolService )
    {
        this.reindexContentToolService = reindexContentToolService;
    }
}
