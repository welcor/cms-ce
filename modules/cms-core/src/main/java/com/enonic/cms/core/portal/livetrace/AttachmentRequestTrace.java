/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

/**
 * Oct 11, 2010
 */
public class AttachmentRequestTrace
    extends BaseTrace
    implements Trace
{
    private MaxLengthedString contentKey = new MaxLengthedString();

    private MaxLengthedString binaryDataKey = new MaxLengthedString();

    private Long sizeInBytes;

    AttachmentRequestTrace()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentKey()
    {
        return contentKey.toString();
    }

    void setContentKey( String contentKey )
    {
        this.contentKey = new MaxLengthedString( contentKey );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getBinaryDataKey()
    {
        return binaryDataKey.toString();
    }

    void setBinaryDataKey( String binaryDataKey )
    {
        this.binaryDataKey = new MaxLengthedString( binaryDataKey );
    }

    @SuppressWarnings("UnusedDeclaration")
    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    void setSizeInBytes( Long sizeInBytes )
    {
        this.sizeInBytes = sizeInBytes;
    }
}
