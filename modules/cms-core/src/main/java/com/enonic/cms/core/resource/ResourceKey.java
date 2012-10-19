/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.LinkedList;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public final class ResourceKey
{
    private final String[] parts;

    private final String path;

    private ResourceKey( final String path )
    {
        this( split( path ) );
    }

    private ResourceKey( final String[] parts )
    {
        this.parts = normalize( parts );
        this.path = join( this.parts );
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof ResourceKey ) && ( (ResourceKey) o ).path.equals( this.path );
    }

    public int hashCode()
    {
        return this.path.hashCode();
    }

    public String toString()
    {
        return this.path;
    }

    public boolean startsWith( final String prefix )
    {
        return this.path.startsWith( prefix );
    }

    public boolean isRoot()
    {
        return this.parts.length == 0;
    }

    public String getName()
    {
        if ( this.parts.length == 0 )
        {
            return "";
        }

        return this.parts[this.parts.length - 1];
    }

    public String getExtension()
    {
        return Files.getFileExtension( getName() );
    }

    public ResourceKey getParent()
    {
        if ( this.parts.length > 0 )
        {
            String[] tmp = new String[this.parts.length - 1];
            System.arraycopy( this.parts, 0, tmp, 0, tmp.length );
            return new ResourceKey( tmp );
        }
        else
        {
            return null;
        }
    }

    public static ResourceKey from( final String path )
    {
        if ( path == null )
        {
            return null;
        }

        if ( path.trim().length() == 0 )
        {
            return null;
        }

        return new ResourceKey( path );
    }

    private static String[] split( final String path )
    {
        final Iterable<String> result = Splitter.on( "/" ).omitEmptyStrings().trimResults().split( path );
        return Iterables.toArray( result, String.class );
    }

    private static String join( final String[] parts )
    {
        return "/" + Joiner.on( "/" ).join( parts );
    }

    private static String[] normalize( final String[] parts )
    {
        final LinkedList<String> normalized = Lists.newLinkedList();

        for ( final String part : parts )
        {
            if ( part.equals( "." ) )
            {
                continue;
            }

            if ( part.equals( ".." ) )
            {
                if ( !normalized.isEmpty() )
                {
                    normalized.removeLast();
                }

                continue;
            }

            normalized.add( part );
        }

        return Iterables.toArray( normalized, String.class );
    }
}
