/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

public class ViewFunctionArgument
{
    private MaxLengthedString name = new MaxLengthedString();

    private MaxLengthedString value = new MaxLengthedString();

    ViewFunctionArgument( String name, String value )
    {
        this.name = new MaxLengthedString( name );
        this.value = new MaxLengthedString( value );
    }

    ViewFunctionArgument( String name, String[] value )
    {
        this.name = new MaxLengthedString( name );
        this.value = new MaxLengthedString( stringArrayToString( value ) );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getName()
    {
        return name.toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getValue()
    {
        return value.toString();
    }

    private String stringArrayToString( String[] array )
    {
        if ( array == null )
        {
            return "null";
        }

        StringBuilder s = new StringBuilder();
        for ( int i = 0; i < array.length; i++ )
        {
            s.append( "" ).append( array[i] );
            if ( i < array.length - 1 )
            {
                s.append( ", " );
            }
        }
        return s.toString();
    }
}

