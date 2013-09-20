package com.enonic.cms.web.status;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    protected String exceptionToString( Throwable t )
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        t.printStackTrace( pw );
        return sw.toString();
    }

    @Override
    public int compareTo( final StatusInfoBuilder o )
    {
        return this.name.compareTo( o.name );
    }
}
