package com.enonic.cms.core.plugin.ext;

import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.TextExtractor;

@Component
public final class TextExtractorExtensions
    extends ExtensionPoint<TextExtractor>
{
    public TextExtractorExtensions()
    {
        super( TextExtractor.class );
    }

    public TextExtractor getByMimeType( final String mimeType )
    {
        for ( final TextExtractor ext : this )
        {
            if ( ext.canHandle( mimeType ) )
            {
                return ext;
            }
        }

        return null;
    }

    @Override
    protected String toHtml( final TextExtractor ext )
    {
        return composeHtml( ext );
    }

    @Override
    public int compare( final TextExtractor o1, final TextExtractor o2 )
    {
        return o1.getDisplayName().compareTo( o2.getDisplayName() );
    }
}
