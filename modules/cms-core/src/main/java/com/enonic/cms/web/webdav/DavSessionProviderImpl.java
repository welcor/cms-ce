/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;

import com.google.common.base.Charsets;

import com.enonic.cms.core.resource.access.ResourceAccessResolver;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;

final class DavSessionProviderImpl
    implements DavSessionProvider
{
    private final SecurityService securityService;

    private final ResourceAccessResolver accessResolver;

    public DavSessionProviderImpl( final DavConfiguration configuration )
    {
        this.securityService = configuration.getSecurityService();
        this.accessResolver = configuration.getResourceAccessResolver();
    }

    @Override
    public boolean attachSession( final WebdavRequest request )
        throws DavException
    {
        final DavSession session = createSession( request );
        if ( session != null )
        {
            request.setDavSession( session );
        }

        return session != null;
    }

    @Override
    public void releaseSession( final WebdavRequest request )
    {
        request.setDavSession( null );
    }

    private DavSession createSession( final WebdavRequest request )
        throws DavException
    {
        final String[] auth = getCredentials( request );
        if ( auth == null )
        {
            throw new DavException( DavServletResponse.SC_UNAUTHORIZED );
        }
        if ( !login( auth[0], auth[1] ) )
        {
            throw new DavException( DavServletResponse.SC_UNAUTHORIZED );
        }

        return new DavSessionImpl();
    }

    private String[] getCredentials( WebdavRequest request )
        throws DavException
    {
        final String authHeader = request.getHeader( DavConstants.HEADER_AUTHORIZATION );
        if ( authHeader == null )
        {
            return null;
        }

        final String[] authStr = authHeader.split( " " );
        if ( authStr.length < 2 )
        {
            return null;
        }

        if ( !authStr[0].equalsIgnoreCase( HttpServletRequest.BASIC_AUTH ) )
        {
            return null;
        }

        final String decAuthStr = new String( Base64.decodeBase64( authStr[1].getBytes() ), Charsets.ISO_8859_1 );
        final int pos = decAuthStr.indexOf( ':' );
        final String userName = decAuthStr.substring( 0, pos );
        final String password = decAuthStr.substring( pos + 1 );

        return new String[]{userName, password};
    }

    private boolean login( final String user, final String password )
    {
        try
        {
            this.securityService.loginDavUser( QualifiedUsername.parse( user ), password );
            return this.accessResolver.hasAccessToResourceTree( this.securityService.getLoggedInPortalUserAsEntity() );
        }
        catch ( Exception e )
        {
            return false;
        }
    }
}
