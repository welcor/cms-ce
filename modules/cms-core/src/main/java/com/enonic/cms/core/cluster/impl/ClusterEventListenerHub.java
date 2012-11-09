package com.enonic.cms.core.cluster.impl;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventListener;

final class ClusterEventListenerHub
    implements ClusterEventListener
{
    private final ClusterEventListener[] listeners;

    public ClusterEventListenerHub( final ClusterEventListener... listeners )
    {
        this.listeners = listeners;
    }

    @Override
    public void handle( final ClusterEvent event )
    {
        for ( final ClusterEventListener listener : this.listeners )
        {
            listener.handle( event );
        }
    }
}
