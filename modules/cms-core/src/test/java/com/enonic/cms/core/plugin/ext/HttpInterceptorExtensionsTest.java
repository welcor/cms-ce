package com.enonic.cms.core.plugin.ext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        return new HttpInterceptor()
        {
            @Override
            public boolean preHandle( final HttpServletRequest request, final HttpServletResponse response )
                throws Exception
            {
                return false;
            }

            @Override
            public void postHandle( final HttpServletRequest request, final HttpServletResponse response )
                throws Exception
            {
            }
        };
    }

    @Override
    protected HttpInterceptorExtensions createExtensionPoint()
    {
        return new HttpInterceptorExtensions();
    }
}
