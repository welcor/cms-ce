/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import java.util.Collection;
import java.util.Formatter;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * This class implements the processor exception.
 */
public final class XsltProcessorException
    extends RuntimeException
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Error list.
     */
    private final XsltProcessorErrors errors;

    /**
     * Construct the errors.
     */
    public XsltProcessorException( final String message )
    {
        super( message );
        this.errors = new XsltProcessorErrors();
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( final XsltProcessorErrors errors )
    {
        super( "Errors occurred during transformation" );
        this.errors = errors;
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( final Throwable cause )
    {
        super( cause.getMessage(), cause );
        this.errors = new XsltProcessorErrors();
    }

    /**
     * Construct the errors.
     */
    public XsltProcessorException( final Throwable cause, final XsltProcessorErrors errors )
    {
        super( cause.getMessage(), cause );
        this.errors = errors;
    }

    /**
     * Return the errors.
     */
    public XsltProcessorErrors getErrors()
    {
        return this.errors;
    }

    @Override
    public Throwable getCause()
    {
        return null;
    }

    @Override
    public String getMessage()
    {
        return formatMessage( super.getMessage(), this.errors.getAllErrors() );
    }

    private static String formatMessage( final String heading, final Collection<TransformerException> errors )
    {
        if ( errors.isEmpty() )
        {
            return heading;
        }

        final Formatter fmt = new Formatter().format( heading ).format( ":%n" );

        int index = 1;
        for ( final TransformerException error : errors )
        {
            if ( error.getLocationAsString() != null )
            {
                fmt.format( "%s) %s (%s)%n", index++, error.getMessage(), getLocation( error.getLocator() ) );
            }
            else
            {
                fmt.format( "%s) %s (%s)%n", index++, error.getMessage(), "?" );
            }
        }

        return fmt.toString();
    }

    private static String getLocation( final SourceLocator locator )
    {
        final StringBuilder str = new StringBuilder();
        str.append( XsltResourceHelper.resolvePath( locator.getSystemId() ) );

        if ( locator.getLineNumber() >= 0 )
        {
            str.append( " #" ).append( locator.getLineNumber() );
        }

        return str.toString();
    }
}
