package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationInterceptor;

@Component
public final class AuthenticationInterceptorExtensions
    extends ExtensionPoint<AuthenticationInterceptor>
{
    public AuthenticationInterceptorExtensions()
    {
        super( AuthenticationInterceptor.class );
    }

    @Override
    protected String toHtml( final AuthenticationInterceptor ext )
    {
        return composeHtml( ext, "priority", ext.getPriority() );
    }

    @Override
    public int compare( final AuthenticationInterceptor o1, final AuthenticationInterceptor o2 )
    {
        return o1.compareTo( o2 );
    }
}
