package com.enonic.cms.core.plugin.ext;

import org.mockito.Mockito;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationInterceptor;

public class AuthenticationInterceptorExtensionsTest
    extends ExtensionPointTest<AuthenticationInterceptor, AuthenticationInterceptorExtensions>
{
    public AuthenticationInterceptorExtensionsTest()
    {
        super( AuthenticationInterceptor.class );
    }

    @Override
    protected AuthenticationInterceptorExtensions createExtensionPoint()
    {
        return new AuthenticationInterceptorExtensions();
    }

    private AuthenticationInterceptor create( final int priority )
    {
        final AuthenticationInterceptor ext = Mockito.mock( AuthenticationInterceptor.class );
        ext.setPriority( priority );
        return ext;
    }

    @Override
    protected AuthenticationInterceptor createOne()
    {
        return create( 1 );
    }

    @Override
    protected AuthenticationInterceptor createTwo()
    {
        return create( 2 );
    }
}
