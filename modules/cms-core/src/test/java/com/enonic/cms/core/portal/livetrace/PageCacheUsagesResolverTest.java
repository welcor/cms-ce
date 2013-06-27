/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import org.junit.Test;

import static org.junit.Assert.*;

public class PageCacheUsagesResolverTest
{
    @Test
    public void when_page_has_cacheUsage_but_no_windows_then_cacheUsage_from_page_is_resolved()
    {
        //  setup
        PageRenderingTrace pageRenderingTrace = new PageRenderingTrace();
        pageRenderingTrace.getCacheUsage().setCacheable( true );
        pageRenderingTrace.getCacheUsage().setUsedCachedResult( false );

        // exercise
        CacheUsages cacheUsages = PageCacheUsagesResolver.resolveCacheUsages( pageRenderingTrace );

        // verify
        assertEquals( 1, cacheUsages.getList().size() );
        assertEquals( true, cacheUsages.getList().get( 0 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 0 ).isUsedCachedResult() );
    }

    @Test
    public void when_page_has_cacheUsage_and_windows_too_then_cacheUsage_from_page_and_windows_is_resolved()
    {
        // setup
        PageRenderingTrace pageRenderingTrace = new PageRenderingTrace();
        pageRenderingTrace.getCacheUsage().setCacheable( true );
        pageRenderingTrace.getCacheUsage().setUsedCachedResult( false );

        WindowRenderingTrace windowRenderingTrace1 = new WindowRenderingTrace( "1:1" );
        windowRenderingTrace1.getCacheUsage().setCacheable( true );
        windowRenderingTrace1.getCacheUsage().setUsedCachedResult( false );
        pageRenderingTrace.addWindowRenderingTrace( windowRenderingTrace1 );

        WindowRenderingTrace windowRenderingTrace2 = new WindowRenderingTrace( "1:2" );
        windowRenderingTrace2.getCacheUsage().setCacheable( true );
        windowRenderingTrace2.getCacheUsage().setUsedCachedResult( false );
        pageRenderingTrace.addWindowRenderingTrace( windowRenderingTrace2 );

        // exercise
        CacheUsages cacheUsages = PageCacheUsagesResolver.resolveCacheUsages( pageRenderingTrace );

        // verify
        assertEquals( 3, cacheUsages.getList().size() );

        assertEquals( true, cacheUsages.getList().get( 0 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 0 ).isUsedCachedResult() );

        assertEquals( true, cacheUsages.getList().get( 1 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 1 ).isUsedCachedResult() );

        assertEquals( true, cacheUsages.getList().get( 2 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 2 ).isUsedCachedResult() );
    }

    @Test
    public void when_page_has_window_rendering_in_view_transformation_trace()
    {
        // setup
        PageRenderingTrace pageRenderingTrace = new PageRenderingTrace();
        pageRenderingTrace.getCacheUsage().setCacheable( true );
        pageRenderingTrace.getCacheUsage().setUsedCachedResult( false );

        WindowRenderingTrace windowRenderingTrace1 = new WindowRenderingTrace( "1:1" );
        windowRenderingTrace1.getCacheUsage().setCacheable( true );
        windowRenderingTrace1.getCacheUsage().setUsedCachedResult( false );
        ViewFunctionTrace viewFunctionTrace = new ViewFunctionTrace();
        viewFunctionTrace.addTrace( windowRenderingTrace1 );
        ViewTransformationTrace viewTransformationTrace = new ViewTransformationTrace();
        viewTransformationTrace.addViewFunctionTrace( viewFunctionTrace );
        pageRenderingTrace.setViewTransformationTrace( viewTransformationTrace );

        windowRenderingTrace1 = new WindowRenderingTrace( "1:1" );
        windowRenderingTrace1.getCacheUsage().setCacheable( true );
        windowRenderingTrace1.getCacheUsage().setUsedCachedResult( true );
        pageRenderingTrace.addWindowRenderingTrace( windowRenderingTrace1 );

        WindowRenderingTrace windowRenderingTrace2 = new WindowRenderingTrace( "1:2" );
        windowRenderingTrace2.getCacheUsage().setCacheable( true );
        windowRenderingTrace2.getCacheUsage().setUsedCachedResult( false );
        pageRenderingTrace.addWindowRenderingTrace( windowRenderingTrace2 );

        // exercise
        CacheUsages cacheUsages = PageCacheUsagesResolver.resolveCacheUsages( pageRenderingTrace );

        // verify
        assertEquals( 3, cacheUsages.getList().size() );

        assertEquals( true, cacheUsages.getList().get( 0 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 0 ).isUsedCachedResult() );

        assertEquals( true, cacheUsages.getList().get( 1 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 1 ).isUsedCachedResult() );

        assertEquals( true, cacheUsages.getList().get( 2 ).isCacheable() );
        assertEquals( false, cacheUsages.getList().get( 2 ).isUsedCachedResult() );
    }
}
