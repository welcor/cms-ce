/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;

public abstract class AbstractToolController
    implements Controller
{
    private SecurityService securityService;

    private ViewResolver viewResolver;

    @Override
    public final ModelAndView handleRequest( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final User user = this.securityService.getLoggedInAdminConsoleUser();
        if ( user == null )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            // redirectToLogin( req, res );
            return null;
        }

        if ( req.getMethod().equalsIgnoreCase( "GET" ) )
        {
            doGet( req, res );
        }
        else if ( req.getMethod().equalsIgnoreCase( "POST" ) )
        {
            doPost( req, res );
        }
        else
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
        }

        return null;
    }

    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
    }

    protected void doPost( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
    }

    private void redirectToLogin( final HttpServletRequest req, final HttpServletResponse res )
    {
        AdminHelper.redirectClientToAdminPath( req, res, "login", new MultiValueMap() );
    }

    protected final void redirectToReferrer( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final URL url = new URL( req.getHeader( "referer" ) );
        AdminHelper.redirectToURL( url, res );
    }

    protected final void renderView( final HttpServletRequest req, final HttpServletResponse res, final Map<String, Object> model,
                                     final String templateName )
        throws Exception
    {
        res.setContentType( "text/html; charset=utf-8" );
        final View view = this.viewResolver.resolveViewName( templateName, Locale.getDefault() );
        view.render( model, req, res );
    }

    protected final String getBaseUrl( final HttpServletRequest req )
    {
        return AdminHelper.getAdminPath( req, true );
    }

    protected final void renderJson( final HttpServletResponse res, final JsonNode node )
        throws Exception
    {
        renderJson( res, node.toString() );
    }

    protected final void renderJson( final HttpServletResponse res, final String json )
        throws Exception
    {
        res.setContentType( "application/json; charset=utf-8" );
        res.getWriter().println( json );
    }

    @Autowired
    public final void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Autowired
    public final void setViewResolver( final ViewResolver viewResolver )
    {
        this.viewResolver = viewResolver;
    }
}
