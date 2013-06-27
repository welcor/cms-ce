/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.framework.cache.event;

public interface CacheEventHandler
{
    public void handleEvictByKey( String cacheName, String key );

    public void handleEvictByGroup( String cacheName, String group );

    public void handleEvictByPrefix( String cacheName, String prefix );

    public void handleEvictAll( String cacheName );
}
