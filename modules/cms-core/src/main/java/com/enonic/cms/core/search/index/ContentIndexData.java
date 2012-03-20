package com.enonic.cms.core.search.index;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.cms.core.content.ContentKey;

public class ContentIndexData
{
    private final ContentKey key;

    private final XContentBuilder contentData;

    private XContentBuilder binaryData;

    public ContentIndexData( final ContentKey key, final XContentBuilder contentData )
    {
        this.key = key;
        this.contentData = contentData;
    }

    public ContentKey getKey()
    {
        return this.key;
    }

    public XContentBuilder getContentdata()
    {
        return this.contentData;
    }

    public String getMetadataJson()
        throws Exception
    {
        return getContentdata().string();
    }

    public XContentBuilder getBinaryData()
    {
        return binaryData;
    }

    public void setBinaryData( XContentBuilder binaryData )
    {
        this.binaryData = binaryData;
    }
}
