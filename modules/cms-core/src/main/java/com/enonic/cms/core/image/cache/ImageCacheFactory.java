/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.cache;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheManager;

@Component
public final class ImageCacheFactory
    implements FactoryBean<ImageCache>
{
    private CacheManager cacheManager;

    @Autowired
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    public ImageCache getObject()
    {
        return new WrappedImageCache( this.cacheManager.getOrCreateCache( "image" ) );
    }

    public Class getObjectType()
    {
        return ImageCache.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
