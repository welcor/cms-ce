package com.enonic.cms.plugin.extractor.xformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsWordDocxTextExtractor
    extends AbstractTextExtractor
{
    private static final Logger logger = LoggerFactory.getLogger( MsWordDocxTextExtractor.class );

    public MsWordDocxTextExtractor()
    {
        super( new String[]{"application/vnd.openxmlformats-officedocument.wordprocessingml.document"} );
    }

    @Override
    public Reader extractText( final InputStream stream, final String type, final String encoding )
        throws IOException
    {
        try
        {
            final OPCPackage opcPackage = OPCPackage.open( stream );
            final XWPFWordExtractor xw = new XWPFWordExtractor( opcPackage );
            return new StringReader( xw.getText() );
        }
        catch ( Exception e )
        {
            logger.warn( "Failed to extract Word text content", e );
            return new StringReader( "" );
        }
    }
}
