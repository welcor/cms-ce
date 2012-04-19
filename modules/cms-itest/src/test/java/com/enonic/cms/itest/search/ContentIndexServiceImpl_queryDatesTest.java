package com.enonic.cms.itest.search;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static org.junit.Assert.*;


public class ContentIndexServiceImpl_queryDatesTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testDateQueries()
    {
        setUpStandardTestValues();

        printAllIndexContent();

        ContentIndexQuery query1 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')" );
        ContentResultSet res1 = contentIndexService.query( query1 );
        assertEquals( 1, res1.getLength() );

        /* ContentIndexQuery query2 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')" );
            ContentResultSet res2 = contentIndexService.query( query2 );
            assertEquals( 1, res2.getLength() );

            ContentIndexQuery query3 = new ContentIndexQuery( "publishFrom <= date('2008-02-29T00:00:00')" );
            ContentResultSet res3 = contentIndexService.query( query3 );
            assertEquals( 2, res3.getLength() );

            ContentIndexQuery query4 = new ContentIndexQuery( "publishFrom > date('2008-02-28')" );
            ContentResultSet res4 = contentIndexService.query( query4 );
            assertEquals( 3, res4.getLength() );

            ContentIndexQuery query5 =
                new ContentIndexQuery( "publishFrom >= date('2008-02-29T00:00:00') AND publishTo < date('2008-03-29T00:00:00')" );
            ContentResultSet res5 = contentIndexService.query( query5 );
            assertEquals( 1, res5.getLength() );
        */
    }


    @Test
    public void testQueryReturnsEmptyResultWhenNowIsOneMillisecondBeforePublishFrom()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 0, 59, 999 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryReturnsAResultWhenNowIsSameAsPublishFrom()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryReturnsAResultWhenNowIsOneMillisecondAfterPublishFrom()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 1 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryReturnsEmptytWhenNowIsSameAsBothPublishFromAndPublishTo()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryReturnsEmptyWhenNowIsAfterPublishFromAndSameAsPublishTo()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryReturnsAResultWhenNowIsAfterPublishFromAndWithinThePublishToMinute()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "J�rund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001" );
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 59, 999 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryDateEqualsPublishToFromFields()
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( 3001 ) );
        doc1.setCategoryKey( new CategoryKey( 201 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 101 ) );
        doc1.setContentTypeName( "Person" );
        doc1.setTitle( "Jørund Vier Skriubakken" );
        doc1.setPublishFrom( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );
        doc1.setPublishTo( new DateTime( 2010, 4, 19, 13, 2, 0, 0 ).toDate() );
        doc1.setStatus( 2 );
        doc1.setPriority( 0 );
        contentIndexService.index( doc1, true );

        flushIndex();

        ContentIndexQuery query =
            new ContentIndexQuery( "@publishFrom = date('2010-04-19 13:01') AND @publishTo = date('2010-04-19 13:02')" );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        ContentIndexQuery queryNotEquals =
            new ContentIndexQuery( "@publishFrom != date('2010-04-19 13:01') OR @publishTo != date('2010-04-19 13:02')" );

        ContentResultSet contentResultSet2 = contentIndexService.query( queryNotEquals );
        assertEquals( 0, contentResultSet2.getKeys().size() );
    }

    @Test
    public void testQueryDateFieldsInUserData_equals()
    {
        final ContentDocument contentDocument = createContentDocument( 1 );
        addUserDefinedBlock( contentDocument, "1975-05-05" );
        contentIndexService.index( contentDocument, true );
        flushIndex();

        /*
           Enonic Content Query Language supported date-formates:
                yyyy-MM-ddTHH:mm:ss
                yyyy-MM-dd HH:mm:ss
                yyyy-MM-dd HH:mm
                yyyy-MM-dd
        */

        ContentIndexQuery query = new ContentIndexQuery( "data/person/birthdate = date('1975-05-05')" );
        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate = date('1975-05-05 00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate = date('1975-05-05 00:00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate = date('1975-05-04T23:00:00.000Z')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryDateFieldsInUserData_range_multiple_entries_in_one_doc()
    {
        final ContentDocument contentDocument = createContentDocument( 1 );
        addUserDefinedBlock( contentDocument, "1975-05-05" );
        addUserDefinedBlock( contentDocument, "1994-06-06" );
        addUserDefinedBlock( contentDocument, "1975-06-06" );
        contentIndexService.index( contentDocument, true );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/birthdate <= date('1975-05-05')" );
        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate >= date('1976-06-06 00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate < date('1975-05-05 00:00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate > date('1994-06-06T23:00:00.000Z')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testQueryDateFieldsInUserData_range_single_entry_in_multiple_docs()
    {
        final ContentDocument contentDocument1 = createContentDocument( 1 );
        addUserDefinedBlock( contentDocument1, "1975-05-05" );
        final ContentDocument contentDocument2 = createContentDocument( 2 );
        addUserDefinedBlock( contentDocument2, "1994-06-06" );
        final ContentDocument contentDocument3 = createContentDocument( 3 );
        addUserDefinedBlock( contentDocument3, "1975-06-06" );
        contentIndexService.index( contentDocument1, true );
        contentIndexService.index( contentDocument2, true );
        contentIndexService.index( contentDocument3, true );
        flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/birthdate <= date('1975-05-05')" );
        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate >= date('1975-05-05 00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 3, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate < date('1994-06-06 00:00:00')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 2, contentResultSet.getKeys().size() );

        query = new ContentIndexQuery( "data/person/birthdate > date('1975-06-06T23:00:00.000Z')" );
        contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }


    private ContentDocument createContentDocument( int contentKey )
    {
        ContentDocument doc1 = new ContentDocument( new ContentKey( contentKey ) );
        doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
        doc1.setTitle( "Family" );
        doc1.setContentTypeName( "Adults" );
        return doc1;
    }

    private void addUserDefinedBlock( final ContentDocument doc1, String birthdate )
    {
        doc1.addUserDefinedField( "data/person/birthdate", birthdate );
    }


}
