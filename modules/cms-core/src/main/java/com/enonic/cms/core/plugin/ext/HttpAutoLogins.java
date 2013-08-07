package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;

@Component
public final class HttpAutoLogins
    extends HttpProcessors<HttpAutoLogin>
{
    public HttpAutoLogins()
    {
        super( HttpAutoLogin.class );
    }
}
