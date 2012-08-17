/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.saxon;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SAXDestination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

final class XsltProcessorImpl
    implements XsltProcessor
{
    private final XsltExecutable executable;

    private boolean omitXmlDecl;

    private final Map<String, XdmAtomicValue> parameters;

    private final URIResolver uriResolver;

    public XsltProcessorImpl( final XsltExecutable executable, final URIResolver uriResolver )
    {
        this.executable = executable;
        this.parameters = Maps.newHashMap();
        this.uriResolver = uriResolver;
    }

    private String getOutputProperty( final String name )
    {
        return this.executable.getUnderlyingCompiledStylesheet().getOutputProperties().getProperty( name );
    }

    public String getOutputMethod()
    {
        return getOutputProperty( "method" );
    }

    public String getOutputMediaType()
    {
        return getOutputProperty( "media-type" );
    }

    public String getOutputEncoding()
    {
        return getOutputProperty( "encoding" );
    }

    public void setOmitXmlDecl( boolean omitXmlDecl )
    {
        this.omitXmlDecl = omitXmlDecl;
    }

    public String getContentType()
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

    /**
     * Process the xml with stylesheet.
     */
    public String process( final Source xml )
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

    public void process( Source xml, Result result )
        throws XsltProcessorException
    {
        if ( result instanceof DOMResult )
        {
            processDom( xml, (DOMResult) result );
        }
        else if ( result instanceof SAXResult )
        {
            processSax( xml, (SAXResult) result );
        }
        else if ( result instanceof StreamResult )
        {
            processStream( xml, (StreamResult) result );
        }
        else
        {
            throw new XsltProcessorException( "Cannot handle result of type [" + result.getClass().getName() + "]" );
        }
    }

    private void processDom( final Source xml, final DOMResult result )
        throws XsltProcessorException
    {
        final Document doc = XMLTool.createDocument();
        result.setNode( doc );

        final DOMDestination destination = new DOMDestination( doc );
        doProcess( xml, destination );
    }

    private void processSax( final Source xml, final SAXResult result )
        throws XsltProcessorException
    {
        final SAXDestination destination = new SAXDestination( result.getHandler() );
        doProcess( xml, destination );
    }

    private void processStream( final Source xml, final StreamResult result )
        throws XsltProcessorException
    {
        Writer writer = result.getWriter();
        if ( writer == null )
        {
            writer = new OutputStreamWriter( result.getOutputStream() );
        }

        process( xml, writer );
    }

    public void process( final Source xml, final Writer writer )
        throws XsltProcessorException
    {
        final Serializer serializer = new Serializer();
        serializer.setOutputWriter( writer );
        serializer.setOutputProperty( Serializer.Property.OMIT_XML_DECLARATION, this.omitXmlDecl ? "yes" : "no" );

        doProcess( xml, serializer );
    }

    private void doProcess( final Source xml, final Destination destination )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        final XsltTransformer transformer = this.executable.load();

        try
        {
            transformer.setSource( xml );
            transformer.setDestination( destination );
            transformer.getUnderlyingController().setErrorListener( errors );
            transformer.getUnderlyingController().setURIResolver( this.uriResolver );
            applyParameters( transformer );

            transformer.transform();

            // transformer.close();
            // destination.close();
        }
        catch ( final Exception e )
        {
            throw new XsltProcessorException( e, errors );
        }
    }

    private void applyParameters( final XsltTransformer transformer )
    {
        for ( final Map.Entry<String, XdmAtomicValue> entry : this.parameters.entrySet() )
        {
            transformer.setParameter( new QName( entry.getKey() ), entry.getValue() );
        }
    }

    public Object getParameter( final String name )
    {
        return this.parameters.get( name );
    }

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
        this.parameters.put( name, new XdmAtomicValue( value ) );
    }

    private void setFloatParameter( final String name, final float value )
    {
        this.parameters.put( name, new XdmAtomicValue( value ) );
    }

    private void setDoubleParameter( final String name, final double value )
    {
        this.parameters.put( name, new XdmAtomicValue( value ) );
    }

    private void setLongParameter( final String name, final long value )
    {
        this.parameters.put( name, new XdmAtomicValue( value ) );
    }

    private void setStringParameter( final String name, final String value )
    {
        try
        {
            this.parameters.put( name, new XdmAtomicValue( value, ItemType.ANY_ATOMIC_VALUE ) );
        }
        catch ( SaxonApiException e )
        {
            this.parameters.put( name, new XdmAtomicValue( value ) );
        }
    }

    public void clearParameters()
    {
        this.parameters.clear();
    }
}
