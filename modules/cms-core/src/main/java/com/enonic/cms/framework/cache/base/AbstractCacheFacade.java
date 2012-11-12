package com.enonic.cms.framework.cache.base;

import java.util.concurrent.atomic.AtomicLong;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.event.CacheEventPublisher;

public abstract class AbstractCacheFacade
    implements CacheFacade
{
    private String name;

    private CacheConfig config;

    private final AtomicLong hitCount;

    private final AtomicLong missCount;

    private final AtomicLong removeAllCount;

    private CacheEventPublisher eventPublisher;

    public AbstractCacheFacade()
    {
        this.hitCount = new AtomicLong( 0 );
        this.missCount = new AtomicLong( 0 );
        this.removeAllCount = new AtomicLong( 0 );
    }

    @Override
    public final String getName()
    {
        return this.name;
    }

    @Override
    public final int getMemoryCapacity()
    {
        return this.config.getMemoryCapacity();
    }

    @Override
    public final int getTimeToLive()
    {
        return this.config.getTimeToLive();
    }

    @Override
    public final long getMissCount()
    {
        return this.missCount.get();
    }

    @Override
    public final long getHitCount()
    {
        return this.hitCount.get();
    }

    @Override
    public final int getMemoryCapacityUsage()
    {
        if ( getMemoryCapacity() == 0 )
        {
            return 0;
        }

        return 100 * getCount() / getMemoryCapacity();
    }

    @Override
    public final int getEffectiveness()
    {
        final long totalCount = getHitCount() + getMissCount();

        if ( totalCount == 0 )
        {
            return 100;
        }

        return (int) ( 100L * getHitCount() / totalCount );
    }

    @Override
    public long getRemoveAllCount()
    {
        return this.removeAllCount.get();
    }

    @Override
    public final void clearStatistics()
    {
        this.hitCount.set( 0 );
        this.missCount.set( 0 );
    }

    @Override
    public final Object get( final String group, final String key )
    {
        final String compositeKey = createCompositeKey( group, key );
        final Object value = doGet( compositeKey );

        if ( value != null )
        {
            this.hitCount.incrementAndGet();
        }
        else
        {
            this.missCount.incrementAndGet();
        }

        return value;
    }

    @Override
    public final void put( final String group, final String key, final Object value )
    {
        put( group, key, value, -1 );
    }

    @Override
    public final void put( final String group, final String key, final Object value, final int timeToLive )
    {
        if ( value == null )
        {
            remove( group, key );
        }
        else
        {
            final int realTimeToLive = timeToLive < 0 ? this.config.getTimeToLive() : timeToLive;
            String compositeKey = createCompositeKey( group, key );
            doPut( compositeKey, value, realTimeToLive );
        }
    }

    @Override
    public final void remove( String group, String key )
    {
        String compositeKey = createCompositeKey( group, key );
        doRemove( compositeKey );
        this.eventPublisher.publishEvictByKey( this.name, compositeKey );
    }

    @Override
    public final void removeGroup( String group )
    {
        doRemoveGroup( group );
        this.eventPublisher.publishEvictByGroup( this.name, group );
    }

    @Override
    public final void removeGroupByPrefix( String prefix )
    {
        doRemoveGroupByPrefix( prefix );
        this.eventPublisher.publishEvictByPrefix( this.name, prefix );
    }

    @Override
    public final void removeAll()
    {
        doRemoveAll();

        removeAllCount.incrementAndGet();
        clearStatistics();

        this.eventPublisher.publishEvictAll( this.name );
    }

    private String createCompositeKey( final String group, final String key )
    {
        if ( group != null )
        {
            return group + ":" + key;
        }
        else
        {
            return key;
        }
    }

    protected abstract Object doGet( String compositeKey );

    protected abstract void doPut( String compositeKey, Object value, int timeToLive );

    protected abstract void doRemove( String compositeKey );

    protected abstract void doRemoveGroup( String groupName );

    protected abstract void doRemoveGroupByPrefix( String prefix );

    protected abstract void doRemoveAll();

    public final void setName( final String name )
    {
        this.name = name;
    }

    public final void setConfig( final CacheConfig config )
    {
        this.config = config;
    }

    protected final void setCacheEventPublisher( final CacheEventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
