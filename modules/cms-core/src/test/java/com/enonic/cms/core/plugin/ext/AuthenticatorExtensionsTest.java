package com.enonic.cms.core.plugin.ext;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationToken;
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
        final Authenticator ext = new Authenticator()
        {
            @Override
            public boolean authenticate( final AuthenticationToken token )
            {
                return false;
            }
        };

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
