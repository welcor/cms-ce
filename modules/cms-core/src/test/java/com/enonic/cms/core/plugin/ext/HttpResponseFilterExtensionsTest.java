package com.enonic.cms.core.plugin.ext;

import org.mockito.Mockito;

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
        return Mockito.mock( HttpResponseFilter.class );
    }

    @Override
    protected HttpResponseFilterExtensions createExtensionPoint()
    {
        return new HttpResponseFilterExtensions();
    }
}
