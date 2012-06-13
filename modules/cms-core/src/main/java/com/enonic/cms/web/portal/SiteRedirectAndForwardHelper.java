/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.servlet.http.HttpServletRequestWrapper;

import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SiteURLResolver;

public class SiteRedirectAndForwardHelper
{
    private SiteURLResolver siteURLResolver;

    private String replaceSpacesWithPlus( String path )
    {
        return path.replaceAll( " ", "+" );
    }

    @Autowired
    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public void forward( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws ServletException, IOException
    {
        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, false );
        path = replaceSpacesWithPlus( path );

        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request, sitePath.getParams() );
        request.getRequestDispatcher( path ).forward( wrappedRequest, response );
    }
}
