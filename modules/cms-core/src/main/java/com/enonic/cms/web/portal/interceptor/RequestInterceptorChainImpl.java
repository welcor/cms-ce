package com.enonic.cms.web.portal.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.web.portal.PortalWebContext;

@Component
public final class RequestInterceptorChainImpl
    implements RequestInterceptorChain
{
    private AutoLoginInterceptor autoLoginInterceptor;

    private BasicAuthInterceptor basicAuthInterceptor;

    private HttpInterceptorInterceptor httpInterceptorInterceptor;

    private Iterable<RequestInterceptor> getChain()
    {
        return Lists.newArrayList( this.autoLoginInterceptor, this.basicAuthInterceptor, this.httpInterceptorInterceptor );
    }

    @Override
    public boolean preHandle( final PortalWebContext context )
        throws Exception
    {
        for ( final RequestInterceptor entry : getChain() )
        {
            if ( !entry.preHandle( context ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle( final PortalWebContext context )
        throws Exception
    {
        for ( final RequestInterceptor entry : getChain() )
        {
            entry.postHandle( context );
        }
    }

    @Autowired
    public void setAuthLoginInterceptor( final AutoLoginInterceptor interceptor )
    {
        this.autoLoginInterceptor = interceptor;
    }

    @Autowired
    public void setBasicAuthInterceptor( final BasicAuthInterceptor interceptor )
    {
        this.basicAuthInterceptor = interceptor;
    }

    @Autowired
    public void setHttpInterceptorInterceptor( final HttpInterceptorInterceptor interceptor )
    {
        this.httpInterceptorInterceptor = interceptor;
    }
}
