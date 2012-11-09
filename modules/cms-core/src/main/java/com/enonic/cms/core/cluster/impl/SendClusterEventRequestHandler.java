package com.enonic.cms.core.cluster.impl;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportRequestHandler;
import org.elasticsearch.transport.TransportChannel;

import com.enonic.cms.core.cluster.ClusterEventListener;

final class SendClusterEventRequestHandler
    extends BaseTransportRequestHandler<SendClusterEventRequest>
{
    public final static String ACTION = "cms/cluster/send";

    private final ClusterEventListener listener;

    public SendClusterEventRequestHandler( final ClusterEventListener listener )
    {
        this.listener = listener;
    }

    @Override
    public SendClusterEventRequest newInstance()
    {
        return new SendClusterEventRequest();
    }

    @Override
    public void messageReceived( final SendClusterEventRequest request, final TransportChannel channel )
        throws Exception
    {
        this.listener.handle( request.getEvent() );
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
    }
}
