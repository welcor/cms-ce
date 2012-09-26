package com.enonic.cms.web.portal.instanttrace;


import javax.servlet.http.HttpServletResponse;

public final class InstantTraceResponseWriter
{
    private static final Integer charsPrHeader = 1000;

    private static final String HEADER_NAME = "Instant-Trace-Id";

    private static final String HEADER_NAME2 = "Instant-Trace-Info-";

    public static void applyTraceInfo( final HttpServletResponse response, final InstantTraceId instantTraceId )
    {
        response.setHeader( HEADER_NAME, instantTraceId.toString() );
    }

    public static void applyTraceInfo2( final HttpServletResponse response, final String traceInfo )
    {
        final int charLength = traceInfo.length();
        final double numberOfHeadersAsDouble = Math.ceil( charLength / charsPrHeader.doubleValue() );
        final int numberOfHeaders = (int) ( numberOfHeadersAsDouble );

        for ( int i = 0; i < numberOfHeaders; i++ )
        {
            final int beginIndex = charsPrHeader * i;

            int endIndex = beginIndex + charsPrHeader;
            if ( endIndex >= charLength )
            {
                endIndex = beginIndex + ( charLength - beginIndex );
            }

            final String value = traceInfo.substring( beginIndex, endIndex );

            response.setHeader( createResponseHeaderName( i ), value );
        }
    }

    private static String createResponseHeaderName( final int index )
    {
        final int position = index + 1;

        if ( position < 10 )
        {
            return HEADER_NAME2 + "0" + position;
        }
        else
        {
            return HEADER_NAME2 + position;
        }
    }
}
