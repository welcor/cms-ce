/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

import java.util.HashMap;
import java.util.LinkedHashMap;

class PageCacheUsagesResolver
{
    static CacheUsages resolveCacheUsages( final PageRenderingTrace pageRenderingTrace )
    {
        HashMap<String, WindowRenderingTrace> collection = new LinkedHashMap<String, WindowRenderingTrace>();

        if ( pageRenderingTrace.getViewTransformationTrace() != null )
        {
            collectWindowRenderingTracesInViewTransformationTrace( pageRenderingTrace.getViewTransformationTrace(), collection );
        }

        if ( pageRenderingTrace.getWindowRenderingTraces() != null )
        {
            collectWindowRenderingTraces( pageRenderingTrace.getWindowRenderingTraces(), collection );
        }

        final CacheUsages resolvedCacheUsages = new CacheUsages();
        resolvedCacheUsages.add( pageRenderingTrace.getCacheUsage() );
        for ( WindowRenderingTrace trace : collection.values() )
        {
            resolvedCacheUsages.add( trace.getCacheUsage() );
        }

        return resolvedCacheUsages;
    }

    private static void collectWindowRenderingTraces( final Traces<WindowRenderingTrace> traces,
                                                      final HashMap<String, WindowRenderingTrace> collection )
    {
        for ( WindowRenderingTrace trace : traces )
        {
            collectWindowRenderingTrace( trace, collection );
        }
    }

    private static void collectWindowRenderingTracesInViewTransformationTrace( final ViewTransformationTrace viewTransformationTrace,
                                                                               final HashMap<String, WindowRenderingTrace> collection )
    {
        if ( viewTransformationTrace.getViewFunctionTraces() != null )
        {
            for ( ViewFunctionTrace viewFunctionTrace : viewTransformationTrace.getViewFunctionTraces() )
            {
                for ( Trace trace : viewFunctionTrace.getTraces() )
                {
                    if ( trace instanceof WindowRenderingTrace )
                    {
                        collectWindowRenderingTrace( (WindowRenderingTrace) trace, collection );
                    }
                }
            }
        }
    }

    private static void collectWindowRenderingTrace( final WindowRenderingTrace windowRenderingTrace,
                                                     final HashMap<String, WindowRenderingTrace> collection )
    {
        final WindowRenderingTrace existingTrace = collection.get( windowRenderingTrace.getWindowKey() );
        if ( existingTrace != null )
        {
            if ( windowRenderingTrace.getCacheUsage().isWorseThan( existingTrace.getCacheUsage() ) )
            {
                collection.put( windowRenderingTrace.getWindowKey(), windowRenderingTrace );
            }
        }
        else
        {
            collection.put( windowRenderingTrace.getWindowKey(), windowRenderingTrace );
        }
    }
}

