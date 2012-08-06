package com.enonic.cms.core.xslt;

import java.net.URI;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public final class XsltProcessorError
{
    public final TransformerException exception;

    public XsltProcessorError( final TransformerException exception )
    {
        this.exception = exception;
    }

    public String getMessage()
    {
        return this.exception.getMessage();
    }

    public String getLocation()
    {
        final SourceLocator locator = this.exception.getLocator();
        final int lineNumber = locator.getLineNumber();
        final String systemId = locator.getSystemId();

        final StringBuilder str = new StringBuilder();
        str.append( prettifySource( systemId ) );

        if ( lineNumber > -1 )
        {
            str.append( " [" ).append( lineNumber ).append( "]" );
        }

        return str.toString();
    }

    public String getMessageAndLocation()
    {
        return getMessage() + " @ " + getLocation();
    }

    public String toString()
    {
        return getMessageAndLocation();
    }

    private String prettifySource( final String source )
    {
        try
        {
            final URI uri = new URI( source );
            final String path = uri.getPath();
            return removeBeginSlash( path );
        }
        catch ( final Exception e )
        {
            return source;
        }
    }

    private String removeBeginSlash( final String source )
    {
        if ( source.length() == 0 )
        {
            return source;
        }

        if ( source.charAt( 0 ) == '/' )
        {
            return removeBeginSlash( source.substring( 1 ) );
        }

        return source;
    }
}
