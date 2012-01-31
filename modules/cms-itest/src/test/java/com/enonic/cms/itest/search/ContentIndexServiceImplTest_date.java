package com.enonic.cms.itest.search;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/10/12
 * Time: 9:44 AM
 */
public class ContentIndexServiceImplTest_date
    extends ContentIndexServiceTestBase
{

    @Ignore
    @Test
    public void testDateQueries()
    {
        setUpStandardTestValues();

        ContentIndexQuery query1 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')");
        ContentResultSet res1 = contentIndexService.query( query1 );
        assertEquals( 1, res1.getLength() );

        ContentIndexQuery query2 = new ContentIndexQuery( "publishFrom = date('2008-02-28T00:00:00')");
        ContentResultSet res2 = contentIndexService.query( query2 );
        assertEquals( 1, res2.getLength() );

        ContentIndexQuery query3 = new ContentIndexQuery( "publishFrom <= date('2008-02-29T00:00:00')");
        ContentResultSet res3 = contentIndexService.query( query3 );
        assertEquals( 2, res3.getLength() );

        ContentIndexQuery query4 = new ContentIndexQuery( "publishFrom > date('2008-02-28')");
        ContentResultSet res4 = contentIndexService.query( query4 );
        assertEquals( 3, res4.getLength() );

        ContentIndexQuery query5 =
            new ContentIndexQuery( "publishFrom >= date('2008-02-29T00:00:00') AND publishTo < date('2008-03-29T00:00:00')");
        ContentResultSet res5 = contentIndexService.query( query5 );
        assertEquals( 1, res5.getLength() );
    }


    @Ignore
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

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
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

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
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

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 1 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Ignore
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

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 0, 0 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Ignore
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

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
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

        //flushIndex();

        ContentIndexQuery query = new ContentIndexQuery( "@key = 3001");
        query.setContentOnlineAtFilter( new DateTime( 2010, 4, 19, 13, 1, 59, 999 ).toDate() );

        ContentResultSet contentResultSet = contentIndexService.query( query );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

}
