/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalLogger;

public class VerticalAdminLogger
    extends VerticalLogger
{
    public static void errorAdmin( String message )
    {
        error( message );
        throw new VerticalAdminException( message );
    }

    public static void errorAdmin( String message, Object[] msgData )
    {
        error( message, msgData );
        throw new VerticalAdminException( format( message, msgData ) );
    }

    public static void errorAdmin( String message, Object msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalAdminException( format( message, msgData ), throwable );
    }

    public static void errorAdmin( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalAdminException( message, throwable );
    }
}

