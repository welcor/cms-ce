/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.xslt.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;

import com.google.common.io.CharStreams;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

public abstract class AbstractXsltFunctionTest<T extends XsltFunctionLibrary>
{
    private TransformerFactoryImpl transformerFactory;

    protected T functionLibrary;

    @Before
    public void setUp()
    {
        XMLUnit.setIgnoreWhitespace( true );
        XMLUnit.setNormalize( true );
        XMLUnit.setNormalizeWhitespace( true );

        this.transformerFactory = new TransformerFactoryImpl();
        final Configuration config = this.transformerFactory.getConfiguration();

        this.functionLibrary = newFunctionLibrary();
        this.functionLibrary.register( config );
    }

    protected abstract T newFunctionLibrary();

    protected final void processTemplate( final String baseName )
        throws Exception
    {
        final InputStream expectedIn = getClass().getResourceAsStream( baseName + "Result.xml" );

        final String target = executeTemplate( baseName + ".xsl" );
        final String expected = CharStreams.toString( new InputStreamReader( expectedIn ) );

        XMLAssert.assertXMLEqual( "Results differ", expected, target );
    }

    protected final String executeTemplate( final String templateName )
        throws Exception
    {
        final Source source = loadStreamSource( "sourceInputDoc.xml" );
        final Source stylesheet = loadStreamSource( templateName );

        final StringWriter out = new StringWriter();
        final StreamResult result = new StreamResult( out );

        final Transformer transformer = this.transformerFactory.newTransformer( stylesheet );
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer.transform( source, result );

        return out.toString();
    }

    private Source loadStreamSource( final String name )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( name );
        return new StreamSource( in );
    }
}
