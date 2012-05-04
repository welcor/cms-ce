package com.enonic.cms.core.search.builder;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;

public class ContentIndexData
{
    private final ContentKey key;

    private XContentBuilder binaryData;

    private Set<ContentIndexDataElement> contentIndexDataElements = Sets.newHashSet();

    public String getContentDataAsJsonString()
        throws Exception
    {
        return buildContentDataJson().string();
    }

    public XContentBuilder buildContentDataJson()
        throws Exception
    {
        final XContentBuilder builder = XContentFactory.jsonBuilder();

        builder.startObject();

        for ( ContentIndexDataElement contentIndexDataElement : contentIndexDataElements )
        {
            for ( ContentIndexDataFieldValue fieldValue : contentIndexDataElement.getAllFieldValuesForElement() )
            {
                builder.field( fieldValue.getFieldName(), fieldValue.getValue() );
            }
        }

        builder.endObject();
        return builder;
    }

    public void addContentData( String dataElementName, Object value )
    {
        if ( value == null )
        {
            return;
        }

        ContentIndexDataElement contentIndexDataElement;

        if ( value instanceof Set )
        {
            contentIndexDataElement = new ContentIndexDataElement( dataElementName, (Set) value );
        }
        else
        {
            contentIndexDataElement = new ContentIndexDataElement( dataElementName, Sets.newHashSet( value ) );
        }

        this.contentIndexDataElements.add( contentIndexDataElement );
    }

    public ContentIndexData( final ContentKey key )
    {
        this.key = key;
    }

    public ContentKey getKey()
    {
        return this.key;
    }

    public XContentBuilder getBinaryData()
    {
        return binaryData;
    }

    public void setBinaryData( XContentBuilder binaryData )
    {
        this.binaryData = binaryData;
    }

    public Set<ContentIndexDataElement> getContentIndexDataElements()
    {
        return contentIndexDataElements;
    }
}
