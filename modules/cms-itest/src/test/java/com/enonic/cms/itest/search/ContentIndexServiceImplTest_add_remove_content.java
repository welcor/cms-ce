/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.search;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;

import static org.junit.Assert.*;

public class ContentIndexServiceImplTest_add_remove_content
    extends ContentIndexServiceTestBase
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentIndexServiceImplTest_add_remove_content.class.getName() );

    @Test
    public void testAddNewValue()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        setUpStandardTestValues();

        final ContentKey contentKey = new ContentKey( 1322 );

        // Index the same content again
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/surname", "Simpson" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, false );
        flushIndex();

        printAllIndexContent();

        verifyStandardFields( doc1, contentKey );

        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testRemoveAndAdd()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );

        // Index the same content again
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        // but this time with two indexes removed, and two added
        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/firstname", "elvis" );
        doc1.addUserDefinedField( "data/person/surname", "presley" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, false );

        flushIndex();

        printAllIndexContent();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }

    @Test
    public void testRemoveTwoAndAddOne()
        throws Exception
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );
        setUpStandardTestValues();

        ContentKey contentKey = new ContentKey( 1322 );

        // Index the same content again
        ContentDocument doc1 = new ContentDocument( contentKey );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );

        // but this time with two indexes removed, but only one added
        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/surname", "presley" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );

        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, false );

        flushIndex();

        verifyStandardFields( doc1, contentKey );
        verifyUserDefinedFields( contentKey, doc1 );
    }


    @Test
    public void add_and_remove_document()
        throws Exception
    {
        // Check if indexed
        assertFalse( contentIndexService.isIndexed( new ContentKey( 1322 ) ) );

        // Setup standard values
        doIndexTestData();

        //letTheIndexFinishItsWork();

        // Check if indexed
        assertTrue( contentIndexService.isIndexed( new ContentKey( 1322 ) ) );

        // Remove content
        int removeCount = contentIndexService.remove( new ContentKey( 1322 ) );

        // Check removed properly.
        assertTrue( removeCount > 0 );
        assertFalse( contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
    }


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
        //letTheIndexFinishItsWork();

        // Check contents exists
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1324 ) ) );

        // Remove by content type
        this.contentIndexService.removeByContentType( new ContentTypeKey( 32 ) );

        //flushIndex();

        // Check contents deleted
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1324 ) ) );

        // Remove content
        this.contentIndexService.removeByContentType( new ContentTypeKey( 37 ) );

        //flushIndex();

        // Check if indexed
        assertFalse( this.contentIndexService.isIndexed( new ContentKey( 1323 ) ) );
    }


    private void doIndexTestData()
    {
        List<ContentDocument> docs = indexDataCreator.createSimpleIndexDataList();

        doIndexContentDocuments( docs );
    }

}
