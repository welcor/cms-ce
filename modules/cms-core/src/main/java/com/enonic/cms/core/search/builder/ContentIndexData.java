package com.enonic.cms.core.search.builder;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.ContentIndexException;

public class ContentIndexData
{
    private final ContentKey key;

    private Set<ContentIndexDataElement> contentDataElements = Sets.newHashSet();

    private Set<ContentIndexDataElement> binaryDataElements = Sets.newHashSet();

    public String getContentDataAsJsonString()
        throws Exception
    {
        return buildContentDataJson().string();
    }

    public String getBinaryDataAsJsonString()
        throws Exception
    {
        return buildBinaryDataJson().string();
    }

    public XContentBuilder buildContentDataJson()
    {
        return buildJsonForDataElements( contentDataElements );
    }

    public XContentBuilder buildBinaryDataJson()
    {
        return buildJsonForDataElements( binaryDataElements );
    }

    private XContentBuilder buildJsonForDataElements( Collection<ContentIndexDataElement> contentIndexDataElements )
    {
        try
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
        catch ( IOException e )
        {
            throw new ContentIndexException( "Failed to build json: ", e );
        }
    }

    public void addBinaryData( String dataElementName, Object value )
    {
        ContentIndexDataElement contentIndexDataElement = doCreateContentIndexDataElement( dataElementName, value );

        if ( contentIndexDataElement == null )
        {
            return;
        }

        this.binaryDataElements.add( contentIndexDataElement );
    }

    public void addContentData( String dataElementName, Object value )
    {
        ContentIndexDataElement contentIndexDataElement = doCreateContentIndexDataElement( dataElementName, value );

        if ( contentIndexDataElement == null )
        {
            return;
        }

        this.contentDataElements.add( contentIndexDataElement );
    }

    private ContentIndexDataElement doCreateContentIndexDataElement( final String dataElementName, final Object value )
    {
        if ( value == null )
        {
            return null;
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
        return contentIndexDataElement;
    }

    public ContentIndexData( final ContentKey key )
    {
        this.key = key;
    }

    public ContentKey getKey()
    {
        return this.key;
    }

    public Set<ContentIndexDataElement> getContentDataElements()
    {
        return contentDataElements;
    }

    public Set<ContentIndexDataElement> getBinaryDataElements()
    {
        return binaryDataElements;
    }

    public boolean hasBinaryData()
    {
        return !binaryDataElements.isEmpty();
    }
}
