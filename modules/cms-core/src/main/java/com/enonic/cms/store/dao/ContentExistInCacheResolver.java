/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.store.dao;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

class ContentExistInCacheResolver
{
    private CacheFacade entityCache;

    private final static String cacheGroup = ContentEntity.class.getName();

    ContentExistInCacheResolver( CacheFacade entityCache )
    {
        this.entityCache = entityCache;
    }

    boolean contentExistsInCache( final ContentKey contentKey )
    {
        final String cacheKey = cacheGroup + "#" + contentKey.toString();
        final Object cacheEntry = entityCache.get( cacheGroup, cacheKey );
        return cacheEntry != null;
    }
}
