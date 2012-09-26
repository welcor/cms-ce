package com.enonic.cms.web.portal.instanttrace;


import javax.servlet.http.HttpSession;

public final class InstantTraceSessionInspector
{
    public final static String AUTHENTICATION_ATTRIBUTE_NAME = "Instant-Trace-Client-Authenticated";

    public final static String SESSION_OBJECT_ATTRIBUTE_NAME = "Instant-Trace";

    static void markAuthenticated( final HttpSession httpSession )
    {
        httpSession.setAttribute( AUTHENTICATION_ATTRIBUTE_NAME, "true" );
    }
}
