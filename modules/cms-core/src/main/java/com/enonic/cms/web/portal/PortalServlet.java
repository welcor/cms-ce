/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.server.service.servlet.OriginalUrlResolver;

public final class PortalServlet
    extends HttpServlet
{
    private final static List<HttpMethod> ALLOWED_HTTP_METHODS =
        Arrays.asList( HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private RequestDispatcher dispatcher;

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        super.init( config );

        final ServletContext context = config.getServletContext();
        final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext( context );
        this.dispatcher = springContext.getBean( RequestDispatcher.class );
    }

    @Override
    protected void doOptions( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        response.setHeader( "Allow", StringUtils.join( ALLOWED_HTTP_METHODS, "," ) );
        response.setStatus( HttpServletResponse.SC_OK );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final HttpMethod requestMethod = HttpMethod.valueOf( req.getMethod() );

        if ( !ALLOWED_HTTP_METHODS.contains( requestMethod ) )
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
            return;
        }

        if ( requestMethod.equals( HttpMethod.OPTIONS ) )
        {
            doOptions( req, res );
            return;
        }

        ServletRequestAccessor.setRequest( req );
        OriginalUrlResolver.resolveOriginalUrl( req );

        this.dispatcher.handle( req, res );
    }
}
