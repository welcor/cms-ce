package com.enonic.cms.core.security.userstore.connector;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationInterceptor;
import com.enonic.cms.api.plugin.ext.auth.AuthenticationResult;
import com.enonic.cms.core.plugin.ext.AuthenticationInterceptorExtensions;

public final class AuthenticationChain
{
    private final AuthenticationInterceptorExtensions interceptors;

    public AuthenticationChain( final AuthenticationInterceptorExtensions interceptors )
    {
        this.interceptors = interceptors;
    }

    public AuthenticationResult authenticate( final String userStore, final String userName, final String password )
    {
        if ( this.interceptors.isEmpty() )
        {
            return AuthenticationResult.CONTINUE;
        }

        final AuthenticationTokenImpl token = new AuthenticationTokenImpl( userStore, userName, password );
        for ( final AuthenticationInterceptor interceptor : this.interceptors )
        {
            final AuthenticationResult result = interceptor.authenticate( token );
            if ( result != AuthenticationResult.CONTINUE )
            {
                return result;
            }
        }

        return AuthenticationResult.CONTINUE;
    }
}
