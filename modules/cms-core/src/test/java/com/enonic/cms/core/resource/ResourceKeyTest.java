/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ResourceKeyTest
{
    @Test
    public void testFrom_null()
    {
        final ResourceKey key = ResourceKey.from( null );
        assertNull( key );
    }

    @Test
    public void testFrom_empty()
    {
        final ResourceKey key1 = ResourceKey.from( "" );
        assertNull( key1 );

        final ResourceKey key2 = ResourceKey.from( "    " );
        assertNull( key2 );
    }

    @Test
    public void testFrom_path()
    {
        final ResourceKey key1 = ResourceKey.from( "/" );
        assertNotNull( key1 );
        assertEquals( "/", key1.toString() );

        final ResourceKey key2 = ResourceKey.from( "a/b/c" );
        assertNotNull( key2 );
        assertEquals( "/a/b/c", key2.toString() );

        final ResourceKey key3 = ResourceKey.from( "/a///b/c" );
        assertNotNull( key3 );
        assertEquals( "/a/b/c", key3.toString() );
    }

    @Test
    public void testNormalize()
    {
        final ResourceKey key1 = ResourceKey.from( ".." );
        assertNotNull( key1 );
        assertEquals( "/", key1.toString() );

        final ResourceKey key2 = ResourceKey.from( "././." );
        assertNotNull( key2 );
        assertEquals( "/", key2.toString() );

        final ResourceKey key3 = ResourceKey.from( "/a/../." );
        assertNotNull( key3 );
        assertEquals( "/", key3.toString() );

        final ResourceKey key4 = ResourceKey.from( "a/./b/c/./../.." );
        assertNotNull( key4 );
        assertEquals( "/a", key4.toString() );
    }

    @Test
    public void testIsRoot()
    {
        final ResourceKey key1 = ResourceKey.from( "/" );
        assertNotNull( key1 );
        assertTrue( key1.isRoot() );

        final ResourceKey key2 = ResourceKey.from( "a/b/c" );
        assertNotNull( key2 );
        assertFalse( key2.isRoot() );
    }

    @Test
    public void testGetName()
    {
        final ResourceKey key1 = ResourceKey.from( "/" );
        assertNotNull( key1 );
        assertEquals( "", key1.getName() );

        final ResourceKey key2 = ResourceKey.from( "a/b/c" );
        assertNotNull( key2 );
        assertEquals( "c", key2.getName() );
    }

    @Test
    public void testGetExtension()
    {
        final ResourceKey key1 = ResourceKey.from( "/a/b" );
        assertNotNull( key1 );
        assertEquals( "", key1.getExtension() );

        final ResourceKey key2 = ResourceKey.from( "/a/b.txt" );
        assertNotNull( key2 );
        assertEquals( "txt", key2.getExtension() );
    }

    @Test
    public void testGetParent()
    {
        final ResourceKey key1 = ResourceKey.from( "/" );
        assertNotNull( key1 );
        assertNull( key1.getParent() );

        final ResourceKey key2 = ResourceKey.from( "/a/b.txt" );
        assertNotNull( key2 );
        assertEquals( "/a", key2.getParent().toString() );
    }
}
