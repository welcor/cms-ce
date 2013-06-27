/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.portal.cache.PageCache;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.portal.datasource.executor.DataSourceExecutorFactory;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.rendering.viewtransformer.PortletXsltViewTransformer;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SitePropertiesService;

/**
 * Apr 20, 2009
 */
@Component
public class WindowRendererFactory
{
    @Autowired
    private PageCacheService pageCacheService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataSourceExecutorFactory datasourceExecutorFactory;

    @Autowired
    private PortletXsltViewTransformer portletXsltViewTransformer;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private MimeTypeResolver mimeTypeResolver;

    @Autowired
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    public WindowRenderer createPortletRenderer( WindowRendererContext windowRendererContext )
    {
        PageCache pageCache = pageCacheService.getPageCacheService( windowRendererContext.getSite().getKey() );

        WindowRenderer windowRenderer = new WindowRenderer( windowRendererContext );

        windowRenderer.setDataSourceExecutorFactory( datasourceExecutorFactory );
        windowRenderer.setPageCache( pageCache );
        windowRenderer.setPortletXsltViewTransformer( portletXsltViewTransformer );
        windowRenderer.setResourceService( resourceService );
        windowRenderer.setSiteURLResolver( siteURLResolver );
        windowRenderer.setMimeTypeResolver( mimeTypeResolver );
        windowRenderer.setSitePropertiesService( sitePropertiesService );
        windowRenderer.setPostProcessInstructionExecutor( postProcessInstructionExecutor );
        windowRenderer.setLiveTraceService( livePortalTraceService );

        return windowRenderer;
    }

}
