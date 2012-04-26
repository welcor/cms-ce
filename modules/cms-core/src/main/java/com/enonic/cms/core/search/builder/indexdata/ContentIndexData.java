package com.enonic.cms.core.search.builder.indexdata;

import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;

public class ContentIndexData
{
    private final ContentKey key;

    private XContentBuilder binaryData;

    private Set<ContentIndexDataElement> contentData = Sets.newHashSet();

    private XContentBuilder metadata;

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

        for ( ContentIndexDataElement contentDataElement : contentData )
        {
            for ( ContentIndexDataFieldValue fieldValue : contentDataElement.getAllFieldValuesForElement() )
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

        this.contentData.add( contentIndexDataElement );
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

    public String getContentDataAsString()
    {
        StringBuilder builder = new StringBuilder();

        for ( ContentIndexDataElement element : this.contentData )
        {
            for ( ContentIndexDataFieldValue fieldValue : element.getAllFieldValuesForElement() )
            {
                builder.append( fieldValue.toString() + "," );
            }
        }

        builder.deleteCharAt( builder.length() - 1 );

        return builder.toString();
    }

    public Set<ContentIndexDataElement> getContentData()
    {
        return contentData;
    }
}
