/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.SiteNotFoundException;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

@Component("siteService")
public class SiteServiceImpl
    implements SiteService, SitePropertiesListener
{

    private static final Logger LOG = LoggerFactory.getLogger( SiteServiceImpl.class );

    private PageCacheService pageCacheService;

    private SiteContextManager siteContextManager;

    private SitePropertiesService sitePropertiesService;

    private SiteDao siteDao;

    private UserDao userDao;

    private final Object lock = new Object();

    @PostConstruct
    public void postConstruct()
    {
        sitePropertiesService.registerSitePropertiesListener( this );
    }

    @Override
    public void sitePropertiesLoaded( final SiteProperties siteProperties )
    {
        registerSite( siteProperties.getSiteKey() );
    }

    @Override
    public void sitePropertiesReloaded( final SiteProperties siteProperties )
    {
        final SiteContext siteContext = siteContextManager.getSiteContext( siteProperties.getSiteKey() );
        siteContext.setAuthenticationLoggingEnabled( siteProperties.getAuthenticationLoggingEnabled() );
    }

    private void registerSite( SiteKey siteKey )
    {

        synchronized ( lock )
        {
            if ( !siteContextManager.isRegistered( siteKey ) )
            {
                LOG.info( "Site [" + siteKey + "] is registering..." );
                SiteContext siteContext = createSiteContext( siteKey );
                siteContextManager.registerSiteContext( siteContext );

                LOG.info( "Site [" + siteKey + "] is registered" );
            }
        }
    }

    private void unregisterSite( SiteKey siteKey )
    {

        synchronized ( lock )
        {
            if ( siteContextManager.isRegistered( siteKey ) )
            {
                LOG.info( "Site [" + siteKey + "] is unregistering..." );

                siteContextManager.unregisterSiteContext( siteKey );
                pageCacheService.tearDownPageCache( siteKey );

                LOG.info( "Site [" + siteKey + "] is unregistered." );
            }
        }
    }

    private SiteContext createSiteContext( SiteKey siteKey )
    {
        final SiteContext siteContext = new SiteContext( siteKey );
        siteContext.setAuthenticationLoggingEnabled( sitePropertiesService.getSiteProperties( siteKey ).getAuthenticationLoggingEnabled() );

        pageCacheService.setUpPageCache( siteKey );

        return siteContext;
    }

    /**
     * @inheritDoc
     */
    public boolean siteExists( SiteKey siteKey )
    {
        return siteDao.findByKey( siteKey.toInt() ) != null;
    }

    /**
     * @inheritDoc
     */
    public void checkSiteExist( SiteKey siteKey )
        throws SiteNotFoundException
    {
        if ( !siteExists( siteKey ) )
        {
            throw new SiteNotFoundException( siteKey );
        }
    }

    /**
     * @inheritDoc
     */
    public SiteContext getSiteContext( SiteKey siteKey )
        throws SiteNotFoundException
    {

        SiteContext siteContext = siteContextManager.getSiteContext( siteKey );

        boolean siteExistsInDb = siteDao.findByKey( siteKey ) != null;
        boolean isRegistered = siteContext != null;

        if ( siteExistsInDb && isRegistered )
        {
            return siteContext;
        }

        if ( siteExistsInDb && !isRegistered )
        {
            registerSite( siteKey );
            return getSiteContext( siteKey );
        }

        if ( !siteExistsInDb && isRegistered )
        {
            unregisterSite( siteKey );
            throw new SiteNotFoundException( siteKey );
        }

        throw new SiteNotFoundException( siteKey );
    }

    public List<SiteEntity> getSitesToPublishTo( int contentTypeKey, User oldUser )
    {

        UserEntity user = userDao.findByKey( oldUser.getKey() );
        return siteDao.findByPublishPossible( contentTypeKey, user );
    }

    @Autowired
    public void setPageCacheService( PageCacheService value )
    {
        this.pageCacheService = value;
    }

    @Autowired
    public void setSiteContextManager( SiteContextManager value )
    {
        this.siteContextManager = value;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    @Autowired
    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    @Autowired
    public void setUserDao( UserDao value )
    {
        this.userDao = value;
    }
}
