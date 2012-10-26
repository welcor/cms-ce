package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

public final class MethodElementBuilder
{
    private final Element result;

    public MethodElementBuilder( final String name )
    {
        this.result = new Element( "datasource" );
        this.result.setAttribute( "name", name );
    }

    public MethodElementBuilder param( final String name, final String value )
    {
        final Element elem = new Element( "parameter" );
        elem.setAttribute( "name", name );
        elem.setText( value );
        this.result.addContent( elem );
        return this;
    }

    public MethodElementBuilder params( final String[] values, final String... names )
    {
        final int min = Math.min( values.length, names.length );
        for ( int i = 0; i < min; i++ )
        {
            param( names[i], values[i] );
        }

        return this;
    }

    public Element build()
    {
        return this.result;
    }
}
