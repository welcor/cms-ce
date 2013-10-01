/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.AbstractIntegerBasedKey;

public class SectionContentTypeFilterKey
    extends AbstractIntegerBasedKey
    implements Serializable
{

    public SectionContentTypeFilterKey( String key )
    {
        init( key );
    }

    public SectionContentTypeFilterKey( int key )
    {
        init( key );
    }

    public SectionContentTypeFilterKey( Integer key )
    {
        init( key );
    }

    @Override
    protected int minAllowedValue()
    {
        return -1;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SectionContentTypeFilterKey ) )
        {
            return false;
        }

        SectionContentTypeFilterKey that = (SectionContentTypeFilterKey) o;

        if ( intValue() != that.intValue() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 823, 263 ).append( intValue() ).toHashCode();
    }

    public static SectionContentTypeFilterKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new SectionContentTypeFilterKey( str );
    }
}
