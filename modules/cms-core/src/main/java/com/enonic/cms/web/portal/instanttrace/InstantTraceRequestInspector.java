package com.enonic.cms.web.portal.instanttrace;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.PathAndParams;
import com.enonic.cms.core.PathAndParamsToStringBuilder;


public final class InstantTraceRequestInspector
{
    public static boolean isClientEnabled( final HttpServletRequest request )
    {
        return "true".equals( request.getHeader( "X-Instant-Trace-Enabled" ) );
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

    public static void setAttributeOriginalUrl( final PathAndParams pathAndParams, final HttpServletRequest request )
    {
        final PathAndParamsToStringBuilder builder = new PathAndParamsToStringBuilder();
        builder.setIncludeFragment( true );
        builder.setIncludeParamsInPath( true );
        String originalUrl = builder.toString( pathAndParams );
        request.setAttribute( "itrace.originalUrl", originalUrl );
    }

    public static InstantTraceId getInstantTraceId( final Path path )
    {
        String s = path.getPathElementAfter( InstantTracePathInspector.TRACE_INFO_PATH_ELEMENTS );
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
        String originalUrl = (String) request.getAttribute( "itrace.originalUrl" );
        if ( StringUtils.isBlank( originalUrl ) )
        {
            return getParameterOriginalUrl( request );
        }
        return originalUrl;
    }
}
