package com.enonic.cms.core.plugin.ext;

import org.mockito.Mockito;

import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;

public class HttpInterceptorExtensionsTest
    extends HttpProcessorExtensionsTest<HttpInterceptor, HttpInterceptorExtensions>
{
    public HttpInterceptorExtensionsTest()
    {
        super( HttpInterceptor.class );
    }

    @Override
    protected HttpInterceptor createExt()
    {
        return Mockito.mock( HttpInterceptor.class );
    }

    @Override
    protected HttpInterceptorExtensions createExtensionPoint()
    {
        return new HttpInterceptorExtensions();
    }
}
