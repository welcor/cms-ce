/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

public abstract class AbstractSiteController
    extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger( AbstractSiteController.class );

    protected SiteService siteService;

    protected SiteDao siteDao;

    protected ContentDao contentDao;

    protected UserDao userDao;

    protected GroupDao groupDao;

    protected SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    protected PortalSitePathResolver sitePathResolver;

    protected SecurityService securityService;

    protected TimeService timeService;

    protected PreviewService previewService;

    protected AutoLoginService autoLoginService;

    protected SitePropertiesService sitePropertiesService;

    public AbstractSiteController()
    {
        setCacheSeconds( -1 );
    }

    public final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        // Get check and eventually set original sitePath
        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( originalSitePath == null )
        {
            originalSitePath = sitePathResolver.resolveSitePath( request );
            siteService.checkSiteExist( originalSitePath.getSiteKey() );
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, originalSitePath );
        }

        // Get and set the current sitePath
        SitePath currentSitePath = sitePathResolver.resolveSitePath( request );

        return logAndReturn( handleRequestInternal( request, response, currentSitePath ) );
    }

    private ModelAndView logAndReturn( ModelAndView modelAndView )
    {
        if ( modelAndView != null )
        {
            LOG.trace( modelAndView.getViewName() );
        }
        return modelAndView;
    }


    /**
     * Process the site path and return a {@link ModelAndView}.
     */
    protected abstract ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception;

    protected void enableHttpCacheHeaders( HttpServletResponse response, SitePath sitePath, DateTime now, Integer siteCacheSettingsMaxAge,
                                           boolean anonymousAccess )
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

    protected boolean hasTimestampParameter( SitePath sitePath )
    {
        String timestamp = sitePath.getParam( "_ts" );
        return StringUtils.isNotBlank( timestamp );
    }

    @Autowired
    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    @Autowired
    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    @Autowired
    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper value )
    {
        this.siteRedirectAndForwardHelper = value;
    }

    @Autowired
    public void setSitePathResolver( PortalSitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    @Autowired
    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }

    @Autowired
    public void setAutoLoginService( AutoLoginService autoLoginService )
    {
        this.autoLoginService = autoLoginService;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }
}
