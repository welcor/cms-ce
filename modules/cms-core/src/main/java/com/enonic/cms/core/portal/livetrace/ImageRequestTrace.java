/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;

/**
 * Oct 11, 2010
 */
public class ImageRequestTrace
    extends BaseTrace
    implements Trace
{
    private MaxLengthedString contentKey = new MaxLengthedString();

    private MaxLengthedString label = new MaxLengthedString();

    private MaxLengthedString imageParamQuality = new MaxLengthedString();

    private MaxLengthedString imageParamFormat = new MaxLengthedString();

    private MaxLengthedString imageParamFilter = new MaxLengthedString();

    private MaxLengthedString imageParamBackgroundColor = new MaxLengthedString();

    private Long sizeInBytes;

    private CacheUsage cacheUsage = new CacheUsage().setCacheable( true );

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
    public String getLabel()
    {
        return label != null ? label.toString() : null;
    }

    void setLabel( String label )
    {
        this.label = new MaxLengthedString( label );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getImageParamFormat()
    {
        return imageParamFormat != null ? imageParamFormat.toString() : null;
    }

    void setImageParamFormat( String imageParamFormat )
    {
        this.imageParamFormat = new MaxLengthedString( imageParamFormat );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getImageParamQuality()
    {
        return imageParamQuality != null ? imageParamQuality.toString() : null;
    }

    void setImageParamQuality( String imageParamQuality )
    {
        this.imageParamQuality = new MaxLengthedString( imageParamQuality );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getImageParamFilter()
    {
        return imageParamFilter != null ? imageParamFilter.toString() : null;
    }

    void setImageParamFilter( String imageParamFilter )
    {
        this.imageParamFilter = new MaxLengthedString( imageParamFilter );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getImageParamBackgroundColor()
    {
        return imageParamBackgroundColor != null ? imageParamBackgroundColor.toString() : null;
    }

    void setImageParamBackgroundColor( String imageParamBackgroundColor )
    {
        this.imageParamBackgroundColor = new MaxLengthedString( imageParamBackgroundColor );
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

    public CacheUsage getCacheUsage()
    {
        return cacheUsage;
    }

}
