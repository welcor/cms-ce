/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;

@Component("siteCachesService")
public class SiteCachesServiceImpl
    implements SiteCachesService
{
    private static final Logger LOG = LoggerFactory.getLogger( SiteCachesServiceImpl.class );

    private Map<SiteKey, PageCacheService> pageCacheServices = new LinkedHashMap<SiteKey, PageCacheService>();

    private PageCacheServiceFactory pageCacheServiceFactory;

    private SitePropertiesService sitePropertiesService;

    private CacheManager cacheManager;


    @Autowired
    public void setPageCacheServiceFactory( PageCacheServiceFactory value )
    {
        this.pageCacheServiceFactory = value;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    @Autowired
    public void setCacheManager( CacheManager value )
    {
        this.cacheManager = value;
    }

    public synchronized void setUpSiteCachesService( SiteKey siteKey )
    {
        setUpPageCacheService( siteKey );
    }

    public void tearDownSiteCachesService( SiteKey siteKey )
    {
        tearDownPageAndObjectCacheService( siteKey );
    }

    private void setUpPageCacheService( SiteKey siteKey )
    {
        boolean cacheEnabled = sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE, siteKey );

        PageCacheServiceImpl cacheService = (PageCacheServiceImpl) pageCacheServices.get( siteKey );
        if ( cacheService == null )
        {
            cacheService = pageCacheServiceFactory.createPageAndObjectCacheService( siteKey );
            pageCacheServices.put( siteKey, cacheService );
            LOG.info( "Page cache service is set up for site " + siteKey );
        }
        cacheService.setEnabled( cacheEnabled );
    }

    private void tearDownPageAndObjectCacheService( SiteKey siteKey )
    {
        pageCacheServices.remove( siteKey );

        LOG.info( "Page cache service is teared down for site " + siteKey );
    }

    public synchronized PageCacheService getPageCacheService( SiteKey siteKey )
    {
        Assert.notNull( siteKey, "Given siteKey cannot be null" );

        PageCacheService cacheService = (PageCacheService) pageCacheServices.get( siteKey );
        if ( cacheService == null )
        {
            setUpPageCacheService( siteKey );
            cacheService = (PageCacheService) pageCacheServices.get( siteKey );
        }
        return cacheService;
    }

    public void clearCaches( SiteKey siteKey )
    {
        getPageCacheService( siteKey ).clearCache();
    }

    public void clearCache( String cacheName )
    {
        CacheFacade cache = cacheManager.getCache( cacheName );
        if ( cache != null )
        {
            cache.removeAll();
        }
    }

    public void clearCacheStatistics( String cacheName )
    {
        CacheFacade cache = cacheManager.getCache( cacheName );
        if ( cache != null )
        {
            cache.clearStatistics();
        }
    }
}
