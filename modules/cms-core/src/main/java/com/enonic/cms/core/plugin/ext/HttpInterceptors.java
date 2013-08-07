package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;

@Component
public final class HttpInterceptors
    extends HttpProcessors<HttpInterceptor>
{
    public HttpInterceptors()
    {
        super( HttpInterceptor.class );
    }
}
