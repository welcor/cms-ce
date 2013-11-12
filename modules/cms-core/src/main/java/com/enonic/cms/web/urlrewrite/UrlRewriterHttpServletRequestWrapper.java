/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.urlrewrite;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Helps to integrate internal VirtualHostFilter with urlrewritefilter.
 */
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

    /**
     * <p>If someone calls getRequestDispatcher on some path (like urlrewritefilter does) he
     * is probably going to forward to the path, so fullTargetPath must be updated.</p>
     *
     * <p>It is better to update fullTargetPath inside forward or include but current
     * solution is enough for urlrewritefilter</p>
     *
     * <p>Tested with urlrewritefilter-3.0.4</p>
     *
     * @see org.tuckey.web.filters.urlrewrite.NormalRewrittenUrl
     */
    public RequestDispatcher getRequestDispatcher( final String path )
    {
        fullTargetPath = path;

        return super.getRequestDispatcher( path );
    }
}