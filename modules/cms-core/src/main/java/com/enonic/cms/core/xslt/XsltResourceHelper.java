package com.enonic.cms.core.xslt;

import java.net.URI;

public class XsltResourceHelper
{
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
}
