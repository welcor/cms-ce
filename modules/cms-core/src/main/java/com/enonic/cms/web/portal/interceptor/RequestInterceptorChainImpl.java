package com.enonic.cms.web.portal.interceptor;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.web.portal.PortalWebContext;

@Component
public final class RequestInterceptorChainImpl
    implements RequestInterceptorChain
{
    private List<RequestInterceptor> list;

    @Override
    public boolean preHandle( final PortalWebContext context )
        throws Exception
    {
        for ( final RequestInterceptor entry : this.list )
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
        for ( final RequestInterceptor entry : this.list )
        {
            entry.postHandle( context );
        }
    }

    @Autowired
    public void setInterceptors( final RequestInterceptor... list )
    {
        this.list = Lists.newArrayList( list );
        Collections.sort( this.list, new AnnotationAwareOrderComparator() );
    }
}
