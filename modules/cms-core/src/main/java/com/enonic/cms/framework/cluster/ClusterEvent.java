package com.enonic.cms.framework.cluster;

import java.util.EventObject;

public final class ClusterEvent
    extends EventObject
{
    private final String payload;

    private final long timestamp;

    public ClusterEvent( final String type, final String payload )
    {
        super( type );
        this.payload = payload != null ? payload: "";
        this.timestamp = System.currentTimeMillis();
    }

    public String getType()
    {
        return (String) getSource();
    }

    public boolean isOfType( String type )
    {
        return getType().equals( type );
    }

    public String getPayload()
    {
        return this.payload;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public String toString()
    {
        final StringBuilder str = new StringBuilder();
        str.append( "type = (" ).append( getType() );
        str.append( "), payload = (" ).append( this.payload );
        str.append( "), timestamp = (" ).append( this.timestamp ).append( ")" );
        return str.toString();
    }
}

