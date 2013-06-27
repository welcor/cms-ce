/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.rendering;

import org.junit.Test;
import org.springframework.util.StopWatch;

import static org.junit.Assert.*;

public class RenderedWindowResultTest
{


    @Test
    public void testStripNamespacesNoNamespace()
    {

        RenderedWindowResult result = new RenderedWindowResult();

        result.setContent( createNoNamespaceContent() );

        result.stripXHTMLNamespaces();

        assertEquals( createNoNamespaceContent(), result.getContent() );


    }

    private String createNoNamespaceContent()
    {
        return "<html><title>test</title></html >";
    }

    @Test
    public void testStripNamespacesWithNamespace()
    {

        RenderedWindowResult result = new RenderedWindowResult();

        result.setContent( createContentWithNameSpaces() );

        result.stripXHTMLNamespaces();

        assertEquals( "<html><title>test</title></html><html >\" <html lang=\"en\" >", result.getContent() );

    }

    @Test
    public void testLotsOfWhiteSpaces()
    {

        RenderedWindowResult result = new RenderedWindowResult();

        result.setContent( createContentWithNameSpaces( 50000 ) );

        StopWatch timer = new StopWatch();
        timer.start( "strip" );
        result.stripXHTMLNamespaces();
        timer.stop();

        System.out.println( timer.prettyPrint() );

        assertEquals( "<html><title>test</title></html><html >\" <html lang=\"en\" >", result.getContent().trim() );

    }

    private String createContentWithNameSpaces()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(
            "<html><title>test</title></html><html xmlns=\"http://www.w3.org/1999/xhtml\">\" <html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">" );

        return builder.toString();


    }

    private String createContentWithNameSpaces( int numberOfWhitespaces )
    {
        StringBuilder builder = new StringBuilder();

        for ( int i = 0; i < numberOfWhitespaces; i++ )
        {
            builder.append( " " );
        }

        builder.append(
            "<html><title>test</title></html><html xmlns=\"http://www.w3.org/1999/xhtml\">\" <html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">" );

        for ( int i = 0; i < numberOfWhitespaces; i++ )
        {
            builder.append( " " );
        }

        return builder.toString();


    }



}
