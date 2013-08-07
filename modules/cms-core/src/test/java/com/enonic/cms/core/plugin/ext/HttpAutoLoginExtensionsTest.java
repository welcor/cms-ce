package com.enonic.cms.core.plugin.ext;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;

public class HttpAutoLoginExtensionsTest
    extends HttpProcessorExtensionsTest<HttpAutoLogin, HttpAutoLoginExtensions>
{
    public HttpAutoLoginExtensionsTest()
    {
        super( HttpAutoLogin.class );
    }

    @Override
    protected HttpAutoLogin createExt()
    {
        return new HttpAutoLogin()
        {
            @Override
            public String getAuthenticatedUser( final HttpServletRequest request )
                throws Exception
            {
                return null;
            }
        };
    }

    @Override
    protected HttpAutoLoginExtensions createExtensionPoint()
    {
        return new HttpAutoLoginExtensions();
    }
}
