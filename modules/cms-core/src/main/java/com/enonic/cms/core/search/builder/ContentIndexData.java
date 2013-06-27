/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.IndexException;

public class ContentIndexData
{
    private final ContentKey key;

    private final Set<ContentIndexDataElement> contentIndexDataElements = Sets.newHashSet();

    private final Set<ContentIndexDataElement> binaryDataElements = Sets.newHashSet();

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
        return buildJsonForDataElements( contentIndexDataElements );
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
                final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues =
                    ContentIndexDataFieldValueSetFactory.create( contentIndexDataElement );

                for ( ContentIndexDataFieldAndValue fieldAndValue : contentIndexDataFieldAndValues )
                {
                    builder.field( fieldAndValue.getFieldName(), fieldAndValue.getValue() );
                }
            }

            builder.endObject();
            return builder;
        }
        catch ( IOException e )
        {
            throw new IndexException( "Failed to build json: ", e );
        }
    }

    public void addBinaryData( String dataElementName, Object value )
    {
        ContentIndexDataElement contentIndexDataElement = doCreateContentIndexDataElement( dataElementName, value, false );

        if ( contentIndexDataElement == null )
        {
            return;
        }

        this.binaryDataElements.add( contentIndexDataElement );
    }

    public void addContentIndexDataElement( String dataElementName, Object value )
    {
        doAddContentIndexDataElement( dataElementName, value, true );
    }

    public void addContentIndexDataElement( String dataElementName, Object value, boolean includeOrderby )
    {
        doAddContentIndexDataElement( dataElementName, value, includeOrderby );
    }

    private void doAddContentIndexDataElement( final String dataElementName, final Object value, boolean includeOrderby )
    {
        ContentIndexDataElement contentIndexDataElement = doCreateContentIndexDataElement( dataElementName, value, includeOrderby );

        if ( contentIndexDataElement == null )
        {
            return;
        }

        this.contentIndexDataElements.add( contentIndexDataElement );
    }

    private ContentIndexDataElement doCreateContentIndexDataElement( final String dataElementName, final Object value,
                                                                     boolean includeOrderby )
    {
        if ( value == null )
        {
            return null;
        }

        ContentIndexDataElement contentIndexDataElement;

        if ( value instanceof Set )
        {
            contentIndexDataElement = ContentIndexDataElementFactory.create( dataElementName, (Set) value, includeOrderby );
        }
        else
        {
            contentIndexDataElement = ContentIndexDataElementFactory.create( dataElementName, Sets.newHashSet( value ), includeOrderby );
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

    public Set<ContentIndexDataElement> getContentIndexDataElements()
    {
        return contentIndexDataElements;
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
