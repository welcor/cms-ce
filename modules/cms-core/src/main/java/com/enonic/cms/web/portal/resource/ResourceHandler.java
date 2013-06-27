/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.resource;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertyNames;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class ResourceHandler
    extends WebHandlerBase
{
    private ResourceService resourceService;

    @Override
    protected boolean canHandle( final Path localPath )
    {
        return localPath.containsSubPath( "_public" ) || localPath.containsSubPath( "~" );
    }

    @Autowired
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final SitePath sitePath = context.getSitePath();
        final HttpServletResponse response = context.getResponse();

        String sitePublicHome = null;

        final ResourceKey publicPath = siteDao.findByKey( sitePath.getSiteKey().toInt() ).getPathToPublicResources();
        if ( publicPath != null )
        {
            sitePublicHome = publicPath.toString();
        }

        final ResourceKeyResolver resourceKeyResolverForSiteLocalResources = new ResourceKeyResolver( sitePublicHome );

        final ResourceKey resourceKey = resourceKeyResolverForSiteLocalResources.resolveResourceKey( sitePath );
        final ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );

        if ( resourceFile == null )
        {
            // this is to prevent redirect loop
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }

        setHttpHeaders( response, sitePath );

        response.setContentType( resourceFile.getMimeType() );
        response.setContentLength( (int) resourceFile.getSize() );

        HttpServletUtil.copyNoCloseOut( resourceFile.getDataAsInputStream(), response.getOutputStream() );
    }

    private void setHttpHeaders( final HttpServletResponse response, final SitePath sitePath )
    {
        final DateTime now = new DateTime();
        HttpServletUtil.setDateHeader( response, now.toDate() );

        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( sitePath.getSiteKey() );
        final boolean cacheHeadersEnabled = siteProperties.getPropertyAsBoolean( SitePropertyNames.RESOURCE_CACHE_HEADERS_ENABLED );
        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache = siteProperties.getPropertyAsBoolean( SitePropertyNames.RESOURCE_CACHE_HEADERS_FORCENOCACHE );
            if ( forceNoCache )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge = siteProperties.getPropertyAsInteger( SitePropertyNames.RESOURCE_CACHE_HEADERS_MAXAGE );

                enableHttpCacheHeaders( response, sitePath, now, siteCacheSettingsMaxAge, true );

            }
        }
    }
}
