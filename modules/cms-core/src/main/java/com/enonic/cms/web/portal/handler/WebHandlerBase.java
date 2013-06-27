/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.handler;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.SitePropertiesService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.web.portal.PortalWebContext;

public abstract class WebHandlerBase
    implements WebHandler
{
    protected TimeService timeService;

    protected SecurityService securityService;

    protected PreviewService previewService;

    protected LivePortalTraceService livePortalTraceService;

    protected SiteDao siteDao;

    protected SitePropertiesService sitePropertiesService;

    protected AutoLoginService autoLoginService;

    protected ContentDao contentDao;

    protected GroupDao groupDao;

    protected UserDao userDao;

    protected SiteService siteService;

    @Override
    public final boolean canHandle( final PortalWebContext context )
    {
        return canHandle( context.getSitePath().getLocalPath() );
    }

    protected abstract boolean canHandle( final Path localPath );

    @Override
    public final void handle( final PortalWebContext context )
        throws Exception
    {
        final SitePath originalSitePath = context.getOriginalSitePath();
        if ( originalSitePath == null )
        {
            siteService.checkSiteExist( context.getSitePath().getSiteKey() );
            context.setOriginalSitePath( context.getSitePath() );
        }

        doHandle( context );
    }

    protected abstract void doHandle( final PortalWebContext context )
        throws Exception;

    @Autowired
    public final void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public final void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Autowired
    public final void setLivePortalTraceService( final LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    @Autowired
    public final void setPreviewService( final PreviewService previewService )
    {
        this.previewService = previewService;
    }

    @Autowired
    public final void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired
    public final void setSitePropertiesService( final SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public final void setAutoLoginService( final AutoLoginService autoLoginService )
    {
        this.autoLoginService = autoLoginService;
    }

    @Autowired
    public final void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public final void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public final void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public final void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    protected final void enableHttpCacheHeaders( HttpServletResponse response, SitePath sitePath, DateTime now,
                                                 Integer siteCacheSettingsMaxAge, boolean anonymousAccess )
    {
        int maxAge;

        boolean cacheForever = hasTimestampParameter( sitePath );

        if ( cacheForever )
        {
            maxAge = HttpCacheControlSettings.CACHE_FOREVER_SECONDS;
        }
        else
        {
            maxAge = siteCacheSettingsMaxAge;
        }

        final DateTime expirationTime = now.plusSeconds( maxAge );

        final HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();
        cacheControlSettings.maxAgeSecondsToLive = (long) maxAge;
        cacheControlSettings.publicAccess = anonymousAccess;
        HttpServletUtil.setExpiresHeader( response, expirationTime.toDate() );
        HttpServletUtil.setCacheControl( response, cacheControlSettings );
    }

    protected final boolean hasTimestampParameter( SitePath sitePath )
    {
        String timestamp = sitePath.getParam( "_ts" );
        return StringUtils.isNotBlank( timestamp );
    }
}
