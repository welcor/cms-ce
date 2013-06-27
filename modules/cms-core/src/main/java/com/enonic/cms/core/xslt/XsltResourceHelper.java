/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class XsltResourceHelper
{
    private static final String DUMMY_PREFIX = "dummy:/";

    public static String resolvePath( final String path )
    {
        try
        {
            final URI uri = new URI( path );
            return removeExtraSlashes( uri.getPath() );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static String removeExtraSlashes( final String str )
    {
        if ( str.contains( "//" ) )
        {
            return removeExtraSlashes( str.replace( "//", "/" ) );
        }
        else
        {
            return str;
        }
    }

    public static String resolveBasePath( final String path )
    {
        final String name = resolvePath( path );
        return removeExtraSlashes( name.substring( 0, name.lastIndexOf( '/' ) ) );
    }

    public static String resolveRelativePath( final String href, final String base )
    {
        if ( href.startsWith( "/" ) )
        {
            return href;
        }

        return removeExtraSlashes( resolveBasePath( base ) + "/" + href );
    }

    public static String createUri( final String path )
    {
        if ( path.contains( ":/" ) )
        {
            return path;
        }

        try
        {
            return DUMMY_PREFIX + URLEncoder.encode( path, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new AssertionError( e );
        }
    }
}
