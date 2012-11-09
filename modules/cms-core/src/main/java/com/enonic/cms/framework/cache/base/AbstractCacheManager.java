package com.enonic.cms.framework.cache.base;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.cache.event.CacheEventHandler;
import com.enonic.cms.framework.cache.event.CacheEventHandlerAdapter;
import com.enonic.cms.framework.cache.event.CacheEventPublisherAdapter;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventListener;
import com.enonic.cms.core.cluster.ClusterEventPublisher;
import com.enonic.cms.core.cluster.NopClusterEventPublisher;
import com.enonic.cms.core.config.ConfigProperties;

public abstract class AbstractCacheManager
    implements CacheManager, ClusterEventListener, CacheEventHandler, InitializingBean
{
    private final Map<String, AbstractCacheFacade> cacheMap;

    private final CacheEventHandlerAdapter cacheEventHandlerAdapter;

    private CacheEventPublisherAdapter cacheEventPublisher;

    private CacheManagerConfig config;

    public AbstractCacheManager()
    {
        this.cacheMap = Maps.newLinkedHashMap();
        this.cacheEventHandlerAdapter = new CacheEventHandlerAdapter( this );
        this.cacheEventPublisher = new CacheEventPublisherAdapter( new NopClusterEventPublisher() );
    }

    @Override
    public final Iterable<CacheFacade> getAll()
    {
        final ImmutableList.Builder<CacheFacade> builder = ImmutableList.builder();
        for ( final CacheFacade facade : this.cacheMap.values() )
        {
            builder.add( facade );
        }

        return builder.build();
    }

    @Override
    public final void handleEvictByKey( final String cacheName, final String key )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemove( key );
        }
    }

    @Override
    public final void handleEvictByGroup( final String cacheName, final String group )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveGroup( group );
        }
    }

    @Override
    public final void handleEvictByPrefix( final String cacheName, final String prefix )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveGroupByPrefix( prefix );
        }
    }

    @Override
    public final void handleEvictAll( final String cacheName )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveAll();
        }
    }

    @Override
    public final void handle( final ClusterEvent event )
    {
        this.cacheEventHandlerAdapter.handle( event );
    }

    private void createCache( final String name )
    {
        final CacheConfig config = this.config.getCacheConfig( name );
        final AbstractCacheFacade facade = doCreateCache( config );
        facade.setName( name );
        facade.setConfig( config );
        facade.setCacheEventPublisher( this.cacheEventPublisher );
        this.cacheMap.put( name, facade );
    }

    protected abstract AbstractCacheFacade doCreateCache( CacheConfig config );

    @Override
    public final CacheFacade getCache( final String name )
    {
        return this.cacheMap.get( name );
    }

    @Autowired
    public final void setProperties( final ConfigProperties properties )
    {
        this.config = new CacheManagerConfig( properties );
    }

    @Autowired(required = false)
    public final void setClusterEventPublisher( final ClusterEventPublisher clusterEventPublisher )
    {
        this.cacheEventPublisher = new CacheEventPublisherAdapter( clusterEventPublisher );
    }

    @Override
    public final CacheFacade getEntityCache()
    {
        return getCache( "entity" );
    }

    @Override
    public final CacheFacade getImageCache()
    {
        return getCache( "image" );
    }

    @Override
    public final CacheFacade getLocalizationCache()
    {
        return getCache( "localization" );
    }

    @Override
    public final CacheFacade getPageCache()
    {
        return getCache( "page" );
    }

    @Override
    public final CacheFacade getXsltCache()
    {
        return getCache( "xslt" );
    }

    @Override
    public void afterPropertiesSet()
    {
        createCache( "page" );
        createCache( "entity" );
        createCache( "image" );
        createCache( "xslt" );
        createCache( "localization" );
    }
}
