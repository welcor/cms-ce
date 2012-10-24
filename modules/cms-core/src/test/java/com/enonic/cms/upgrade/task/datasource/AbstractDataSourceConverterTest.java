package com.enonic.cms.upgrade.task.datasource;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractDataSourceConverterTest
{
    private final DataSourceConverter converter;

    public AbstractDataSourceConverterTest( final DataSourceConverter converter )
    {
        this.converter = converter;
    }

    protected final void testConvert( final String name )
        throws Exception
    {
        testConvert( name + ".xml", name + "_result.xml" );
    }

    private void testConvert( final String source, final String result )
        throws Exception
    {
        final Document sourceDoc = readDoc( source );
        final Document resultDoc = readDoc( result );
        final Document convertedDoc = testConvert( sourceDoc );

        final String resultStr = toString( resultDoc );
        final String convertedStr = toString( convertedDoc );

        assertEquals( resultStr, convertedStr );
    }

    private Document testConvert( final Document source )
        throws Exception
    {
        final Element target = this.converter.convert( source.getRootElement() );
        return new Document( target );
    }

    private Document readDoc( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name );
        assertNotNull( "Document [" + name + "] not found", url );

        final SAXBuilder builder = new SAXBuilder();
        return builder.build( url );
    }

    private String toString( final Document doc )
        throws Exception
    {
        final XMLOutputter out = new XMLOutputter();
        out.setFormat( Format.getPrettyFormat() );
        return out.outputString( doc );
    }
}
