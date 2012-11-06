/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.webdav;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

public final class SimpleDavServlet
    extends AbstractWebdavServlet
{
    private final static String AUTHENTICATE_HEADER_VALUE = "Basic Realm=\"Enonic Webdav Server\"";

    private DavSessionProvider sessionProvider;

    private DavLocatorFactory locatorFactory;

    private DavResourceFactory resourceFactory;

    public void init( final ServletConfig config )
        throws ServletException
    {
        final ServletContext servletContext = config.getServletContext();
        final DavConfiguration configuration = getConfiguration( servletContext );

        setLocatorFactory( new DavLocatorFactoryImpl() );
        setDavSessionProvider( new DavSessionProviderImpl( configuration ) );
        setResourceFactory( new DavResourceFactoryImpl( configuration ) );

        super.init( config );
    }

    private DavConfiguration getConfiguration( final ServletContext context )
    {
        final WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext( context );
        return appContext.getBean( DavConfiguration.class );
    }

    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        ServletRequestAccessor.setRequest( request );
        super.service( request, response );
    }

    @Override
    protected boolean isPreconditionValid( final WebdavRequest req, final DavResource resource )
    {
        return true;
    }

    @Override
    public DavSessionProvider getDavSessionProvider()
    {
        return this.sessionProvider;
    }

    @Override
    public void setDavSessionProvider( final DavSessionProvider sessionProvider )
    {
        this.sessionProvider = sessionProvider;
    }

    @Override
    public DavLocatorFactory getLocatorFactory()
    {
        return this.locatorFactory;
    }

    @Override
    public void setLocatorFactory( final DavLocatorFactory locatorFactory )
    {
        this.locatorFactory = locatorFactory;
    }

    @Override
    public DavResourceFactory getResourceFactory()
    {
        return this.resourceFactory;
    }

    @Override
    public void setResourceFactory( final DavResourceFactory resourceFactory )
    {
        this.resourceFactory = resourceFactory;
    }

    @Override
    public String getAuthenticateHeaderValue()
    {
        return AUTHENTICATE_HEADER_VALUE;
    }
}
