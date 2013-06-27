/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.api.client.model.content.image.ImageContentDataInput;

/**
 * This class is deprecated
 *
 * @Deprecated
 */
public class BinaryData
    implements Serializable
{

    public static final String LABEL_FILE = "file";

    private static final long serialVersionUID = -2286685939812284906L;

    public int key = -1;

    public int contentKey = -1;

    public byte[] data;

    public Date timestamp;

    public String fileName;

    public String label;

    public boolean anonymousAccess = false;

    public String getSafeFileName()
    {
        if ( fileName == null )
        {
            return null;
        }
        else
        {
            return StringUtil.stripControlChars( fileName );
        }
    }

    public void setSafeFileName( String fileName )
    {
        if ( ( fileName != null ) && fileName.length() > 0 )
        {
            this.fileName = StringUtil.stripControlChars( fileName );
        }
        else
        {
            this.fileName = "noname.bin";
        }
    }

    public static BinaryData createBinaryDataFromStream( final ByteArrayOutputStream stream, final String fileName, final String label,
                                                         final ImageContentDataInput contentData )
    {
        final BinaryData binaryData = new BinaryData();
        binaryData.fileName = contentData == null ? fileName : contentData.binary.getBinaryName();
        binaryData.data = contentData == null ? stream.toByteArray() : contentData.binary.getBinary();
        binaryData.label = label;
        return binaryData;
    }
}
