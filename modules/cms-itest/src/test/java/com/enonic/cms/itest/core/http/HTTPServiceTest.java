/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.core.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.core.http.HTTPService;

import static org.junit.Assert.*;

public class HTTPServiceTest
{
    private final static String SAMPLE_TEXT_RESPONSE = "sample text response with special chars: \u00C5\u00F8 \u00E9";

    private final static String SAMPLE_XML_RESPONSE =
        "<base><node1>H\u00e6?</node1><node2>\u00c6\u00d8\u00c5</node2><node3>Citro\u00ebn est d\u00e9go\u00fbtant</node3></base>";

    private HTTPService httpService;

    private HttpServer httpServer;

    private String responseType;

    private byte[] responseBytes;

    @Before
    public void before()
        throws Exception
    {
        final InetSocketAddress address = new InetSocketAddress( 9999 );

        this.httpServer = HttpServer.create( address, 0 );
        this.httpServer.createContext( "/test", new HttpHandler()
        {
            @Override
            public void handle( final HttpExchange httpExchange )
                throws IOException
            {
                httpExchange.getResponseHeaders().set( "Content-Type", responseType );
                httpExchange.sendResponseHeaders( 200, responseBytes.length );
                httpExchange.getResponseBody().write( responseBytes );
                httpExchange.close();
            }
        } );

        this.httpService = new HTTPService();
        this.httpService.setUserAgent( "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)" );
        this.httpServer.start();
    }

    @After
    public void after()
    {
        httpServer.stop( 0 );
    }

    @Test
    public void get_url_as_text_test()
        throws Exception
    {
        setResponse( SAMPLE_TEXT_RESPONSE, "utf8", "text/plain" );
        final String result = this.httpService.getURL( buildServerUrl( "/test" ), "utf8", 5000 );
        assertEquals( SAMPLE_TEXT_RESPONSE, result );
    }

    @Test
    public void get_win1252_response_test()
        throws Exception
    {
        setResponse( SAMPLE_TEXT_RESPONSE, "cp1252", "text/plain" );
        final String result = this.httpService.getURL( buildServerUrl( "/test" ), "cp1252", 5000 );
        assertEquals( SAMPLE_TEXT_RESPONSE, result );
    }

    @Test
    public void get_win1252_respons_when_encoding_is_not_known_test()
        throws Exception
    {
        // This is the typical situation when calls to getUrlAsText or getUrlAsXML are made from the datasource.
        // The datasource does not know the encoding of the source, so we need to do something to detect it in "getUrl".

        setResponse( "<?xml version=\"1.0\" encoding=\"Windows-1252\" ?>" + SAMPLE_XML_RESPONSE, "cp1252", "text/xml" );

        final byte[] httpResult = this.httpService.getURLAsBytes( buildServerUrl( "/test" ), 5000 );
        final ByteArrayInputStream byteStream = new ByteArrayInputStream( httpResult );
        final Document resultDoc = XMLTool.domparse( byteStream );
        final String resultXML = XMLTool.documentToString( resultDoc );

        final int xmlBodyStart = resultXML.indexOf( "<base>" );
        final String xmlBody = resultXML.substring( xmlBodyStart );
        assertEquals( SAMPLE_XML_RESPONSE, xmlBody );
    }

    @Test
    public void get_url_as_text_wrong_url_test()
        throws Exception
    {
        final String result = this.httpService.getURL( buildServerUrl( "/unknown" ), null, 5000 );
        assertNull( result );
    }

    private String buildServerUrl( final String path )
    {
        final StringBuilder str = new StringBuilder( "http://localhost:" );
        str.append( this.httpServer.getAddress().getPort() ).append( path );
        return str.toString();
    }

    private void setResponse( final String text, final String encoding, final String type )
        throws Exception
    {
        this.responseBytes = text.getBytes( encoding );
        this.responseType = type;
    }
}
