package com.enonic.cms.core.xslt.base;

import static org.junit.Assert.*;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import com.enonic.cms.core.xslt.XsltProcessor;

public abstract class BaseXsltProcessorTest<T extends XsltProcessor>
{
    protected abstract Transformer createTransformer( final Source source )
        throws Exception;

    protected abstract T createProcessor( Transformer transformer )
        throws Exception;

    protected final T createProcessor( final Class<?> loader, final String xslName )
        throws Exception
    {
        final Source source = loadXml( loader, xslName );
        final Transformer transformer = createTransformer( source );
        return createProcessor( transformer );
    }

    protected final Source loadXml( final Class<?> loader, final String xmlName )
        throws Exception
    {
        final InputStream in = loader.getResourceAsStream( xmlName );
        assertNotNull( "File [" + xmlName + "] does not exist", in );
        return new StreamSource( in );
    }

    protected final String doProcess( final XsltProcessor processor )
        throws Exception
    {
        return processor.process( loadXml( BaseXsltProcessor.class, "process_input.xml" ) );
    }

    @Test
    public final void testOutputProperties_none()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_none.xsl" );
        assertNotNull( processor );
        assertEquals( "text/plain; charset=utf-8", processor.getContentType() );
        assertEquals( null, processor.getOutputEncoding() );
        assertEquals( null, processor.getOutputMediaType() );
        assertEquals( null, processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_mediaType()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_mediaType.xsl" );
        assertNotNull( processor );
        assertEquals( "application/json; charset=utf-8", processor.getContentType() );
        assertEquals( null, processor.getOutputEncoding() );
        assertEquals( "application/json", processor.getOutputMediaType() );
        assertEquals( null, processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_method_html()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_method_html.xsl" );
        assertNotNull( processor );
        assertEquals( "text/html; charset=utf-8", processor.getContentType() );
        assertEquals( null, processor.getOutputEncoding() );
        assertEquals( null, processor.getOutputMediaType() );
        assertEquals( "html", processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_method_xhtml()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_method_xhtml.xsl" );
        assertNotNull( processor );
        assertEquals( "text/html; charset=utf-8", processor.getContentType() );
        assertEquals( null, processor.getOutputEncoding() );
        assertEquals( null, processor.getOutputMediaType() );
        assertEquals( "xhtml", processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_method_xml()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_method_xml.xsl" );
        assertNotNull( processor );
        assertEquals( "text/xml; charset=utf-8", processor.getContentType() );
        assertEquals( null, processor.getOutputEncoding() );
        assertEquals( null, processor.getOutputMediaType() );
        assertEquals( "xml", processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_encoding()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_encoding.xsl" );
        assertNotNull( processor );
        assertEquals( "text/plain; charset=ISO-8859-1", processor.getContentType() );
        assertEquals( "ISO-8859-1", processor.getOutputEncoding() );
        assertEquals( null, processor.getOutputMediaType() );
        assertEquals( null, processor.getOutputMethod() );
    }

    @Test
    public final void testOutputProperties_all()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "outputProperties_all.xsl" );
        assertNotNull( processor );
        assertEquals( "application/rss+xml; charset=ISO-8859-1", processor.getContentType() );
        assertEquals( "ISO-8859-1", processor.getOutputEncoding() );
        assertEquals( "application/rss+xml", processor.getOutputMediaType() );
        assertEquals( "xml", processor.getOutputMethod() );
    }

    @Test
    public final void testProcess_simple()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "process_simple.xsl" );
        assertNotNull( processor );

        final String result = doProcess( processor );
        assertEquals( "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy a=\"1\" b=\"2\"/>", result );
    }

    @Test
    public final void testProcess_omitXmlDecl()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "process_simple.xsl" );
        assertNotNull( processor );
        processor.setOmitXmlDecl( true );

        final String result = doProcess( processor );
        assertEquals( "<dummy a=\"1\" b=\"2\"/>", result );
    }

    @Test
    public final void testProcess_parameters()
        throws Exception
    {
        final XsltProcessor processor = createProcessor( BaseXsltProcessorTest.class, "process_simple.xsl" );
        assertNotNull( processor );
        processor.setOmitXmlDecl( true );

        processor.setParameter( "param1", 3 );
        processor.setParameter( "param2", true );

        final String result1 = doProcess( processor );
        assertEquals( "<dummy a=\"3\" b=\"true\"/>", result1 );

        processor.setParameter( "param1", 3.1 );
        processor.setParameter( "param2", "b" );

        final String result2 = doProcess( processor );
        assertEquals( "<dummy a=\"3.1\" b=\"b\"/>", result2 );

        processor.setParameter( "param1", 3.2f );
        processor.setParameter( "param2", null );

        final String result3 = doProcess( processor );
        assertEquals( "<dummy a=\"3.2\" b=\"b\"/>", result3 );
    }
}
