package com.enonic.cms.web.portal;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.elasticsearch.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.web.portal.exception.ExceptionHandler;
import com.enonic.cms.web.portal.handler.WebHandler;
import com.enonic.cms.web.portal.handler.WebHandlerRegistry;
import com.enonic.cms.web.portal.interceptor.RequestInterceptorChain;

@Component
public final class RequestDispatcherImpl
    implements RequestDispatcher
{
    private WebHandlerRegistry handlerRegistry;

    private ExceptionHandler exceptionHandler;

    private RequestInterceptorChain interceptorChain;

    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final PortalWebContext context = new PortalWebContext();
        context.setRequest( req );
        context.setResponse( res );
        context.setSitePath( resolveSitePath( req ) );

        handle( context );
    }

    private void handle( final PortalWebContext context )
        throws ServletException, IOException
    {
        try
        {
            final WebHandler handler = this.handlerRegistry.find( context );
            handle( context, handler );
        }
        catch ( final ServletException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            this.exceptionHandler.handle( context, e );
        }
    }

    private void handle( final PortalWebContext context, final WebHandler handler )
        throws Exception
    {
        if ( !this.interceptorChain.preHandle( context ) )
        {
            return;
        }

        try
        {
            handler.handle( context );
        }
        finally
        {
            this.interceptorChain.postHandle( context );
        }
    }

    @SuppressWarnings("unchecked")
    private SitePath resolveSitePath( final HttpServletRequest request )
    {
        final String path = request.getRequestURI();
        final Iterable<String> elements = Splitter.on( '/' ).omitEmptyStrings().split( path );
        final List<String> elementList = Lists.newArrayList( elements );

        elementList.remove( 0 );
        final SiteKey siteKey = new SiteKey( elementList.remove( 0 ) );

        String localPath = Joiner.on( '/' ).join( elementList );
        if ( path.endsWith( "/" ) )
        {
            localPath = localPath + "/";
        }

        return new SitePath( siteKey, localPath, request.getParameterMap() );
    }

    @Autowired
    public void setHandlerRegistry( final WebHandlerRegistry handlerRegistry )
    {
        this.handlerRegistry = handlerRegistry;
    }

    @Autowired
    public void setExceptionHandler( final ExceptionHandler exceptionHandler )
    {
        this.exceptionHandler = exceptionHandler;
    }

    @Autowired
    public void setInterceptorChain( final RequestInterceptorChain interceptorChain )
    {
        this.interceptorChain = interceptorChain;
    }
}
