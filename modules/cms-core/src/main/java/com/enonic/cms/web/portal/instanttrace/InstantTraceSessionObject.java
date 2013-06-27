/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;


import java.util.LinkedHashMap;

import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;

public class InstantTraceSessionObject
{
    private final static int DEFAULT_MAX_TRACES = 50;

    private int maxTraces = DEFAULT_MAX_TRACES;

    private LinkedHashMap<InstantTraceId, PortalRequestTrace> traceById = new LinkedHashMap<InstantTraceId, PortalRequestTrace>();

    private InstantTraceId oldest;

    public InstantTraceSessionObject()
    {

    }

    public InstantTraceSessionObject( final int maxTraces )
    {
        this.maxTraces = maxTraces;
    }

    public void addTrace( final InstantTraceId instantTraceId, final PortalRequestTrace portalRequestTrace )
    {
        if ( traceById.size() == 0 )
        {
            oldest = instantTraceId;
        }

        if ( traceById.size() > maxTraces - 1 )
        {
            traceById.remove( oldest );
        }
        traceById.put( instantTraceId, portalRequestTrace );
    }

    public PortalRequestTrace getTrace( final InstantTraceId instantTraceId )
    {
        return traceById.get( instantTraceId );
    }

}
