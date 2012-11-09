package com.enonic.cms.core.cluster.impl;

import java.io.IOException;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;

import com.enonic.cms.core.cluster.ClusterEvent;

final class SendClusterEventRequest
    implements Streamable
{
    private ClusterEvent event;

    public SendClusterEventRequest()
    {
        this( null );
    }

    public SendClusterEventRequest( final ClusterEvent event )
    {
        this.event = event;
    }

    public ClusterEvent getEvent()
    {
        return this.event;
    }

    @Override
    public void readFrom( final StreamInput in )
        throws IOException
    {
        final String type = in.readString();
        final String[] payload = in.readStringArray();
        this.event = new ClusterEvent( type, payload );
    }

    @Override
    public void writeTo( final StreamOutput out )
        throws IOException
    {
        out.writeString( this.event.getType() );
        out.writeStringArray( this.event.getPayload() );
    }
}
