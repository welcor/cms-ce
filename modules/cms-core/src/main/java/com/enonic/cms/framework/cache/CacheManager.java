/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import com.enonic.cms.framework.xml.XMLDocument;

/**
 * This interface defines the cache manager.
 */
public interface CacheManager
{
    public Iterable<CacheFacade> getAll();

    public CacheFacade getCache( String name );

    public CacheFacade getEntityCache();

    public CacheFacade getImageCache();

    public CacheFacade getLocalizationCache();

    public CacheFacade getPageCache();

    public CacheFacade getXsltCache();
}
