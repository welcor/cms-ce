package com.enonic.cms.web.portal.instanttrace;


import com.enonic.cms.core.Path;

public class InstantTracePathInspector
{
    public static boolean isAuthenticationPagePath( final Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "authenticate" );
    }

    public static boolean isTraceInfoPath( final Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "info" );
    }

    public static boolean isResourcePath( final Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "resources" );
    }
}
