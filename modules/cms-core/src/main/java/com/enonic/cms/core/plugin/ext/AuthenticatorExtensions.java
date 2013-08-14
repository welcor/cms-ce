package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.auth.Authenticator;

@Component
public final class AuthenticatorExtensions
    extends ExtensionPoint<Authenticator>
{
    public AuthenticatorExtensions()
    {
        super( Authenticator.class );
    }

    @Override
    protected String toHtml( final Authenticator ext )
    {
        return composeHtml( ext, "priority", ext.getPriority() );
    }

    @Override
    public int compare( final Authenticator o1, final Authenticator o2 )
    {
        return o1.compareTo( o2 );
    }
}
