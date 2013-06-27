/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import com.enonic.cms.core.Path;

/**
 * Jul 26, 2009
 */
public class WindowReference
{
    public final static String WINDOW_PATH_PREFIX = "_window";

    private String portletName;

    private Path pathToMenuItem;

    private String extension;

    public static WindowReference parse( Path localPath )
    {
        int index = localPath.indexOf( WINDOW_PATH_PREFIX );
        if ( index == -1 )
        {
            return null;
        }

        if ( index >= localPath.getPathElementsCount() )
        {
            return null;
        }
        String portletName = localPath.getPathElement( index + 1 );

        // extension is used for outputFormat . see reference in method's javadoc
        final String portletNameWithoutExtension = portletName.replaceAll( "\\.[^\\.]*?$", "" );

        String extension = null;
        if ( portletNameWithoutExtension.length() + 1 < portletName.length() )
        {
            extension = portletName.substring( portletNameWithoutExtension.length() + 1, portletName.length() );
        }

        String pathWithoutWindowReference = localPath.subPath( 0, index );
        if ( localPath.hasFragment() )
        {
            pathWithoutWindowReference = pathWithoutWindowReference + "#" + localPath.getFragment();
        }
        Path pathToMenuItem = new Path( pathWithoutWindowReference, true );

        return new WindowReference( portletNameWithoutExtension, pathToMenuItem, extension );
    }

    private WindowReference( String portletName, Path pathToMenuItem, String extension )
    {
        this.portletName = portletName;
        this.pathToMenuItem = pathToMenuItem;
        this.extension = extension;
    }

    public String getPortletName()
    {
        return portletName;
    }

    public boolean hasExtension()
    {
        return extension != null;
    }

    public String getExtension()
    {
        return extension;
    }

    public Path getPathToMenuItem()
    {
        return pathToMenuItem;
    }
}
