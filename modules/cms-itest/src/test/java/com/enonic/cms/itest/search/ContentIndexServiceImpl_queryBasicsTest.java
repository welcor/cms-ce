package com.enonic.cms.itest.search;

import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/3/12
 * Time: 12:06 PM
 */
public class ContentIndexServiceImpl_queryBasicsTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void simple_querying()
    {
        // Setup standard values
        setUpStandardTestValues();
        flushIndex();

        ContentIndexQuery query = createQuery( "key = 1321" );
        ContentResultSet res1 = contentIndexService.query( query );
        assertEquals( 0, res1.getLength() );

        query = createQuery( "key = 1322" );
        ContentResultSet res2 = contentIndexService.query( query );
        assertEquals( 1, res2.getLength() );

        query = createQuery( "key = '1322'" );
        ContentResultSet res3 = contentIndexService.query( query );
        //assertEquals( 1, res3.getLength() );

        query = createQuery( "title = 'Bart'" );
        ContentResultSet res3b = contentIndexService.query( query );
        assertEquals( 1, res3b.getLength() );
        assertEquals( 1323, res3b.getKey( 0 ).toInt() );

        query = createQuery( "key != 1322" );
        ContentResultSet res4 = contentIndexService.query( query );
        assertEquals( 3, res4.getLength() );

        query = createQuery( "key != '1324'" );
        ContentResultSet res5 = contentIndexService.query( query );
        //assertEquals( 3, res5.getLength() );

        query = createQuery( "key > 1323 order by key asc" );
        ContentResultSet res6 = contentIndexService.query( query );
        assertEquals( 2, res6.getLength() );
        assertTrue( 1324 == res6.getKey( 0 ).toInt() );

        query = createQuery( "key < 1327" );
        ContentResultSet res7 = contentIndexService.query( query );
        assertEquals( 3, res7.getLength() );

        query = createQuery( "key >= 1323" );
        ContentResultSet res8 = contentIndexService.query( query );
        assertEquals( 3, res8.getLength() );

        query = createQuery( "key <= 1324" );
        ContentResultSet res9 = contentIndexService.query( query );
        assertEquals( 3, res9.getLength() );
    }

    private ContentIndexQuery createQuery( String queryString )
    {
        ContentIndexQuery query = new ContentIndexQuery( queryString );
        query.setCount( 10 );
        return query;
    }


}
