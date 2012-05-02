/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.search;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;

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
        //letTheIndexFinishItsWork();

        // Check contents exists
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1327 ) ) );

        // Remove by category
        this.contentIndexService.removeByCategory( new CategoryKey( 9 ) );
        flushIndex();

        // Check contents deleted
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1327 ) ) );

        // Remove content
        this.contentIndexService.removeByCategory( new CategoryKey( 7 ) );

        //flushIndex();

        // Check if indexed
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1327 ) ) );
    }

    @Test
    public void remove_by_category_type()
        throws Exception
    {
        // Setup standard values
        setUpStandardTestValues();

        // Check contents exists
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1324 ) ) );

        // Remove by content type
        this.contentIndexService.removeByContentType( new ContentTypeKey( 32 ) );

        // Check contents deleted
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1324 ) ) );

        // Remove content
        this.contentIndexService.removeByContentType( new ContentTypeKey( 37 ) );

        // Check if indexed
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
    }


}
