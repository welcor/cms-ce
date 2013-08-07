package com.enonic.cms.core.security.userstore.connector;

import com.enonic.cms.api.plugin.ext.auth.Authenticator;
import com.enonic.cms.core.plugin.ext.AuthenticatorExtensions;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public final class AuthenticationChain
{
    private final AuthenticatorExtensions interceptors;

    public AuthenticationChain( final AuthenticatorExtensions interceptors )
    {
        this.interceptors = interceptors;
    }

    public boolean authenticate( final UserStoreKey userStore, final String userName, final String password )
    {
        if ( this.interceptors.isEmpty() )
        {
            return false;
        }

        final AuthenticationTokenImpl token = new AuthenticationTokenImpl( userStore, userName, password );
        for ( final Authenticator interceptor : this.interceptors )
        {
            if ( interceptor.authenticate( token ) )
            {
                return true;
            }
        }

        return false;
    }
}
