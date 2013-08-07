package com.enonic.cms.core.plugin.ext;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;

import static org.junit.Assert.*;

public class TextExtractorExtensionsTest
    extends ExtensionPointTest<TextExtractor, TextExtractorExtensions>
{
    public TextExtractorExtensionsTest()
    {
        super( TextExtractor.class );
    }

    @Override
    protected TextExtractorExtensions createExtensionPoint()
    {
        return new TextExtractorExtensions();
    }

    private TextExtractor create( final String displayName, final String canHandleType )
    {
        final TextExtractor ext = new TextExtractor()
        {
            @Override
            public boolean canHandle( final String mimeType )
            {
                return canHandleType.equals( mimeType );
            }

            @Override
            public String extractText( final String mimeType, final InputStream stream, final String encoding )
                throws IOException
            {
                return null;
            }
        };

        ext.setDisplayName( displayName );
        return ext;
    }

    @Override
    protected TextExtractor createOne()
    {
        return create( "a", "text/plain" );
    }

    @Override
    protected TextExtractor createTwo()
    {
        return create( "b", "text/html" );
    }

    @Test
    public void testGetByMimeType()
    {
        assertNull( this.extensions.getByMimeType( "text/plain" ) );

        this.extensions.addExtension( this.ext1 );
        assertSame( this.ext1, this.extensions.getByMimeType( "text/plain" ) );
    }
}
