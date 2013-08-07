package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;

@Component
public final class HttpResponseFilterExtensions
    extends HttpProcessorExtensions<HttpResponseFilter>
{
    public HttpResponseFilterExtensions()
    {
        super( HttpResponseFilter.class );
    }
}
