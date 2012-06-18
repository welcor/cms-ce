/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.BigText;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.search.IndexType;

import static org.junit.Assert.*;

public class ContentIndexServiceImpl_indexRemoveTest
    extends ContentIndexServiceTestBase
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentIndexServiceImpl_indexRemoveTest.class.getName() );

    @Test
    public void remove_by_category()
        throws Exception
    {
        // Setup standard values
        setUpStandardTestValues();

        // Check contents exists
        assertTrue( contentIndexService.isIndexed( new ContentKey( 1322 ), IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( new ContentKey( 1323 ), IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( new ContentKey( 1327 ), IndexType.Content ) );

        // Remove by category
        contentIndexService.removeByCategory( new CategoryKey( 9 ) );
        flushIndex();

        // Check contents deleted
        assertFalse( contentIndexService.isIndexed( new ContentKey( 1322 ), IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( new ContentKey( 1323 ), IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( new ContentKey( 1327 ), IndexType.Content ) );

        // Remove content
        contentIndexService.removeByCategory( new CategoryKey( 7 ) );

        //flushIndex();

        // Check if indexed
        assertFalse( contentIndexService.isIndexed( new ContentKey( 1327 ), IndexType.Content ) );
    }

    @Test
    public void remove_by_category_type()
        throws Exception
    {
        // Setup standard values
        setUpStandardTestValues();

        final ContentKey contentKey1 = new ContentKey( 1322 );
        final ContentKey contentKey2 = new ContentKey( 1323 );
        final ContentKey contentKey3 = new ContentKey( 1324 );

        // Check contents exists

        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey3, IndexType.Content ) );

        // Remove by content type
        contentIndexService.removeByContentType( new ContentTypeKey( 32 ) );

        // Check contents deleted
        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey3, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );

        // Remove content
        contentIndexService.removeByContentType( new ContentTypeKey( 37 ) );

        // Check if indexed
        assertFalse( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
    }

    @Test
    public void remove_parent_and_child()
    {
        final ContentKey contentKey = new ContentKey( 1322 );

        ContentDocument doc1 = createContentWithBinary( contentKey );

        contentIndexService.index( doc1 );

        flushIndex();

        assertTrue( contentIndexService.isIndexed( contentKey, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey, IndexType.Binaries ) );

        contentIndexService.remove( contentKey );

        flushIndex();

        assertFalse( contentIndexService.isIndexed( contentKey, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey, IndexType.Binaries ) );
    }

    @Test
    public void remove_parent_and_child_by_category()
    {
        final ContentKey contentKey1 = new ContentKey( 1322 );
        final ContentKey contentKey2 = new ContentKey( 1322 );

        contentIndexService.index( createContentWithBinary( contentKey1 ) );
        contentIndexService.index( createContentWithBinary( contentKey2 ) );
        flushIndex();

        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Binaries ) );

        contentIndexService.removeByCategory( new CategoryKey( 9 ) );
        flushIndex();

        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );
        assertFalse( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey2, IndexType.Binaries ) );
    }

    @Test
    public void remove_parent_and_child_by_contenttype()
    {
        final ContentKey contentKey1 = new ContentKey( 1322 );
        final ContentKey contentKey2 = new ContentKey( 1322 );

        contentIndexService.index( createContentWithBinary( contentKey1 ) );
        contentIndexService.index( createContentWithBinary( contentKey2 ) );
        flushIndex();

        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey2, IndexType.Binaries ) );

        contentIndexService.removeByContentType( new ContentTypeKey( 32 ) );
        flushIndex();

        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );
        assertFalse( contentIndexService.isIndexed( contentKey2, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey2, IndexType.Binaries ) );
    }

    @Test
    public void remove_child_on_reindex_if_no_longer_applicable()
    {
        final ContentKey contentKey1 = new ContentKey( 1322 );

        final ContentDocument contentWithBinary = createContentWithBinary( contentKey1 );
        contentIndexService.index( contentWithBinary );
        flushIndex();

        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );

        contentWithBinary.setBinaryExtractedText( null );
        contentIndexService.index( contentWithBinary );
        flushIndex();

        assertTrue( contentIndexService.isIndexed( contentKey1, IndexType.Content ) );
        assertFalse( contentIndexService.isIndexed( contentKey1, IndexType.Binaries ) );
    }


    private ContentDocument createContentWithBinary( final ContentKey contentKey )
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );

        doc1.setBinaryExtractedText( new BigText( "This is a binary content" ) );
        return doc1;
    }

}
