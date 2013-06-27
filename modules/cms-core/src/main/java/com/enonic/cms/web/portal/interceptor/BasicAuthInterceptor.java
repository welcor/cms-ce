/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.web.portal.PortalWebContext;

@Component
public final class BasicAuthInterceptor
    implements RequestInterceptor
{
    private SecurityService securityService;

    @Autowired
    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest req = context.getRequest();
        String[] cred = getAuthCredentials( req );
        UserEntity current = this.securityService.getLoggedInPortalUserAsEntity();

        if ( cred != null && cred.length == 2 && current.isAnonymous() )
        {
            login( cred[0], cred[1] );
        }

        return true;
    }

    @Override
    public void postHandle( final PortalWebContext context )
        throws Exception
    {
        // Do nothing
    }

    private String[] getAuthCredentials( HttpServletRequest req )
    {
        String auth = req.getHeader( "Authorization" );
        if ( auth == null )
        {
            return null;
        }

        String[] tmp = auth.split( " " );
        if ( tmp.length < 2 )
        {
            return null;
        }

        if ( !"basic".equalsIgnoreCase( tmp[0] ) )
        {
            return null;
        }

        String authStr = new String( Base64.decodeBase64( tmp[1].getBytes() ) );

        String[] credentials = authStr.split( ":" );

        if ( credentials == null )
        {
            return null;
        }

        // Set blank password if none provided
        if ( credentials.length == 1 )
        {
            return new String[]{credentials[0], ""};
        }
        else if ( credentials.length == 2 )
        {
            return credentials;
        }
        else
        {
            return null;
        }
    }

    private void login( String user, String password )
    {
        login( new QualifiedUsername( user ), password );
    }

    private void login( QualifiedUsername user, String password )
    {
        UserEntity current = this.securityService.getLoggedInPortalUserAsEntity();
        if ( current.getQualifiedName().equals( user ) )
        {
            return;
        }

        try
        {
            this.securityService.loginPortalUser( user, password );
        }
        catch ( Exception e )
        {
            // Do nothing
        }
    }
}
