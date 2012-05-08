package com.enonic.cms.core.portal.rendering;

import org.junit.Test;

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


    private String createContentWithNameSpaces()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(
            "<html><title>test</title></html><html xmlns=\"http://www.w3.org/1999/xhtml\">\" <html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">" );

        return builder.toString();


    }


}
