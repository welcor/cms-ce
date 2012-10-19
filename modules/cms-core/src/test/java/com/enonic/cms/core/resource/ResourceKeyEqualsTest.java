/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.junit.Test;

import com.enonic.cms.core.AbstractEqualsTest;


public class ResourceKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return ResourceKey.parse( "ABC" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{ResourceKey.parse( "CBA" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return ResourceKey.parse( "ABC" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return ResourceKey.parse( "ABC" );
    }
}
