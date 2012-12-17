package com.enonic.cms.core.plugin.hook;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.FrameworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FrameworkLogImpl
    implements FrameworkLog
{
    private final static Logger LOG = LoggerFactory.getLogger( FrameworkLogImpl.class );

    public void log( final FrameworkEvent event )
    {
    }

    public void log( final FrameworkLogEntry entry )
    {
        final String message = entry.getMessage();
        final Throwable cause = entry.getThrowable();
        final int severity = entry.getSeverity();
        
        switch (severity) {
            case FrameworkLogEntry.ERROR:
                LOG.error( message, cause );
                break;
            case FrameworkLogEntry.WARNING:
                LOG.warn( message, cause );
                break;
            case FrameworkLogEntry.INFO:
                LOG.info( message, cause );
                break;
            default:
                LOG.debug( message, cause );
        }
    }

    public void setWriter( final Writer newWriter, final boolean append )
    {
    }

    public void setFile( final File newFile, final boolean append )
        throws IOException
    {
    }

    public File getFile()
    {
        return null;
    }

    public void setConsoleLog( final boolean consoleLog )
    {
    }

    public void close()
    {
    }
}
