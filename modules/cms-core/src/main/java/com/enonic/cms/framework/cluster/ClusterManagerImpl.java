package com.enonic.cms.framework.cluster;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public final class ClusterManagerImpl
    implements ClusterManager, ClusterEventListener, InitializingBean, DisposableBean
{
    private boolean enabled;

    private ClusterProvider provider;

    private final List<ClusterEventListener> listeners;

    public ClusterManagerImpl()
    {
        this.listeners = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public String getNodeName()
    {
        if ( isEnabled() )
        {
            return this.provider.getNodeName();
        }
        else
        {
            return "Unknown";
        }
    }

    @Override
    public List<String> getMembers()
    {
        if ( isEnabled() )
        {
            return this.provider.getMembers();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled && ( this.provider != null );
    }

    @Value("${cms.cluster.enabled}")
    public void setEnabled( final boolean enabled )
    {
        this.enabled = enabled;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        if ( !isEnabled() )
        {
            return;
        }

        this.provider.start( this );
    }

    @Override
    public void destroy()
        throws Exception
    {
        if ( !isEnabled() )
        {
            return;
        }

        this.provider.stop();
    }

    @Override
    public synchronized void addListener( final ClusterEventListener listener )
    {
        if ( !this.listeners.contains( listener ) )
        {
            this.listeners.add( listener );
        }
    }

    @Override
    public synchronized void removeListener( final ClusterEventListener listener )
    {
        this.listeners.remove( listener );
    }

    @Override
    public void handle( final ClusterEvent event )
    {
        if ( !isEnabled() )
        {
            return;
        }

        for ( final ClusterEventListener listener : this.listeners )
        {
            listener.handle( event );
        }
    }

    @Override
    public void publish( final ClusterEvent event )
    {
        if ( !isEnabled() )
        {
            return;
        }

        this.provider.publish( event );
    }

    @Autowired(required = false)
    public void setProvider( final ClusterProvider provider )
    {
        this.provider = provider;
    }
}
