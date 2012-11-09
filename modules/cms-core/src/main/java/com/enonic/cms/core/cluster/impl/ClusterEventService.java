package com.enonic.cms.core.cluster.impl;

import javax.annotation.PostConstruct;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.transport.VoidTransportResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.cluster.ClusterEvent;
import com.enonic.cms.core.cluster.ClusterEventListener;
import com.enonic.cms.core.cluster.ClusterEventPublisher;

@Component
public final class ClusterEventService
    implements ClusterEventPublisher
{
    private Node node;

    private ClusterEventListener[] listeners;

    private ClusterService clusterService;

    private TransportService transportService;

    @Override
    public void publish( final ClusterEvent event )
    {
        final DiscoveryNode localNode = this.clusterService.localNode();
        for ( final DiscoveryNode node : this.clusterService.state().nodes() )
        {
            if ( !node.equals( localNode ) )
            {
                send( node, event );
            }
        }
    }

    private void send( final DiscoveryNode node, final ClusterEvent event )
    {
        final SendClusterEventRequest request = new SendClusterEventRequest( event );
        final VoidTransportResponseHandler responseHandler = new VoidTransportResponseHandler( ThreadPool.Names.MANAGEMENT );
        this.transportService.sendRequest( node, SendClusterEventRequestHandler.ACTION, request, responseHandler );
    }

    @PostConstruct
    public void init()
    {
        final InternalNode internalNode = (InternalNode) this.node;
        this.transportService = internalNode.injector().getInstance( TransportService.class );
        this.clusterService = internalNode.injector().getInstance( ClusterService.class );

        final ClusterEventListenerHub listenerHub = new ClusterEventListenerHub( this.listeners );
        final SendClusterEventRequestHandler handler = new SendClusterEventRequestHandler( listenerHub );
        this.transportService.registerHandler( SendClusterEventRequestHandler.ACTION, handler );
    }

    @Autowired
    public void setNode( final Node node )
    {
        this.node = node;
    }

    @Autowired
    public void setListeners( final ClusterEventListener... listeners )
    {
        this.listeners = listeners;
    }
}
