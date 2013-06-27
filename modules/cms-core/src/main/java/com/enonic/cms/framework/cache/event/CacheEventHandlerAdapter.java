/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.framework.cache.event;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventListener;

public final class CacheEventHandlerAdapter
    implements ClusterEventListener, CacheEventConstants
{
    private final CacheEventHandler handler;

    public CacheEventHandlerAdapter( final CacheEventHandler handler )
    {
        this.handler = handler;
    }

    @Override
    public void handle( final ClusterEvent event )
    {
        final String type = event.getType();

        if ( EVICT_ALL.equals( type ) )
        {
            handleEvictAll( event );
        }
        else if ( EVICT_BY_KEY.equals( type ) )
        {
            handleEvictByKey( event );
        }
        else if ( EVICT_BY_GROUP.equals( type ) )
        {
            handleEvictByGroup( event );
        }
        else if ( EVICT_BY_PREFIX.equals( type ) )
        {
            handleEvictByPrefix( event );
        }
    }

    private void handleEvictAll( final ClusterEvent event )
    {
        final String cacheName = event.getPayloadAt( 0 );
        this.handler.handleEvictAll( cacheName );
    }

    private void handleEvictByKey( final ClusterEvent event )
    {
        final String cacheName = event.getPayloadAt( 0 );
        final String key = event.getPayloadAt( 1 );
        this.handler.handleEvictByKey( cacheName, key );
    }

    private void handleEvictByGroup( final ClusterEvent event )
    {
        final String cacheName = event.getPayloadAt( 0 );
        final String group = event.getPayloadAt( 1 );
        this.handler.handleEvictByGroup( cacheName, group );
    }

    private void handleEvictByPrefix( final ClusterEvent event )
    {
        final String cacheName = event.getPayloadAt( 0 );
        final String prefix = event.getPayloadAt( 1 );
        this.handler.handleEvictByPrefix( cacheName, prefix );
    }
}
