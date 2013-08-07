package com.enonic.cms.core.plugin.ext;

import org.mockito.Mockito;

import com.enonic.cms.api.plugin.ext.auth.Authenticator;

public class AuthenticatorExtensionsTest
    extends ExtensionPointTest<Authenticator, AuthenticatorExtensions>
{
    public AuthenticatorExtensionsTest()
    {
        super( Authenticator.class );
    }

    @Override
    protected AuthenticatorExtensions createExtensionPoint()
    {
        return new AuthenticatorExtensions();
    }

    private Authenticator create( final int priority )
    {
        final Authenticator ext = Mockito.mock( Authenticator.class );
        ext.setPriority( priority );
        return ext;
    }

    @Override
    protected Authenticator createOne()
    {
        return create( 1 );
    }

    @Override
    protected Authenticator createTwo()
    {
        return create( 2 );
    }
}
