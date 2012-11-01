package com.enonic.cms.core.xslt.base;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import com.google.common.io.Closeables;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

public abstract class BaseXsltProcessor
    implements XsltProcessor
{
    protected final Transformer transformer;

    public BaseXsltProcessor( final Transformer transformer )
    {
        this.transformer = transformer;
    }

    @Override
    public final String getOutputMethod()
    {
        return this.transformer.getOutputProperty( OutputKeys.METHOD );
    }

    @Override
    public final String getOutputMediaType()
    {
        return this.transformer.getOutputProperty( OutputKeys.MEDIA_TYPE );
    }

    @Override
    public final String getOutputEncoding()
    {
        return this.transformer.getOutputProperty( OutputKeys.ENCODING );
    }

    @Override
    public final void setOmitXmlDecl( boolean omitXmlDecl )
    {
        this.transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, omitXmlDecl ? "yes" : "no" );
    }

    @Override
    public final String getContentType()
    {
        StringBuilder contentType = new StringBuilder();
        String outputMediaType = getOutputMediaType();
        if ( outputMediaType != null )
        {
            contentType.append( outputMediaType );
        }
        else
        {
            String outputMethod = getOutputMethod();
            if ( "xml".equals( outputMethod ) )
            {
                contentType.append( "text/xml" );
            }
            else if ( "html".equals( outputMethod ) || "xhtml".equals( outputMethod ) )
            {
                contentType.append( "text/html" );
            }
            else
            {
                contentType.append( "text/plain" );
            }
        }

        String outputEncoding = getOutputEncoding();
        contentType.append( "; charset=" );
        contentType.append( outputEncoding != null ? outputEncoding : "utf-8" );
        return contentType.toString();
    }

    @Override
    public final String process( final Source xml )
        throws XsltProcessorException
    {
        StringWriter writer = new StringWriter();

        try
        {
            process( xml, writer );
        }
        finally
        {
            Closeables.closeQuietly( writer );
        }

        return writer.toString();
    }

    @Override
    public final void process( final Source xml, final Result result )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();

        try
        {
            this.transformer.setErrorListener( errors );
            this.transformer.transform( xml, result );

            if ( errors.hasErrors() )
            {
                throw new XsltProcessorException( errors );
            }
        }
        catch ( final TransformerException e )
        {
            throw new XsltProcessorException( e, errors );
        }
    }

    private void process( final Source xml, final Writer writer )
        throws XsltProcessorException
    {
        final StreamResult result = new StreamResult( writer );
        process( xml, result );
    }

    @Override
    public void setParameter( final String name, final Object value )
    {
        if ( value == null )
        {
            return;
        }

        if ( value instanceof Boolean )
        {
            setBooleanParameter( name, (Boolean) value );
        }
        else if ( value instanceof Float )
        {
            setFloatParameter( name, (Float) value );
        }
        else if ( value instanceof Double )
        {
            setDoubleParameter( name, (Double) value );
        }
        else if ( value instanceof Number )
        {
            setLongParameter( name, ( (Number) value ).longValue() );
        }
        else
        {
            setStringParameter( name, value.toString() );
        }
    }

    private void setBooleanParameter( final String name, final boolean value )
    {
        this.transformer.setParameter( name, value );
    }

    private void setFloatParameter( final String name, final float value )
    {
        this.transformer.setParameter( name, value );
    }

    private void setDoubleParameter( final String name, final double value )
    {
        this.transformer.setParameter( name, value );
    }

    private void setLongParameter( final String name, final long value )
    {
        this.transformer.setParameter( name, value );
    }

    private void setStringParameter( final String name, final String value )
    {
        this.transformer.setParameter( name, createUntypedAtomicValue( value ) );
    }

    protected abstract Object createUntypedAtomicValue( String value );
}
