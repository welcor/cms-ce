/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


public class CacheUsage
{
    static final int CONCURRENCY_BLOCK_THRESHOLD = 5;

    private Boolean cacheable = false;

    private Boolean usedCachedResult = false;

    private long concurrencyBlockStartTime = 0;

    private long concurrencyBlockingTime = 0;

    public boolean isCacheable()
    {
        return cacheable;
    }

    CacheUsage setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
        return this;
    }

    public boolean isUsedCachedResult()
    {
        return usedCachedResult;
    }

    public void setUsedCachedResult( boolean value )
    {
        this.usedCachedResult = value;
    }

    public boolean isConcurrencyBlocked()
    {
        return concurrencyBlockingTime > CONCURRENCY_BLOCK_THRESHOLD;
    }

    public long getConcurrencyBlockingTime()
    {
        return isConcurrencyBlocked() ? concurrencyBlockingTime : 0;
    }

    void startConcurrencyBlockTimer()
    {
        concurrencyBlockStartTime = System.currentTimeMillis();
    }

    void stopConcurrencyBlockTimer()
    {
        this.concurrencyBlockingTime = System.currentTimeMillis() - concurrencyBlockStartTime;
    }

    public boolean isWorseThan( CacheUsage other )
    {
        if ( !cacheable && other.isCacheable() )
        {
            return true;
        }
        else if ( cacheable && !other.isCacheable() )
        {
            return false;
        }
        else if ( isConcurrencyBlocked() && !other.isConcurrencyBlocked() )
        {
            return true;
        }
        else if ( !isConcurrencyBlocked() && other.isConcurrencyBlocked() )
        {
            return false;
        }

        return concurrencyBlockingTime > other.getConcurrencyBlockingTime();
    }
}
