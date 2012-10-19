/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.resource;

import com.enonic.cms.core.PathAndParams;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.resource.ResourceKey;

final class ResourceKeyResolver
{
    private final String pathToPublicHome;

    public ResourceKeyResolver( final String pathToPublicHome )
    {
        this.pathToPublicHome = pathToPublicHome;

        if ( this.pathToPublicHome != null )
        {
            validatePathToPublicHome( this.pathToPublicHome );
        }
    }

    private void validatePathToPublicHome( final String value )
    {
        if ( !value.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "pathToPublicHome must start with /" );
        }
    }

    public ResourceKey resolveResourceKey( final SitePath sitePath )
    {
        final PathAndParams localPathAndParams = new PathAndParams( sitePath.getLocalPath(), sitePath.getRequestParameters() );
        return resolveResourceKeyFromPath( localPathAndParams.getPath().toString() );
    }

    private ResourceKey resolveResourceKeyFromPath( String localPath )
    {
        if ( localPath.contains( "/~/" ) )
        {
            final String resolvedPathToHome = pathToPublicHome + "/";
            localPath = localPath.substring( localPath.indexOf( "/~/" ) );
            localPath = localPath.replace( "/~/", resolvedPathToHome );
        }
        else if ( localPath.startsWith( "/" ) )
        {
            localPath = localPath.substring( "/".length() );
        }

        return ResourceKey.parse( localPath );
    }
}
