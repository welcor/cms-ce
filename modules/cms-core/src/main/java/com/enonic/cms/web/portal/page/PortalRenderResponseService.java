/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.SitePropertiesService;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.web.portal.SiteRedirectAndForwardHelper;
import com.enonic.cms.web.portal.instanttrace.InstantTraceRequestInspector;

/**
 * May 26, 2009
 */
@Component
public class PortalRenderResponseService
{
    private SitePropertiesService sitePropertiesService;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private SiteDao siteDao;

    private PluginManager pluginManager;

    private final OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    @Value("${cms.portal.encodeRedirectUrl}")
    private boolean encodeRedirectUrl;

    public void serveResponse( final PortalRequest request, final PortalResponse response, final HttpServletResponse httpResponse,
                               final HttpServletRequest httpRequest, final PortalRequestTrace portalRequestTrace )
        throws Exception
    {
        final SitePath requestedPath = request.getSitePath();
        final SiteKey requestedSiteKey = requestedPath.getSiteKey();
        final SiteEntity site = siteDao.findByKey( requestedSiteKey );

        final PortalResponseProcessor processor = new PortalResponseProcessor();
        processor.setSiteRedirectAndForwardHelper( siteRedirectAndForwardHelper );
        processor.setRequest( request );
        processor.setResponse( response );
        processor.setHttpRequest( httpRequest );
        processor.setHttpResponse( httpResponse );
        processor.setHttpSession( httpRequest.getSession( true ) );
        processor.setInPreview( "true".equals( httpRequest.getAttribute( Attribute.PREVIEW_ENABLED ) ) );
        processor.setRenderTraceOn( RenderTrace.isTraceOn() );
        processor.setDeviceClassificationEnabled( site.isDeviceClassificationEnabled() );
        processor.setLocalizationEnabled( site.isLocalizationEnabled() );
        processor.setForceNoCacheForSite(
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE_HEADERS_FORCENOCACHE, requestedSiteKey ) );
        processor.setEncodeRedirectUrl( encodeRedirectUrl );
        processor.setCacheHeadersEnabledForSite(
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE_HEADERS_ENABLED, requestedSiteKey ) );

        final String matchingPath = originalPathResolver.getRequestPathFromHttpRequest( httpRequest );
        processor.setResponseFilters( pluginManager.getExtensions().findMatchingHttpResponseFilters( matchingPath ) );
        processor.setInstantTraceEnabled( InstantTraceRequestInspector.isClientEnabled( httpRequest ) );
        processor.setCurrentPortalRequestTrace( portalRequestTrace );
        processor.serveResponse();
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper siteRedirectAndForwardHelper )
    {
        this.siteRedirectAndForwardHelper = siteRedirectAndForwardHelper;
    }

    @Autowired
    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired
    public void setPluginManager( PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }
}
