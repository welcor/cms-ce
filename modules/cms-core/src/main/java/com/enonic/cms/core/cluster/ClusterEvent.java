package com.enonic.cms.core.cluster;

import java.util.Arrays;

public final class ClusterEvent
{
    private final String type;

    private final String[] payload;

    public ClusterEvent( final String type, final String... payload )
    {
        this.type = type;
        this.payload = payload;
    }

    public String getType()
    {
        return this.type;
    }

    public String[] getPayload()
    {
        return this.payload;
    }

    public String getPayloadAt( final int index )
    {
        if ( ( index >= 0 ) && ( index < this.payload.length ) )
        {
            return this.payload[index];
        }
        else
        {
            return null;
        }
    }

    public boolean isOfType( final String type )
    {
        return this.type.equals( type );
    }

    @Override
    public String toString()
    {
        return this.type + Arrays.toString( this.payload );
    }
}
