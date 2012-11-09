/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;

@Component
public class PageCacheServiceFactory
{
    private CacheManager cacheManager;

    private SitePropertiesService sitePropertiesService;

    @Autowired
    public void setCacheManager( CacheManager value )
    {
        this.cacheManager = value;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public PageCacheServiceImpl createPageAndObjectCacheService( SiteKey siteKey )
    {

        CacheFacade cacheFacade = cacheManager.getPageCache();

        PageCacheServiceImpl cacheService = new PageCacheServiceImpl( siteKey );
        cacheService.setCacheFacade( cacheFacade );

        Integer defaultTimeToLive = sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey );
        cacheService.setTimeToLive( defaultTimeToLive );
        return cacheService;
    }
}
