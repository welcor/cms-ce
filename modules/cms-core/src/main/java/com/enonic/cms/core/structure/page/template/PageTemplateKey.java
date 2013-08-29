/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.InvalidKeyException;

/**
 *
 */
public class PageTemplateKey
    implements Serializable
{
    private final int intValue;

    private final String stringValue;


    public PageTemplateKey( final String key )
        throws InvalidKeyException
    {
        try
        {
            final int value = Integer.parseInt( key );
            this.intValue = value;
            this.stringValue = String.valueOf( value );
        }
        catch ( NumberFormatException e )
        {
            throw new InvalidKeyException( key, this.getClass() );
        }
    }

    public PageTemplateKey( final int key )
    {
        this.intValue = key;
        this.stringValue = String.valueOf( key );
    }

    public int toInt()
    {
        return intValue;
    }


    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplateKey ) )
        {
            return false;
        }

        PageTemplateKey that = (PageTemplateKey) o;

        if ( intValue != that.intValue )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 95, 187 ).append( intValue ).toHashCode();
    }

    public String toString()
    {
        return stringValue;
    }

    public static PageTemplateKey parse( final String str )
    {
        if ( str == null )
        {
            return null;
        }

        return new PageTemplateKey( str );
    }

}
