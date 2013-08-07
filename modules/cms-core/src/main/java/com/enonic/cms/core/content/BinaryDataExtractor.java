/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.plugin.ext.TextExtractorExtensions;
import com.enonic.cms.store.dao.BinaryDataDao;

@Component
public class BinaryDataExtractor
{
    @Autowired
    TextExtractorExtensions extensions;

    @Autowired
    BinaryDataDao binaryDataDao;

    @Autowired
    MimeTypeResolver mimeTypeResolver;

    private static final Logger LOG = LoggerFactory.getLogger( ContentDocumentFactory.class );

    public BigText extractBinaryData( ContentEntity content )
    {
        Set<ContentBinaryDataEntity> binaryDataRef = content.getMainVersion().getContentBinaryData();
        for ( ContentBinaryDataEntity cbd : binaryDataRef )
        {
            BinaryDataEntity binaryData = cbd.getBinaryData();
            try
            {
                return extractText( binaryData );

            }
            catch ( Throwable e )
            {
                StringBuffer sb = new StringBuffer();
                sb.append( "Failed to extract full text from binary data" );
                sb.append( "(key: " ).append( binaryData.getKey() ).append( ", name: " ).append( binaryData.getName() ).append(
                    ") from content" );
                sb.append( "(key: " ).append( content.getKey() ).append( ", type: " ).append( content.getContentType().getName() );
                sb.append( ", category: " ).append( content.getCategory().getName() ).append( "): " ).append( e.getMessage() );
                LOG.warn( sb.toString(), e );
            }
        }

        return null;
    }

    BigText extractText( BinaryDataEntity binaryData )
        throws IOException
    {
        final String mimeType = mimeTypeResolver.getMimeType( binaryData.getName() );
        final TextExtractor textExtractor = this.extensions.getByMimeType( mimeType );

        final String fullTextString;
        if ( textExtractor == null )
        {
            return null;
        }
        else
        {
            BlobRecord blob = binaryDataDao.getBlob( binaryData );
            //InputStream stream = new ByteArrayInputStream( blob.getAsBytes() );
            InputStream stream = blob.getStream();
            fullTextString = textExtractor.extractText( mimeType, stream, "UTF-8" );
        }

        return fullTextString != null ? new BigText( fullTextString ) : null;
    }
}