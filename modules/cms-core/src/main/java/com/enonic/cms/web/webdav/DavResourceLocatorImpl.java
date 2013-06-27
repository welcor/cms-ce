/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;

final class DavResourceLocatorImpl
    implements DavResourceLocator
{
    private final String prefix;

    private final String resourcePath;

    private final DavLocatorFactory factory;

    private final String href;

    public DavResourceLocatorImpl( final String prefix, String resourcePath, final DavLocatorFactory factory )
    {
        this.prefix = prefix;
        this.factory = factory;

        if ( resourcePath.endsWith( "/" ) && !"/".equals( resourcePath ) )
        {
            resourcePath = resourcePath.substring( 0, resourcePath.length() - 1 );
        }

        this.resourcePath = resourcePath;
        this.href = this.prefix + Text.escapePath( this.resourcePath );
    }

    @Override
    public String getPrefix()
    {
        return this.prefix;
    }

    @Override
    public String getResourcePath()
    {
        return this.resourcePath;
    }

    @Override
    public String getWorkspacePath()
    {
        return "";
    }

    @Override
    public String getWorkspaceName()
    {
        return "";
    }

    @Override
    public boolean isSameWorkspace( DavResourceLocator locator )
    {
        return isSameWorkspace( locator.getWorkspaceName() );
    }

    @Override
    public boolean isSameWorkspace( String workspaceName )
    {
        return getWorkspaceName().equals( workspaceName );
    }

    @Override
    public String getHref( boolean isCollection )
    {
        String suffix = ( isCollection && !isRootLocation() ) ? "/" : "";
        return this.href + suffix;
    }

    @Override
    public boolean isRootLocation()
    {
        return "/".equals( this.resourcePath );
    }

    @Override
    public DavLocatorFactory getFactory()
    {
        return this.factory;
    }

    @Override
    public String getRepositoryPath()
    {
        return getResourcePath();
    }

    @Override
    public int hashCode()
    {
        return this.href.hashCode();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof DavResourceLocator )
        {
            final DavResourceLocator other = (DavResourceLocator) obj;
            return hashCode() == other.hashCode();
        }

        return false;
    }
}
