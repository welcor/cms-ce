/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.VerticalRuntimeException;

public final class VerticalEngineLogger
    extends VerticalLogger
{
    public static void errorSecurity( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalSecurityException( message, throwable );
    }

    public static void errorSecurity( String message, Object[] msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalSecurityException( format( message, msgData ), throwable );
    }

    public static void errorCopy( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalRuntimeException( message, throwable );
    }

    public static void errorCreate( String message, Object[] msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalCreateException( format( message, msgData ), throwable );
    }

    public static void errorCreate( String message, Object msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalCreateException( format( message, msgData ), throwable );
    }

    public static void errorCreate( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalCreateException( message, throwable );
    }

    public static void errorRemove( String message, Object[] msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalRemoveException( format( message, msgData ), throwable );
    }

    public static void errorRemove( String message, Object msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalRemoveException( format( message, msgData ), throwable );
    }

    public static void errorRemove( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalRemoveException( message, throwable );
    }

    public static void errorUpdate( String message, Object[] msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalUpdateException( format( message, msgData ), throwable );
    }

    public static void errorUpdate( String message, Object msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalUpdateException( format( message, msgData ), throwable );
    }

    public static void errorUpdate( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalUpdateException( message, throwable );
    }

    public static void fatalEngine( String message, Object[] msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalRuntimeException( format( message, msgData ), throwable );
    }

    public static void fatalEngine( String message, Object msgData, Throwable throwable )
    {
        error( message, msgData, throwable );
        throw new VerticalRuntimeException( format( message, msgData ), throwable );
    }

    public static void fatalEngine( String message, Throwable throwable )
    {
        error( message, throwable );
        throw new VerticalRuntimeException( message, throwable );
    }
}
