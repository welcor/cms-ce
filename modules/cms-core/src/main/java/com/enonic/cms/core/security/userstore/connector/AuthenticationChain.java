package com.enonic.cms.core.security.userstore.connector;

import java.util.List;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationInterceptor;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public final class AuthenticationChain
{
    private final List<AuthenticationInterceptor> interceptors;

    public AuthenticationChain( final List<AuthenticationInterceptor> interceptors )
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
        for ( final AuthenticationInterceptor interceptor : this.interceptors )
        {
            if ( interceptor.authenticate( token ) )
            {
                return true;
            }
        }

        return false;
    }
}
