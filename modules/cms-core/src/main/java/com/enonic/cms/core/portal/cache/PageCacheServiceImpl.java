/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesListener;
import com.enonic.cms.core.structure.SitePropertiesService;

@Component("pageCacheService")
public class PageCacheServiceImpl
    implements PageCacheService, SitePropertiesListener
{
    private static final Logger LOG = LoggerFactory.getLogger( PageCacheServiceImpl.class );

    private Map<SiteKey, PageCache> pageCaches = new LinkedHashMap<SiteKey, PageCache>();

    private SitePropertiesService sitePropertiesService;

    private CacheManager cacheManager;


    @PostConstruct
    public void postConstruct()
    {
        sitePropertiesService.registerSitePropertiesListener( this );
    }

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
            final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( siteKey );
            pageCache.setEnabled( siteProperties.getPageCacheEnabled() );
            pageCache.setDefaultTimeToLive( siteProperties.getPageCacheTimeToLive() );
        }
    }

    @Override
    public void sitePropertiesLoaded( final SiteProperties siteProperties )
    {
        // nothing
    }

    @Override
    public void sitePropertiesReloaded( final SiteProperties siteProperties )
    {
        final PageCache pageCache = pageCaches.get( siteProperties.getSiteKey() );
        if ( pageCache != null )
        {
            pageCache.setEnabled( siteProperties.getPageCacheEnabled() );
            pageCache.setDefaultTimeToLive( siteProperties.getPageCacheTimeToLive() );
        }
    }

    private PageCache createPageCache( final SiteKey siteKey )
    {
        final PageCache pageCache = new PageCache( siteKey, cacheManager.getPageCache() );
        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( siteKey );
        pageCache.setDefaultTimeToLive( siteProperties.getPageCacheTimeToLive() );
        pageCache.setEnabled( siteProperties.getPageCacheEnabled() );
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
