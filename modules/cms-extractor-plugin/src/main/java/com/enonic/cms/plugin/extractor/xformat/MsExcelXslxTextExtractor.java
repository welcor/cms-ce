package com.enonic.cms.plugin.extractor.xformat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsExcelXslxTextExtractor
    extends AbstractTextExtractor
{
    private static final Logger logger = LoggerFactory.getLogger( MsExcelXslxTextExtractor.class );

    public MsExcelXslxTextExtractor()
    {
        super( new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"} );
    }

    @Override
    public Reader extractText( final InputStream stream, final String type, final String encoding )
        throws IOException
    {
        try
        {
            final OPCPackage opcPackage = OPCPackage.open( stream );
            final XSSFExcelExtractor xw = new XSSFExcelExtractor( opcPackage );
            return new StringReader( xw.getText() );
        }
        catch ( Exception e )
        {
            logger.warn( "Failed to extract Excel text content", e );
            return new StringReader( "" );
        }
    }
}
