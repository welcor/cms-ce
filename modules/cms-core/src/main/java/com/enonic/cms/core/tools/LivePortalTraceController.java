/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.portal.livetrace.LivePortalTraceJsonGenerator;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.core.portal.livetrace.PortalRequestTraceRow;
import com.enonic.cms.core.portal.livetrace.systeminfo.SystemInfo;
import com.enonic.cms.core.portal.livetrace.systeminfo.SystemInfoFactory;

/**
 * This class implements the connection info controller.
 */
public final class LivePortalTraceController
    extends AbstractToolController2
{
    @Autowired
    private SystemInfoFactory systemInfoFactory;

    private LivePortalTraceService livePortalTraceService;

    private CacheManager cacheManager;

    @Autowired
    private LivePortalTraceJsonGenerator livePortalTraceJsonGenerator;

    @Override
    protected void doPost( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String command = req.getParameter( "command" );

        if ( "clear-longestpagerequests".equals( command ) )
        {
            livePortalTraceService.clearLongestPageRequestsTraces();
        }
        else if ( "clear-longestattachmentrequests".equals( command ) )
        {
            livePortalTraceService.clearLongestAttachmentRequestTraces();
        }
        else if ( "clear-longestimagerequests".equals( command ) )
        {
            livePortalTraceService.clearLongestImageRequestTraces();
        }

        res.setStatus( HttpServletResponse.SC_NO_CONTENT );
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String systemInfo = req.getParameter( "system-info" );
        final String window = req.getParameter( "window" );
        final String history = req.getParameter( "history" );

        final HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "baseUrl", getBaseUrl( req ) );

        if ( StringUtils.isNotBlank( systemInfo ) )
        {
            final SystemInfo systemInfoObject =
                systemInfoFactory.createSystemInfo( livePortalTraceService.getNumberOfPortalRequestTracesInProgress(), cacheManager );
            returnJson( livePortalTraceJsonGenerator.generate( systemInfoObject ), res );
        }
        else if ( "current".equals( window ) )
        {
            final List<PortalRequestTrace> traces = livePortalTraceService.getCurrentPortalRequestTraces();
            returnJson( livePortalTraceJsonGenerator.generate( PortalRequestTraceRow.createRows( traces ) ), res );
        }
        else if ( "longestpagerequests".equals( window ) )
        {
            final List<PortalRequestTrace> traces = livePortalTraceService.getLongestTimePortalPageRequestTraces();
            returnJson( livePortalTraceJsonGenerator.generate( PortalRequestTraceRow.createRows( traces ) ), res );
        }
        else if ( "longestattachmentrequests".equals( window ) )
        {
            final List<PortalRequestTrace> traces = livePortalTraceService.getLongestTimePortalAttachmentRequestTraces();
            returnJson( livePortalTraceJsonGenerator.generate( PortalRequestTraceRow.createRows( traces ) ), res );
        }
        else if ( "longestimagerequests".equals( window ) )
        {
            final List<PortalRequestTrace> traces = livePortalTraceService.getLongestTimePortalImageRequestTraces();
            returnJson( livePortalTraceJsonGenerator.generate( PortalRequestTraceRow.createRows( traces ) ), res );
        }
        else if ( history != null )
        {
            final String completedAfterStr = req.getParameter( "completed-after" );
            final String countStr = req.getParameter( "count" );
            final String completedBeforeStr = req.getParameter( "completed-before" );

            Long completedAfter = null;
            if ( !StringUtils.isEmpty( completedAfterStr ) )
            {
                completedAfter = Long.valueOf( completedAfterStr );
            }
            Integer count = null;
            if ( !StringUtils.isEmpty( countStr ) )
            {
                count = Integer.valueOf( countStr );
            }
            Long completedBefore = null;
            if ( !StringUtils.isEmpty( completedBeforeStr ) )
            {
                completedBefore = Long.valueOf( completedBeforeStr );
            }

            List<PortalRequestTrace> traces;
            if ( completedBefore != null )
            {
                traces = livePortalTraceService.getCompletedBefore( completedBefore );
            }
            else
            {
                traces = livePortalTraceService.getCompletedAfter( completedAfter );
            }

            if ( count != null )
            {
                traces = traces.subList( 0, Math.min( count, traces.size() ) );
            }

            final String jsonString = livePortalTraceJsonGenerator.generate( PortalRequestTraceRow.createRows( traces ) );
            returnJson( jsonString, res );
        }
        else
        {
            model.put( "livePortalTraceEnabled", isLivePortalTraceEnabled() ? 1 : 0 );
            res.setHeader( "Content-Type", "text/html; charset=utf-8" );
            renderView( req, res, model, "livePortalTracePage" );
        }

    }

    private void returnJson( String json, HttpServletResponse res )
        throws Exception
    {
        res.setHeader( "Content-Type", "application/json; charset=UTF-8" );
        res.getWriter().println( json );
    }

    private boolean isLivePortalTraceEnabled()
    {
        return livePortalTraceService.tracingEnabled();
    }

    @Autowired
    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    @Autowired
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }
}
