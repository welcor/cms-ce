/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.admin;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.transform.JDOMSource;

import com.google.common.io.Closeables;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorException;

public final class AdminXsltProcessorHelper
{
    private final AdminXsltProcessorFactory factory;

    private AdminXsltProcessor processor;

    private Source input;

    public AdminXsltProcessorHelper( final AdminXsltProcessorFactory factory )
    {
        this.factory = factory;
    }

    public AdminXsltProcessorHelper stylesheet( final Source xsl, final URIResolver resolver )
    {
        this.processor = this.factory.createProcessor( xsl, resolver );
        return this;
    }

    public AdminXsltProcessorHelper input( final Source input )
    {
        this.input = input;
        return this;
    }

    public AdminXsltProcessorHelper input( final org.jdom.Document input )
    {
        return input( new JDOMSource( input ) );
    }

    public AdminXsltProcessorHelper input( final org.w3c.dom.Document input )
    {
        return input( new DOMSource( input ) );
    }

    public AdminXsltProcessorHelper param( final String key, final Object value )
    {
        return param( key, value, false );
    }

    public AdminXsltProcessorHelper param( final String key, final Object value, boolean convertToString )
    {
        this.processor.setParameter( key, convertToString ? value.toString() : value );
        return this;
    }

    public AdminXsltProcessorHelper params( final Map<?, ?> map )
    {
        return params( map, false );
    }

    public AdminXsltProcessorHelper params( final Map<?, ?> map, boolean convertToString )
    {
        if ( map != null )
        {
            for ( final Object key : map.keySet() )
            {
                param( (String) key, map.get( key ), convertToString );
            }
        }

        return this;
    }

    public String process()
    {
        final StringWriter out = new StringWriter();

        try
        {
            process( out );
        }
        finally
        {
            Closeables.closeQuietly( out );
        }

        return out.toString();
    }

    public org.w3c.dom.Document processDom()
    {
        final DOMResult result = new DOMResult();
        process( result );
        return (org.w3c.dom.Document) result.getNode();
    }

    public void process( final Result result )
    {
        final XsltProcessor processor = processor();
        processor.process( this.input, result );
    }

    public void process( final Writer writer )
    {
        final StreamResult result = new StreamResult( writer );
        process( result );
    }

    public void process( final HttpServletResponse response )
    {
        try
        {
            response.setContentType( this.processor.getContentType() );
            process( response.getWriter() );
        }
        catch ( IOException e )
        {
            throw new XsltProcessorException( e );
        }
    }

    public AdminXsltProcessor processor()
    {
        return this.processor;
    }
}
