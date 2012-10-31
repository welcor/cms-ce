/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.legacy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;


public class LegacyArticleContentData
    extends AbstractBaseLegacyContentData
{
    public LegacyArticleContentData( Document contentDataXml )
    {
        super( contentDataXml );
    }

    protected String resolveTitle()
    {
        final Element nameEl = contentDataEl.getChild( "heading" );
        return nameEl.getText();
    }

    protected List<BinaryDataAndBinary> resolveBinaryDataAndBinaryList()
    {
        return null;
    }

    public void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas )
    {
        // nothing to do for this type
    }

    public void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey )
    {
        // nothing to do for this type
    }

    @Override
    public Set<ContentKey> resolveRelatedContentKeys()
    {
        final Set<ContentKey> contentKeys = new HashSet<ContentKey>();

        contentKeys.addAll( resolveContentKeysByXPath( "/contentdata/files/file/@key" ) );
        contentKeys.addAll( resolveContentKeysByXPath( "/contentdata/body/image/@key" ) );
        contentKeys.addAll( resolveContentKeysByXPath( "/contentdata/teaser/image/@key" ) );

        return contentKeys;
    }

    @Override
    public boolean markReferencesToContentAsDeleted( final ContentKey contentKey )
    {
        Iterator iterator;

        iterator = contentDataEl.getDescendants( new ElementFilter( "files" ) );
        while ( iterator.hasNext() )
        {
            final Element e = Element.class.cast( iterator.next() );
            if ( markReferencesToContentAsDeleted( e.getDescendants( new ElementFilter( "file" ) ), contentKey ) )
            {
                return true;
            }
        }

        iterator = contentDataEl.getDescendants( new ElementFilter( "body" ) );
        while ( iterator.hasNext() )
        {
            final Element e = Element.class.cast( iterator.next() );
            if ( markReferencesToContentAsDeleted( e.getDescendants( new ElementFilter( "image" ) ), contentKey ) )
            {
                return true;
            }
        }

        iterator = contentDataEl.getDescendants( new ElementFilter( "teaser" ) );
        while ( iterator.hasNext() )
        {
            final Element e = Element.class.cast( iterator.next() );
            if ( markReferencesToContentAsDeleted( e.getDescendants( new ElementFilter( "image" ) ), contentKey ) )
            {
                return true;
            }
        }

        return false;
    }
}
