/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.base;

/**
 * This class implements the cache configuration.
 */
public final class CacheConfig
{
    /**
     * Memory capacity.
     */
    private final int memoryCapacity;

    /**
     * Time to live. 0 is eternal;
     */
    private final int timeToLive;

    public CacheConfig( final int memoryCapacity, final int timeToLive )
    {
        this.memoryCapacity = memoryCapacity;
        this.timeToLive = timeToLive;
    }

    public int getMemoryCapacity()
    {
        return this.memoryCapacity;
    }

    public int getTimeToLive()
    {
        return this.timeToLive;
    }
}
