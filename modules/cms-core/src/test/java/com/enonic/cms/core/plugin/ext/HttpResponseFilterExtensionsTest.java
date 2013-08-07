package com.enonic.cms.core.plugin.ext;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;

public class HttpResponseFilterExtensionsTest
    extends HttpProcessorExtensionsTest<HttpResponseFilter, HttpResponseFilterExtensions>
{
    public HttpResponseFilterExtensionsTest()
    {
        super( HttpResponseFilter.class );
    }

    @Override
    protected HttpResponseFilter createExt()
    {
        return new HttpResponseFilter()
        {
            @Override
            public String filterResponse( final HttpServletRequest request, final String response, final String contentType )
                throws Exception
            {
                return null;
            }
        };
    }

    @Override
    protected HttpResponseFilterExtensions createExtensionPoint()
    {
        return new HttpResponseFilterExtensions();
    }
}
