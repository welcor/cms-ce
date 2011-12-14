package com.enonic.cms.core.search.index;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.cms.core.content.ContentKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:46 PM
 */
public class ContentIndexData
{
    private final ContentKey key;

    private final XContentBuilder metadata;

    private XContentBuilder extractedBinaryData;

    public ContentIndexData( final ContentKey key, final XContentBuilder metadata )
    {
        this.key = key;
        this.metadata = metadata;
    }

    public ContentKey getKey()
    {
        return this.key;
    }

    public XContentBuilder getContentdata()
    {
        return this.metadata;
    }

    public String getMetadataJson()
        throws Exception
    {
        return getContentdata().string();
    }


    public XContentBuilder getExtractedBinaryData()
    {
        return extractedBinaryData;
    }

    public void setExtractedBinaryData( XContentBuilder extractedBinaryData )
    {
        this.extractedBinaryData = extractedBinaryData;
    }


}
