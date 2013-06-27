/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.instanttrace;


import com.enonic.cms.core.Path;

public class InstantTracePathInspector
{
    public static final String[] TRACE_INFO_PATH_ELEMENTS = new String[]{"_itrace", "info"};

    public static final String[] AUTHENTICATE_PATH_ELEMENTS = new String[]{"_itrace", "authenticate"};

    public static final String[] RESOURCES_PATH_ELEMENTS = new String[]{"_itrace", "resources"};

    public static boolean isAuthenticationPagePath( final Path localPath )
    {
        return localPath.containsSubPath( AUTHENTICATE_PATH_ELEMENTS );
    }

    public static boolean isTraceInfoPath( final Path localPath )
    {
        return localPath.containsSubPath( TRACE_INFO_PATH_ELEMENTS );
    }

    public static boolean isResourcePath( final Path localPath )
    {
        return localPath.containsSubPath( RESOURCES_PATH_ELEMENTS );
    }
}
