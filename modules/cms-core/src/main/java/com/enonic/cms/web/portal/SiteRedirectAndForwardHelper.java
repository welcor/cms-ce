/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.portal;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.esl.servlet.http.HttpServletRequestWrapper;

import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.portal.mvc.view.SiteCustomForwardView;

@Component
public class SiteRedirectAndForwardHelper
{
    private SiteURLResolver siteURLResolver;

    private boolean replaceSpacesWithPlus = true;

    public void setReplaceSpacesWithPlus( boolean value )
    {
        this.replaceSpacesWithPlus = value;
    }

    private ModelAndView getForwardModelAndView(String path, Map<String, String[]> params)
    {
        if ( replaceSpacesWithPlus )
        {
            path = replaceSpacesWithPlus( path );
        }

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put( "path", path );
        model.put( "requestParams", params );
        return new ModelAndView( new SiteCustomForwardView(), model );
    }

    private String replaceSpacesWithPlus( String path )
    {
        return path.replaceAll( " ", "+" );
    }

    @Autowired
    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public ModelAndView getForwardModelAndView( HttpServletRequest request, SitePath sitePath )
    {
        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, false );
        return getForwardModelAndView(path, sitePath.getParams() );
    }

    public void forward( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception
    {
        String path = siteURLResolver.createPathWithinContextPath( request, sitePath, false );
        if ( replaceSpacesWithPlus )
        {
            path = replaceSpacesWithPlus( path );
        }

        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request, sitePath.getParams() );
        request.getRequestDispatcher( path ).forward( wrappedRequest, response );
    }
}
