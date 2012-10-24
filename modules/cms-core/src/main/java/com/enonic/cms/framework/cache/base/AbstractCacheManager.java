package com.enonic.cms.framework.cache.base;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.cluster.ClusterEvent;
import com.enonic.cms.framework.cluster.ClusterEventListener;
import com.enonic.cms.framework.cluster.ClusterManager;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.config.ConfigProperties;

public abstract class AbstractCacheManager
    implements CacheManager, ClusterEventListener, CacheClusterSender
{
    private final static String EVICT_PREFIX = "cache.evict.";

    private final static String EVICT_ALL_PREFIX = "cache.evictAll.";

    private final static String EVICT_GROUP_PREFIX = "cache.evictGroup.";

    private final static String EVICT_BY_PREFIX_PREFIX = "cache.evictByGroupPrefix.";

    private final Map<String, AbstractCacheFacade> cacheMap;

    private ClusterManager clusterManager;

    private CacheManagerConfig config;

    public AbstractCacheManager()
    {
        this.cacheMap = Maps.newHashMap();
    }

    @Autowired
    public final void setClusterManager( final ClusterManager clusterManager )
    {
        this.clusterManager = clusterManager;
        this.clusterManager.addListener( this );
    }

    @Override
    public final void sendEvictMessage( final String cacheName, final String objectKey )
    {
        if ( this.clusterManager != null )
        {
            this.clusterManager.publish( new ClusterEvent( EVICT_PREFIX + cacheName, objectKey ) );
        }
    }

    @Override
    public final void sendEvictGroupMessage( final String cacheName, final String groupName )
    {
        if ( this.clusterManager != null )
        {
            this.clusterManager.publish( new ClusterEvent( EVICT_GROUP_PREFIX + cacheName, groupName ) );
        }
    }

    @Override
    public final void sendEvictByGroupPrefixMessage( final String cacheName, final String groupPrefix )
    {
        if ( this.clusterManager != null )
        {
            this.clusterManager.publish( new ClusterEvent( EVICT_BY_PREFIX_PREFIX + cacheName, groupPrefix ) );
        }
    }

    @Override
    public final void sendEvictAllMessage( String cacheName )
    {
        if ( this.clusterManager != null )
        {
            this.clusterManager.publish( new ClusterEvent( EVICT_ALL_PREFIX + cacheName, null ) );
        }
    }

    @Override
    public final void handle( final ClusterEvent event )
    {
        String type = event.getType();
        String strValue = event.getPayload();

        if ( type.startsWith( EVICT_PREFIX ) )
        {
            handleEvictMessage( type.substring( EVICT_PREFIX.length() ), strValue );
        }
        else if ( type.startsWith( EVICT_ALL_PREFIX ) )
        {
            handleEvictAllMessage( type.substring( EVICT_ALL_PREFIX.length() ) );
        }
        else if ( type.startsWith( EVICT_GROUP_PREFIX ) )
        {
            handleEvictGroupMessage( type.substring( EVICT_GROUP_PREFIX.length() ), strValue );
        }
        else if ( type.startsWith( EVICT_BY_PREFIX_PREFIX ) )
        {
            handleEvictByGroupPrefixMessage( type.substring( EVICT_BY_PREFIX_PREFIX.length() ), strValue );
        }
    }

    private void handleEvictMessage( final String cacheName, final String objectKey )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemove( objectKey );
        }
    }

    private void handleEvictGroupMessage( final String cacheName, final String groupName )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveGroup( groupName );
        }
    }

    private void handleEvictByGroupPrefixMessage( final String cacheName, final String groupPrefix )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveGroupByPrefix( groupPrefix );
        }
    }

    private void handleEvictAllMessage( final String cacheName )
    {
        final AbstractCacheFacade cache = this.cacheMap.get( cacheName );
        if ( cache != null )
        {
            cache.doRemoveAll();
        }
    }

    private AbstractCacheFacade doCreateCache( final String name )
    {
        final CacheConfig config = this.config.getCacheConfig( name );
        final AbstractCacheFacade facade = doCreateCache( config );
        facade.setName( name );
        facade.setConfig( config );
        facade.setClusterSender( this );
        return facade;
    }

    protected abstract AbstractCacheFacade doCreateCache( CacheConfig config );

    @Override
    public final CacheFacade getCache( final String name )
    {
        synchronized ( this.cacheMap )
        {
            return this.cacheMap.get( name );
        }
    }

    @Override
    public final CacheFacade getOrCreateCache( final String name )
    {
        synchronized ( this.cacheMap )
        {
            AbstractCacheFacade cache = this.cacheMap.get( name );
            if ( cache == null )
            {
                cache = doCreateCache( name );
                this.cacheMap.put( name, cache );
            }

            return cache;
        }
    }

    @Autowired
    public final void setProperties( final ConfigProperties properties )
    {
        this.config = new CacheManagerConfig( properties );
    }

    @Override
    public final XMLDocument getInfoAsXml()
    {
        final Element root = new Element( "caches" );
        for ( AbstractCacheFacade cache : this.cacheMap.values() )
        {
            root.addContent( cache.getInfoAsXml().getAsJDOMDocument().getRootElement().detach() );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }
}
