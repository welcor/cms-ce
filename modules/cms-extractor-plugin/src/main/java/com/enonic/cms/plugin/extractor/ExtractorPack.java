package com.enonic.cms.plugin.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.jackrabbit.extractor.CompositeTextExtractor;
import org.apache.jackrabbit.extractor.MsExcelTextExtractor;
import org.apache.jackrabbit.extractor.MsOutlookTextExtractor;
import org.apache.jackrabbit.extractor.MsPowerPointTextExtractor;
import org.apache.jackrabbit.extractor.MsWordTextExtractor;
import org.apache.jackrabbit.extractor.OpenOfficeTextExtractor;
import org.apache.jackrabbit.extractor.PlainTextExtractor;
import org.apache.jackrabbit.extractor.PngTextExtractor;
import org.apache.jackrabbit.extractor.RTFTextExtractor;
import org.apache.jackrabbit.extractor.XMLTextExtractor;

import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.plugin.extractor.xformat.MsExcelXslxTextExtractor;
import com.enonic.cms.plugin.extractor.xformat.MsPowerPointlPptxTextExtractor;
import com.enonic.cms.plugin.extractor.xformat.MsWordDocxTextExtractor;

public final class ExtractorPack
    extends TextExtractor
{
    private final CompositeTextExtractor extractor;

    private final Set<String> allowedMimeTypes;

    public ExtractorPack()
    {
        this.extractor = new CompositeTextExtractor();
        // HTML Extractor fails, dont use this
        //this.extractor.addTextExtractor( new HTMLTextExtractor() );
        this.extractor.addTextExtractor( new MsExcelTextExtractor() );
        this.extractor.addTextExtractor( new MsOutlookTextExtractor() );
        this.extractor.addTextExtractor( new MsPowerPointTextExtractor() );
        this.extractor.addTextExtractor( new MsWordTextExtractor() );

        this.extractor.addTextExtractor( new MsWordDocxTextExtractor() );
        this.extractor.addTextExtractor( new MsExcelXslxTextExtractor() );
        this.extractor.addTextExtractor( new MsPowerPointlPptxTextExtractor() );

        this.extractor.addTextExtractor( new OpenOfficeTextExtractor() );
        this.extractor.addTextExtractor( new PlainTextExtractor() );
        this.extractor.addTextExtractor( new PngTextExtractor() );
        this.extractor.addTextExtractor( new RTFTextExtractor() );
        this.extractor.addTextExtractor( new XMLTextExtractor() );

        final String[] contentTypes = this.extractor.getContentTypes();
        this.allowedMimeTypes = new HashSet<String>( Arrays.asList( contentTypes ) );

    }

    public boolean canHandle( final String mimeType )
    {
        return this.allowedMimeTypes.contains( mimeType );
    }

    public String extractText( final String mimeType, final InputStream inputStream, final String encoding )
        throws IOException
    {
        if ( !canHandle( mimeType ) )
        {
            return null;
        }

        final Reader reader = this.extractor.extractText( inputStream, mimeType, encoding );
        return toString( reader );
    }

    private String toString( final Reader reader )
        throws IOException
    {
        final StringWriter out = new StringWriter();
        final char[] buffer = new char[1024];

        while ( true )
        {
            final int num = reader.read( buffer );
            if ( num <= 0 )
            {
                break;
            }

            out.write( buffer, 0, num );
        }

        reader.close();
        out.close();

        return out.getBuffer().toString();
    }
}
