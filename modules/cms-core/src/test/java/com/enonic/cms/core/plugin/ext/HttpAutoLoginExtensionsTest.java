package com.enonic.cms.core.plugin.ext;

import org.mockito.Mockito;

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
        return Mockito.mock( HttpAutoLogin.class );
    }

    @Override
    protected HttpAutoLoginExtensions createExtensionPoint()
    {
        return new HttpAutoLoginExtensions();
    }
}
