/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.TightestCacheSettingsResolver;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.cache.SiteCachesService;
import com.enonic.cms.core.portal.datasource.executor.DataSourceExecutorFactory;
import com.enonic.cms.core.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.rendering.viewtransformer.PageTemplateXsltViewTransformer;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.time.TimeService;

@Component
public class PageRendererFactory
{
    @Autowired
    @Qualifier("siteCachesService")
    private SiteCachesService siteCachesService;

    @Autowired
    private DataSourceExecutorFactory datasourceExecutorFactory;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PageTemplateXsltViewTransformer pageTemplateXsltViewTransformer;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private TightestCacheSettingsResolver tightestCacheSettingsResolver;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private PluginManager pluginManager;

    public PageRenderer createPageRenderer( PageRendererContext pageRendererContext )
    {
        PageRenderer pageRenderer = new PageRenderer( pageRendererContext, livePortalTraceService );

        pageRenderer.setDataSourceExecutorFactory( datasourceExecutorFactory );
        pageRenderer.setPageTemplateXsltViewTransformer( pageTemplateXsltViewTransformer );
        pageRenderer.setResourceService( resourceService );
        pageRenderer.setPageCacheService( siteCachesService.getPageCacheService( pageRendererContext.getSite().getKey() ) );
        pageRenderer.setSiteURLResolver( siteURLResolver );
        pageRenderer.setSitePropertiesService( sitePropertiesService );
        pageRenderer.setTightestCacheSettingsResolver( tightestCacheSettingsResolver );
        pageRenderer.setTimeService( timeService );
        pageRenderer.setPostProcessInstructionExecutor( postProcessInstructionExecutor );
        pageRenderer.setDataSourceService( dataSourceService );
        pageRenderer.setPluginManager( pluginManager );

        return pageRenderer;
    }
}
