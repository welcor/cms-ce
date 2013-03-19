package com.enonic.cms.web.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.InvalidKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private PortalSitePathResolver sitePathResolver;

    public void handle( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final PortalWebContext context = new PortalWebContext();
        context.setRequest( req );
        context.setResponse( res );

        try
        {
            final SitePath sitePath = this.sitePathResolver.resolveSitePath( req );
            context.setSitePath( sitePath );
        }
        catch ( final InvalidKeyException e )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

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

    @Autowired
    public void setSitePathResolver( final PortalSitePathResolver sitePathResolver )
    {
        this.sitePathResolver = sitePathResolver;
    }
}
