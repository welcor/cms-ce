/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.plugin.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.enonic.cms.api.plugin.ext.TextExtractor;

public class PdfExtractor
    extends TextExtractor
{
    @Override
    public boolean canHandle( String mimeType )
    {
        if ( "application/pdf".equals( mimeType ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String extractText( String mimeType, InputStream inputStream, String encoding )
        throws IOException
    {
        if ( canHandle( mimeType ) )
        {
            PDDocument doc = PDDocument.load( inputStream );
            PDFTextStripper stripper = new PDFTextStripper();
            String text =  stripper.getText(doc);
            doc.close();
            return text;
        }
        else
        {
            return null;
        }
    }
}
