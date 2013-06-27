/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

/**
 * This class implements the bootstrap for hibernate cache. It finds the cache manager to use and holds it in a static way.
 */
public final class HibernateCacheBootstrap
{
    /**
     * Instance.
     */
    private static HibernateCacheBootstrap INSTANCE;

    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * Construct.
     */
    public HibernateCacheBootstrap()
    {
        INSTANCE = this;
    }

    /**
     * Set the cache manager.
     */
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    /**
     * Return the cache.
     */
    public CacheFacade getCache()
    {
        return this.cacheManager.getEntityCache();
    }

    /**
     * Return the cache manager.
     */
    public static HibernateCacheBootstrap getInstance()
    {
        return INSTANCE;
    }
}
