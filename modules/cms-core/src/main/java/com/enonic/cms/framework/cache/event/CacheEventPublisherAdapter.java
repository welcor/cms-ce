package com.enonic.cms.framework.cache.event;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventPublisher;

public final class CacheEventPublisherAdapter
    implements CacheEventPublisher, CacheEventConstants
{
    private final ClusterEventPublisher publisher;

    public CacheEventPublisherAdapter( final ClusterEventPublisher publisher )
    {
        this.publisher = publisher;
    }

    @Override
    public void publishEvictByKey( final String cacheName, final String key )
    {
        this.publisher.publish( new ClusterEvent( EVICT_BY_KEY, cacheName, key ) );
    }

    @Override
    public void publishEvictByGroup( final String cacheName, final String group )
    {
        this.publisher.publish( new ClusterEvent( EVICT_BY_GROUP, cacheName, group ) );
    }

    @Override
    public void publishEvictByPrefix( final String cacheName, final String prefix )
    {
        this.publisher.publish( new ClusterEvent( EVICT_BY_PREFIX, cacheName, prefix ) );
    }

    @Override
    public void publishEvictAll( final String cacheName )
    {
        this.publisher.publish( new ClusterEvent( EVICT_ALL, cacheName ) );
    }
}
