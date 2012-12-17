/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import com.enonic.cms.core.util.LoggingUtil;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalLogger
{
    private final static Logger LOG = LoggerFactory.getLogger( VerticalLogger.class );

    public static void debug( String message )
    {
        LOG.debug( message );
    }

    public static void info( String message, Object msgData )
    {
        LOG.info( LoggingUtil.format( message, msgData ) );
    }

    public static void warn( String message, Object[] msgData )
    {
        LOG.warn( LoggingUtil.format( message, msgData ) );
    }

    public static void warn( String message, Object msgData, Throwable throwable )
    {
        LOG.warn( LoggingUtil.formatCause( message, throwable, msgData ), throwable );
    }

    public static void warn( String message, Throwable throwable )
    {
        LOG.warn( LoggingUtil.formatCause( message, throwable ), throwable );
    }

    public static void error( String message, Object[] msgData )
    {
        LOG.error( LoggingUtil.format( message, msgData ) );
    }

    public static void error( String message, Object msgData, Throwable throwable )
    {
        LOG.error( LoggingUtil.formatCause( message, throwable, msgData ), throwable );
    }

    public static void warn( String message )
    {
        LOG.warn( message );
    }

    public static void error( String message )
    {
        LOG.error( message );
    }

    public static void error( String message, Throwable throwable )
    {
        LOG.error( LoggingUtil.formatCause( message, throwable ), throwable );
    }

    protected static String format( String message, Object... msgData )
    {
        return MessageFormat.format( message, msgData );
    }
}
