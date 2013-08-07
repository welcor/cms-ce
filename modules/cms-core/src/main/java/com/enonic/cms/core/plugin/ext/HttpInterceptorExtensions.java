package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;

@Component
public final class HttpInterceptorExtensions
    extends HttpProcessorExtensions<HttpInterceptor>
{
    public HttpInterceptorExtensions()
    {
        super( HttpInterceptor.class );
    }
}
