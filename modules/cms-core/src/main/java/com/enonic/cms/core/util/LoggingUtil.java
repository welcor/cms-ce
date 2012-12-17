package com.enonic.cms.core.util;

import java.text.MessageFormat;

public final class LoggingUtil
{
    public static String format( final String message, final Object... args )
    {
        if ( args == null || args.length == 0 )
        {
            return message;
        }
        else
        {
            return MessageFormat.format( message, args );
        }
    }

    public static String formatCause( final String message, final Throwable cause, final Object... args )
    {
        if ( cause == null )
        {
            return format( message, args );
        }
        else
        {
            return formatThrowable( format( message, args ), cause );
        }
    }

    private static String formatThrowable( final String message, final Throwable cause )
    {
        if ( cause == null )
        {
            return message;
        }

        final int index = message.indexOf( "%t" );

        if ( index >= 0 )
        {
            final String msg = cause != null ? cause.getMessage() : null;
            final String text = msg != null ? msg : "null";

            final StringBuilder stringBuilder = new StringBuilder( message );
            stringBuilder.replace( index, index + 2, text );
            return stringBuilder.toString();
        }

        return message;
    }
}
