package com.enonic.cms.web.portal.instanttrace;


import javax.servlet.http.HttpServletResponse;

public final class InstantTraceResponseWriter
{
    private static final String HEADER_NAME = "X-Instant-Trace-Id";

    public static void applyInstantTraceId( final HttpServletResponse response, final InstantTraceId instantTraceId )
    {
        response.setHeader( HEADER_NAME, instantTraceId.toString() );
    }
}
