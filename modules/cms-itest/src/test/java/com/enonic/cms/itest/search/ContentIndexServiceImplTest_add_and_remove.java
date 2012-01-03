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
import org.springframework.util.StopWatch;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;

import static org.junit.Assert.*;

public class ContentIndexServiceImplTest_add_and_remove
    extends ContentIndexServiceTestBase
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentIndexServiceImplTest_add_and_remove.class.getName() );

    //@Test
    public void add_large_amount_of_data_bulked()
    {
        List<ContentDocument> docs = indexDataCreator.createContentDocuments( 1001, 1000, "Adult" );

        StopWatch timer = new StopWatch();
        timer.start( "doIndexBulk" );
        this.service.indexBulk( docs );
        timer.stop();

        System.out.println( timer.prettyPrint() );
    }

    @Test
    public void add_and_remove_document()
        throws Exception
    {
        // Check if indexed
        assertFalse( service.isIndexed( new ContentKey( 1322 ) ) );

        // Setup standard values
        doIndexTestData();

        letTheIndexFinishItsWork();

        // Check if indexed
        assertTrue( service.isIndexed( new ContentKey( 1322 ) ) );

        // Remove content
        int removeCount = service.remove( new ContentKey( 1322 ) );

        // Check removed properly.
        assertTrue( removeCount > 0 );
        assertFalse( service.isIndexed( new ContentKey( 1322 ) ) );
    }


    @Test
    public void remove_by_category()
        throws Exception
    {
        // Setup standard values
        setUpStandardTestValues();
        letTheIndexFinishItsWork();

        // Check contents exists
        assertTrue( this.service.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1327 ) ) );

        // Remove by category
        this.service.removeByCategory( new CategoryKey( 9 ) );
        letTheIndexFinishItsWork();

        // Check contents deleted
        assertFalse( this.service.isIndexed( new ContentKey( 1322 ) ) );
        assertFalse( this.service.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1327 ) ) );

        // Remove content
        this.service.removeByCategory( new CategoryKey( 7 ) );
        letTheIndexFinishItsWork();

        // Check if indexed
        assertFalse( this.service.isIndexed( new ContentKey( 1327 ) ) );
    }

    @Test
    public void remove_by_category_type()
        throws Exception
    {
        // Setup standard values
        setUpStandardTestValues();
        letTheIndexFinishItsWork();

        // Check contents exists
        assertTrue( this.service.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1324 ) ) );

        // Remove by content type
        this.service.removeByContentType( new ContentTypeKey( 32 ) );
        letTheIndexFinishItsWork();

        // Check contents deleted
        assertFalse( this.service.isIndexed( new ContentKey( 1322 ) ) );
        assertTrue( this.service.isIndexed( new ContentKey( 1323 ) ) );
        assertFalse( this.service.isIndexed( new ContentKey( 1324 ) ) );

        // Remove content
        this.service.removeByContentType( new ContentTypeKey( 37 ) );
        letTheIndexFinishItsWork();

        // Check if indexed
        assertFalse( this.service.isIndexed( new ContentKey( 1323 ) ) );
    }


    private void doIndexTestData()
    {
        List<ContentDocument> docs = indexDataCreator.createSimpleIndexDataList();

        doIndexContentDocuments( docs );
    }

    private ContentDocument createContentDocument( int contentKey, String title, String preface, String fulltext )
    {
        return createContentDocument( contentKey, title, new String[][]{{"data/preface", preface}, {"fulltext", fulltext}} );
    }

    private ContentDocument createContentDocument( int contentKey, String title, String[][] fields )
    {
        ContentDocument doc = new ContentDocument( new ContentKey( contentKey ) );
        doc.setCategoryKey( new CategoryKey( 9 ) );
        doc.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc.setContentTypeName( "Article" );
        if ( title != null )
        {
            doc.setTitle( title );
        }
        if ( fields != null )
        {
            for ( String[] field : fields )
            {
                doc.addUserDefinedField( field[0], field[1] );
            }
        }
        doc.setStatus( 2 );
        doc.setPriority( 0 );
        return doc;
    }

    private void setUpStandardTestValues()
    {
        final GregorianCalendar date = new GregorianCalendar( 2008, Calendar.FEBRUARY, 28 );

        // Index content 1, 2 og 3:
        ContentDocument doc1 = new ContentDocument( new ContentKey( 1322 ) );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setContentTypeName( "Adults" );
        doc1.setTitle( "Homer" );
        doc1.addUserDefinedField( "data/person/age", "38" );
        doc1.addUserDefinedField( "data/person/gender", "male" );
        doc1.addUserDefinedField( "data/person/description",
                                  "crude, overweight, incompetent, clumsy, thoughtless and a borderline alcoholic" );
        // Publish from February 28th to March 28th.
        doc1.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc1.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        service.index( doc1, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc2 = new ContentDocument( new ContentKey( 1327 ) );
        doc2.setCategoryKey( new CategoryKey( 7 ) );
        doc2.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc2.setContentTypeName( "Adults" );
        doc2.setTitle( "Fry" );
        doc2.addUserDefinedField( "data/person/age", "28" );
        doc2.addUserDefinedField( "data/person/gender", "male" );
        doc2.addUserDefinedField( "data/person/description", "an extratemporal character, unable to comprehend the future" );
        // Publish from February 29th to March 29th.
        doc2.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc2.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc2.setStatus( 2 );
        doc2.setPriority( 0 );
        service.index( doc2, true );

        date.add( Calendar.DAY_OF_MONTH, 1 );
        ContentDocument doc3 = new ContentDocument( new ContentKey( 1323 ) );
        doc3.setCategoryKey( new CategoryKey( 9 ) );
        doc3.setContentTypeKey( new ContentTypeKey( 37 ) );
        doc3.setContentTypeName( "Children" );
        doc3.setTitle( "Bart" );
        doc3.addUserDefinedField( "data/person/age", "10" );
        doc3.addUserDefinedField( "data/person/gender", "male" );
        doc3.addUserDefinedField( "data/person/description", "mischievous, rebellious, disrespecting authority and sharp witted" );
        // Publish from March 1st to April 1st
        doc3.setPublishFrom( date.getTime() );
        date.add( Calendar.MONTH, 1 );
        doc3.setPublishTo( date.getTime() );
        date.add( Calendar.MONTH, -1 );
        doc3.setStatus( 2 );
        doc3.setPriority( 0 );
        service.index( doc3, true );

        ContentDocument doc4 = new ContentDocument( new ContentKey( 1324 ) );
        doc4.setCategoryKey( new CategoryKey( 9 ) );
        doc4.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc4.setContentTypeName( "Adults" );
        doc4.setTitle( "Bender" );
        doc4.addUserDefinedField( "data/person/age", "5" );
        doc4.addUserDefinedField( "data/person/gender", "man-bot" );
        doc4.addUserDefinedField( "data/person/description",
                                  "alcoholic, whore-mongering, chain-smoking gambler with a swarthy Latin charm" );
        // Publish from March 1st to March 28th.
        doc4.setPublishFrom( date.getTime() );
        date.add( Calendar.DAY_OF_MONTH, 27 );
        doc4.setPublishTo( date.getTime() );
        doc4.setStatus( 2 );
        doc4.setPriority( 0 );
        service.index( doc4, true );

    }
}
