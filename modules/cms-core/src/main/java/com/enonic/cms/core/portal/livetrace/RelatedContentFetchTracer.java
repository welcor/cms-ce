/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import com.enonic.cms.core.content.RelatedContentFetcher;

public class RelatedContentFetchTracer
{

    public static RelatedContentFetchTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        if ( livePortalTraceService.tracingEnabled() )
        {
            return livePortalTraceService.startRelatedContentFetchTracing();
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( final RelatedContentFetchTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceParentsFetch( final int level, final int count, final RelatedContentFetchTrace trace )
    {
        if ( trace != null )
        {
            trace.setParentFetch( level, count );
        }
    }

    public static void traceChildrenFetch( final int level, final int count, final RelatedContentFetchTrace trace )
    {
        if ( trace != null )
        {
            trace.setChildrenFetch( level, count );
        }
    }


    public static void traceDefinition( final RelatedContentFetcher fetcher, final RelatedContentFetchTrace trace )
    {
        if ( trace != null )
        {
            trace.setMaxParentLevel( fetcher.getMaxParentLevel() );
            trace.setMaxChildrenLevel( fetcher.getMaxChildrenLevel() );
        }
    }
}
