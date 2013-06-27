/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.base.AbstractCacheFacade;
import com.enonic.cms.framework.cache.base.AbstractCacheManager;
import com.enonic.cms.framework.cache.base.CacheConfig;

/**
 * This class implements the cache.
 */
@Component("cacheFacadeManager")
public final class StandardCacheManager
    extends AbstractCacheManager
{
    @Override
    protected AbstractCacheFacade doCreateCache( final CacheConfig config )
    {
        final StandardCache cache = new StandardCache( config.getMemoryCapacity() );
        return new StandardCacheFacade( cache );
    }
}
