package com.enonic.cms.core.portal.rendering;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticSearchException;
import org.junit.Before;
import org.junit.Test;

public class RenderedWindowResultTest
{


    @Before
    public void setUp()
    {

    }

    @Test
    public void testStuff()
    {

        RenderedWindowResult result = new RenderedWindowResult();

        result.setContent( getContentFromFile() );

        result.stripXHTMLNamespaces();

    }


    public String getContentFromFile()
    {
        InputStream stream = RenderedWindowResultTest.class.getResourceAsStream( "largeContentFile.txt" );

        if ( stream == null )
        {
            throw new ElasticSearchException( "File not found: " + "largeContentFile.txt" );
        }

        StringWriter writer = new StringWriter();
        try
        {

            IOUtils.copy( stream, writer, "UTF-8" );
            final String mapping = writer.toString();

            //System.out.println( "Mapping file loaded: " + mapping );

            return mapping;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to get mapping-file as stream", e );
        }

    }

}
