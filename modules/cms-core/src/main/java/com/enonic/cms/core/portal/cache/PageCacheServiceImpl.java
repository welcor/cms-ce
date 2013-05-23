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

import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;

@Component("siteCachesService")
public class PageCacheServiceImpl
    implements PageCacheService
{
    private static final Logger LOG = LoggerFactory.getLogger( PageCacheServiceImpl.class );

    private Map<SiteKey, PageCache> pageCaches = new LinkedHashMap<SiteKey, PageCache>();

    private SitePropertiesService sitePropertiesService;

    private CacheManager cacheManager;


    public synchronized void setUpPageCache( final SiteKey siteKey )
    {
        doSetupPageCache( siteKey );
    }

    private void doSetupPageCache( final SiteKey siteKey )
    {
        PageCache pageCache = pageCaches.get( siteKey );
        if ( pageCache == null )
        {
            pageCache = createPageCache( siteKey );
            pageCaches.put( siteKey, pageCache );
            LOG.info( "Page cache is set up for site " + siteKey );
        }
        else
        {
            pageCache.setDefaultTimeToLive(
                sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey ) );
            pageCache.setEnabled( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE, siteKey ) );
        }
    }

    public synchronized void reloadPageCacheConfig( final SiteKey siteKey )
    {
        final PageCache pageCache = pageCaches.get( siteKey );
        if ( pageCache != null )
        {
            pageCache.setDefaultTimeToLive(
                sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey ) );
            pageCache.setEnabled( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE, siteKey ) );
        }
    }

    private PageCache createPageCache( final SiteKey siteKey )
    {
        final PageCache pageCache = new PageCache( siteKey, cacheManager.getPageCache() );
        pageCache.setDefaultTimeToLive( sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey ) );
        pageCache.setEnabled( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE, siteKey ) );
        return pageCache;
    }

    public synchronized PageCache getPageCacheService( final SiteKey siteKey )
    {
        Assert.notNull( siteKey, "Given siteKey cannot be null" );

        PageCache cacheService = pageCaches.get( siteKey );
        if ( cacheService == null )
        {
            doSetupPageCache( siteKey );
            cacheService = pageCaches.get( siteKey );
        }
        return cacheService;
    }

    public synchronized void tearDownPageCache( final SiteKey siteKey )
    {
        pageCaches.remove( siteKey );
        LOG.info( "Page cache is teared down for site " + siteKey );
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
}
