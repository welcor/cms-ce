/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.urlrewrite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class UrlRewriterHttpServletRequestWrapper
    extends HttpServletRequestWrapper
{
    private String fullTargetPath;

    public UrlRewriterHttpServletRequestWrapper( HttpServletRequest req, String fullTargetPath )
    {
        super( req );
        this.fullTargetPath = fullTargetPath;
    }

    public String getRequestURI()
    {
        String s = fullTargetPath;
        if ( s == null )
        {
            s = super.getRequestURI();
        }
        return s;
    }

    public String getPathInfo()
    {
        return getRequestURI();
    }

    public String getServletPath()
    {
        return "";
    }
}