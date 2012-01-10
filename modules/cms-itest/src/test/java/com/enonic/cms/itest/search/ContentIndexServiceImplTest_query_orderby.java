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

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/3/12
 * Time: 2:56 PM
 */
public class ContentIndexServiceImplTest_query_orderby
    extends ContentIndexServiceTestBase
{

    @Test
    public void testOrderByPublishfrom()
    {
        ContentDocument doc1 = createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c1",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc1.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 2 ).toDate() );
        service.index( doc1, false );
        letTheIndexFinishItsWork();

        ContentDocument doc2 = createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc2.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 0 ).toDate() );
        service.index( doc2, false );
        letTheIndexFinishItsWork();

        ContentDocument doc3 = createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c3",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc3.setPublishFrom( new DateTime( 2010, 10, 1, 0, 0, 0, 1 ).toDate() );
        service.index( doc3, false );
        letTheIndexFinishItsWork();

        printAllIndexContent();

        // TODO: Should contenttypekey be contenttypekey or contenttype_key ??
        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishFrom asc", 10 ) )
                          .getKeys() );

        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishFrom desc", 10 ) )
                          .getKeys() );

    }

    @Test
    public void testOrderByPublishto()
    {
        ContentDocument doc1 = createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c1",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc1.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 2 ).toDate() );
        service.index( doc1, false );
        letTheIndexFinishItsWork();
        ContentDocument doc2 = createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc2.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 0 ).toDate() );
        service.index( doc2, false );
        letTheIndexFinishItsWork();
        ContentDocument doc3 = createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c3",
                                                      new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} );
        doc3.setPublishTo( new DateTime( 2010, 10, 1, 0, 0, 0, 1 ).toDate() );
        service.index( doc3, false );
        letTheIndexFinishItsWork();

        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishTo asc", 10 ) )
                          .getKeys() );

        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "publishTo desc", 10 ) )
                          .getKeys() );

    }

    @Test
    public void testOrderByStatus()
    {
        service.index( createContentDocument( new ContentKey( 101 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 3, "c1",
                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
        service.index( createContentDocument( new ContentKey( 102 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 0, "c2",
                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
        service.index( createContentDocument( new ContentKey( 103 ), new CategoryKey( 1 ), new ContentTypeKey( 10 ), 2, "c3",
                                              new String[][]{{"data/dummy", "dummy value"}, {"data/dummy2", "dummy value 2"}} ), false );
        letTheIndexFinishItsWork();

        assertEquals( ContentKey.convertToList( new int[]{102, 103, 101} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "status asc", 10 ) )
                          .getKeys() );

        assertEquals( ContentKey.convertToList( new int[]{101, 103, 102} ),
                      service.query( new ContentIndexQuery( "contenttypekey = 10 and title STARTS WITH 'c'", "status desc", 10 ) )
                          .getKeys() );

    }

    @Test
    public void testQueriesWithOrderBy()
    {
        setUpStandardTestValues();

        ContentIndexQuery query1 = new ContentIndexQuery( "ORDER BY key DESC", 10 );
        ContentResultSet res1 = service.query( query1 );
        assertEquals( 4, res1.getLength() );
        assertEquals( 1327, res1.getKey( 0 ).toInt() );
        assertEquals( 1324, res1.getKey( 1 ).toInt() );
        assertEquals( 1323, res1.getKey( 2 ).toInt() );

        ContentIndexQuery query2 = new ContentIndexQuery( "categorykey = 9 ORDER BY title ASC", 10 );
        ContentResultSet res2 = service.query( query2 );
        assertEquals( 3, res2.getLength() );
        assertEquals( 1323, res2.getKey( 0 ).toInt() );
        assertEquals( 1324, res2.getKey( 1 ).toInt() );

        ContentIndexQuery query3 = new ContentIndexQuery( "ORDER BY title ASC", 10 );
        ContentResultSet res3 = service.query( query3 );
        assertEquals( 4, res3.getLength() );
        assertEquals( 1323, res3.getKey( 0 ).toInt() );
        assertEquals( 1324, res3.getKey( 1 ).toInt() );
        assertEquals( 1327, res3.getKey( 2 ).toInt() );

        ContentIndexQuery query4 = new ContentIndexQuery( "ORDER BY publishto ASC", 10 );
        ContentResultSet res4 = service.query( query4 );
        assertEquals( 4, res4.getLength() );
        assertEquals( 1327, res4.getKey( 2 ).toInt() );
        assertEquals( 1323, res4.getKey( 3 ).toInt() );

        ContentIndexQuery query5 = new ContentIndexQuery( "ORDER BY publishto DESC", 10 );
        ContentResultSet res5 = service.query( query5 );
        assertEquals( 4, res5.getLength() );
        assertEquals( 1323, res5.getKey( 0 ).toInt() );
        assertEquals( 1327, res5.getKey( 1 ).toInt() );

        ContentIndexQuery query6 = new ContentIndexQuery( "ORDER BY categorykey DESC, title ASC", 10 );
        ContentResultSet res6 = service.query( query6 );
        assertEquals( 4, res6.getLength() );
        assertEquals( 1323, res6.getKey( 0 ).toInt() );
        assertEquals( 1324, res6.getKey( 1 ).toInt() );
        assertEquals( 1322, res6.getKey( 2 ).toInt() );

        ContentIndexQuery query7 = new ContentIndexQuery( "ORDER BY categorykey ASC, publishfrom DESC, publishto DESC", 10 );
        ContentResultSet res7 = service.query( query7 );
        assertEquals( 4, res7.getLength() );
        assertEquals( 1327, res7.getKey( 0 ).toInt() );
        assertEquals( 1324, res7.getKey( 2 ).toInt() );
        assertEquals( 1322, res7.getKey( 3 ).toInt() );
    }



    @Test
    public void testQueryWithOrderByMultipleRelatedContentDoesNotCreateDuplicateContentKeys()
    {
        service.index( createContentDocument( 101, "title", new String[][]{{"data/myrelated", "3"}, {"data/myrelated", "9"}} ), false );
        letTheIndexFinishItsWork();
        printAllIndexContent();

        assertContentResultSetEquals( new int[]{101}, service.query( new ContentIndexQuery( "", "data/myrelated ASC", 10 ) ) );
    }

}
