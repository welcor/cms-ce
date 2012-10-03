package com.enonic.cms.web.portal.instanttrace;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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

    public static void setAttributeOriginalUrl( final Path localPath, final HttpServletRequest request )
    {
        request.setAttribute( "instantTrace.originalUrl", localPath.toString() );
    }

    public static InstantTraceId getInstantTraceId( final HttpServletRequest request )
    {
        final String s = request.getHeader( "X-Instant-Trace-Id" );
        if ( StringUtils.isBlank( s ) )
        {
            return null;
        }
        return new InstantTraceId( s );
    }

    public static String getParameterUsername( final HttpServletRequest request )
    {
        return request.getParameter( "_itrace_username" );
    }

    public static String getParameterPassword( final HttpServletRequest request )
    {
        return request.getParameter( "_itrace_password" );
    }

    public static String getParameterUserstore( final HttpServletRequest request )
    {
        return request.getParameter( "_itrace_userstore" );
    }

    public static String getParameterOriginalUrl( final HttpServletRequest request )
    {
        return request.getParameter( "_itrace_original_url" );
    }

    public static String getOriginalUrl( final HttpServletRequest request )
    {
        String attribute = (String) request.getAttribute( "itrace.originalUrl" );
        if ( StringUtils.isBlank( attribute ) )
        {
            return getParameterOriginalUrl( request );
        }
        return null;
    }
}
