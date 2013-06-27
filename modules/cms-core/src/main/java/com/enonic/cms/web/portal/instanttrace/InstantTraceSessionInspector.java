/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;


import javax.servlet.http.HttpSession;

public final class InstantTraceSessionInspector
{
    public final static String AUTHENTICATION_ATTRIBUTE_NAME = "Instant-Trace-Client-Authenticated";

    private final static String SESSION_OBJECT_ATTRIBUTE_NAME = "Instant-Trace-Client-Session";

    static void markAuthenticated( final HttpSession httpSession )
    {
        httpSession.setAttribute( AUTHENTICATION_ATTRIBUTE_NAME, "true" );
    }

    public static InstantTraceSessionObject getInstantTraceSessionObject( HttpSession httpSession )
    {
        InstantTraceSessionObject instantTraceSessionObject =
            (InstantTraceSessionObject) httpSession.getAttribute( InstantTraceSessionInspector.SESSION_OBJECT_ATTRIBUTE_NAME );

        if ( instantTraceSessionObject == null )
        {
            instantTraceSessionObject = new InstantTraceSessionObject();
            httpSession.setAttribute( SESSION_OBJECT_ATTRIBUTE_NAME, instantTraceSessionObject );
        }

        return instantTraceSessionObject;
    }
}
