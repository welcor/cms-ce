package com.enonic.cms.framework.cache.base;

import java.util.concurrent.atomic.AtomicLong;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

public abstract class AbstractCacheFacade
    implements CacheFacade
{
    private String name;

    private CacheConfig config;

    private final AtomicLong hitCount;

    private final AtomicLong missCount;

    private final AtomicLong removeAllCount;

    private CacheClusterSender clusterSender;

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

    /**
     * @inherit
     */
    public int getMemoryCapacityUsage()
    {
        if ( getMemoryCapacity() == 0 )
        {
            return -1;
        }

        return 100 * getCount() / getMemoryCapacity();
    }

    /**
     * @inherit
     */
    public int getEffectiveness()
    {
        final long totalCount = getHitCount() + getMissCount();

        if ( totalCount == 0 )
        {
            return -1;
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
        this.clusterSender.sendEvictMessage( this.name, compositeKey );
    }

    @Override
    public final void removeGroup( String group )
    {
        doRemoveGroup( group );
        this.clusterSender.sendEvictGroupMessage( this.name, group );
    }

    @Override
    public final void removeGroupByPrefix( String prefix )
    {
        doRemoveGroupByPrefix( prefix );
        this.clusterSender.sendEvictByGroupPrefixMessage( this.name, prefix );
    }

    @Override
    public final void removeAll()
    {
        doRemoveAll();

        removeAllCount.incrementAndGet();
        clearStatistics();

        this.clusterSender.sendEvictAllMessage( this.name );
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

    public abstract Object doGet( String compositeKey );

    public abstract void doPut( String compositeKey, Object value, int timeToLive );

    public abstract void doRemove( String compositeKey );

    public abstract void doRemoveGroup( String groupName );

    public abstract void doRemoveGroupByPrefix( String prefix );

    public abstract void doRemoveAll();

    @Override
    public final XMLDocument getInfoAsXml()
    {
        final Element root = new Element( "cache" );

        root.setAttribute( "name", getName() );
        root.setAttribute( "implementationName", "Standard Cache" );
        root.setAttribute( "memoryCapacity", String.valueOf( getMemoryCapacity() ) );
        root.setAttribute( "timeToLive", String.valueOf( getTimeToLive() ) );

        final Element statsElem = new Element( "statistics" );
        statsElem.setAttribute( "objectCount", String.valueOf( getCount() ) );
        statsElem.setAttribute( "memoryCapacityUsage", String.valueOf( getMemoryCapacityUsage() ) );
        statsElem.setAttribute( "cacheHits", String.valueOf( getHitCount() ) );
        statsElem.setAttribute( "cacheMisses", String.valueOf( getMissCount() ) );
        statsElem.setAttribute( "cacheEffectiveness", String.valueOf( getEffectiveness() ) );
        statsElem.setAttribute( "cacheClears", String.valueOf( getRemoveAllCount() ) );

        root.addContent( statsElem );
        return XMLDocumentFactory.create( new Document( root ) );
    }

    public final void setName( final String name )
    {
        this.name = name;
    }

    public final void setConfig( final CacheConfig config )
    {
        this.config = config;
    }

    public final void setClusterSender( final CacheClusterSender clusterSender )
    {
        this.clusterSender = clusterSender;
    }
}
