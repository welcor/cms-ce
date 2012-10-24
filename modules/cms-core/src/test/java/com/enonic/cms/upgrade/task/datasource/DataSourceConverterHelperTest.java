package com.enonic.cms.upgrade.task.datasource;

import java.net.URL;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataSourceConverterHelperTest
{
    private final DataSourceConverterHelper helper;

    public DataSourceConverterHelperTest()
    {
        this.helper = new DataSourceConverterHelper( new DataSourceConverter()
        {
            @Override
            public Element convert( final Element root )
                throws Exception
            {
                final Element elem = new Element( "datasources" );
                final Element subElem = new Element( "datasource" );
                subElem.setAttribute( "name", "dummy" );
                elem.addContent( subElem );
                return elem;
            }
        } );
    }

    @Test
    public void testConvert_page()
        throws Exception
    {
        testConvert( "page" );
        testConvert( "page_none" );
    }

    @Test
    public void testConvert_portlet()
        throws Exception
    {
        testConvert( "portlet" );
        testConvert( "portlet_none" );
    }

    protected final void testConvert( final String name )
        throws Exception
    {
        testConvert( name + ".xml", name + "_result.xml" );
    }

    private void testConvert( final String source, final String result )
        throws Exception
    {
        final String sourceDoc = toString( readDoc( source ) );
        final String resultDoc = toString( readDoc( result ) );
        final String convertedDoc = this.helper.convert( sourceDoc );

        assertEquals( resultDoc, convertedDoc );
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
