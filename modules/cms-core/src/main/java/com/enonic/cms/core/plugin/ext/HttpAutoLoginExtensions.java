package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;

@Component
public final class HttpAutoLoginExtensions
    extends HttpProcessorExtensions<HttpAutoLogin>
{
    public HttpAutoLoginExtensions()
    {
        super( HttpAutoLogin.class );
    }
}
