package com.enonic.cms.core.portal.livetrace;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;


public class PortalRequestTraceRow
{
    private PortalRequestTrace portalRequestTrace;

    private long completedNumber;

    private String type;

    private String url;

    private String started;

    private Duration duration;

    public PortalRequestTraceRow( final PortalRequestTrace portalRequestTrace )
    {
        Preconditions.checkNotNull( portalRequestTrace );

        this.portalRequestTrace = portalRequestTrace;
        this.completedNumber = portalRequestTrace.getCompletedNumber();
        this.type = portalRequestTrace.getType();
        this.url = resolveURL( portalRequestTrace );
        this.started = portalRequestTrace.getDuration().getStartTime().toString();
        this.duration = portalRequestTrace.getDuration();
    }

    @SuppressWarnings("UnusedDeclaration")
    public long getCompletedNumber()
    {
        return completedNumber;
    }

    public String getType()
    {
        return type;
    }

    public String getUrl()
    {
        return url;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getStarted()
    {
        return started;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Duration getDuration()
    {
        return duration;
    }

    @SuppressWarnings("UnusedDeclaration")
    public PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }

    private String resolveURL( final PortalRequestTrace portalRequestTrace )
    {
        final StringBuilder s = new StringBuilder();
        s.append( portalRequestTrace.getSiteName() != null ? portalRequestTrace.getSiteName() : "?" );
        s.append( portalRequestTrace.getSiteLocalUrl() != null ? portalRequestTrace.getSiteLocalUrl() : "?" );
        return s.toString();
    }

    public static List<PortalRequestTraceRow> createRows( final List<PortalRequestTrace> portalRequestTraces )
    {
        final List<PortalRequestTraceRow> rows = new ArrayList<PortalRequestTraceRow>();
        for ( PortalRequestTrace trace : portalRequestTraces )
        {
            rows.add( new PortalRequestTraceRow( trace ) );
        }
        return rows;
    }

}
