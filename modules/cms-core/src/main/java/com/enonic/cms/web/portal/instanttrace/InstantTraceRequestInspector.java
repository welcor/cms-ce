package com.enonic.cms.web.portal.instanttrace;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.Path;


public final class InstantTraceRequestInspector
{
    public static boolean isClientEnabled( final HttpServletRequest request )
    {
        return "true".equals( request.getHeader( "X-Instant-Trace-Client-Enabled" ) );
    }

    public static boolean isAuthenticationSubmitted( final HttpServletRequest request )
    {
        if ( !"POST".equalsIgnoreCase( request.getMethod() ) )
        {
            return false;
        }

        String userName = request.getParameter( "_itrace_username" );
        String password = request.getParameter( "_itrace_password" );
        if ( userName != null && password != null )
        {
            return true;
        }
        return false;
    }

    public static boolean isAuthenticationPageRequested( Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "authenticate" );
    }
}
