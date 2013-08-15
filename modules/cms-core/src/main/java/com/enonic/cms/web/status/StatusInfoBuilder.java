package com.enonic.cms.web.status;

import org.codehaus.jackson.node.ObjectNode;

public abstract class StatusInfoBuilder
    implements Comparable<StatusInfoBuilder>
{
    private final String name;

    public StatusInfoBuilder( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public final void addInfo( final ObjectNode json )
    {
        build( json.putObject( this.name ) );
    }

    protected abstract void build( ObjectNode json );

    @Override
    public int compareTo( final StatusInfoBuilder o )
    {
        return this.name.compareTo( o.name );
    }
}
