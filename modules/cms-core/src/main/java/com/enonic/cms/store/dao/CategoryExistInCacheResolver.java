package com.enonic.cms.store.dao;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;

class CategoryExistInCacheResolver
{
    private CacheFacade entityCache;

    private final static String cacheGroup = CategoryEntity.class.getName();

    CategoryExistInCacheResolver( CacheFacade entityCache )
    {
        this.entityCache = entityCache;
    }

    boolean categoryExistsInCache( final CategoryKey categoryKey )
    {
        final String cacheKey = cacheGroup + "#" + categoryKey.toString();
        final Object cacheEntry = entityCache.get( cacheGroup, cacheKey );
        return cacheEntry != null;
    }
}
