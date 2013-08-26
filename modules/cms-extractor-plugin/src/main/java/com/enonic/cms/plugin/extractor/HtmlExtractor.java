package com.enonic.cms.plugin.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import com.enonic.cms.api.plugin.ext.TextExtractor;

public class HtmlExtractor
    extends TextExtractor
{
    @Override
    public boolean canHandle( final String mimeType )
    {
        return "text/html".equals( mimeType );
    }

    @Override
    public String extractText( final String mimeType, final InputStream inputStream, final String encoding )
        throws IOException
    {
        if ( !canHandle( mimeType ) )
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        Document doc = Jsoup.parse( inputStream, encoding, "" );

        for ( Element element : doc.getAllElements() )
        {
            for ( TextNode textNode : element.textNodes() )
            {
                final String text = textNode.text();
                builder.append( text );
                appendWhitespaceAfterTextIfNotThere( builder, text );
            }
        }

        return builder.toString();
    }

    private void appendWhitespaceAfterTextIfNotThere( final StringBuilder builder, final String text )
    {
        if ( text != null && !text.isEmpty() && !text.endsWith( " " ) )
        {
            builder.append( " " );
        }
    }

}
