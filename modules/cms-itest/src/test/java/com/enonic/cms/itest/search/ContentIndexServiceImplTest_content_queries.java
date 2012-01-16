package com.enonic.cms.itest.search;

import java.util.ArrayList;
import java.util.List;

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
 * Date: 1/13/12
 * Time: 12:20 PM
 */
public class ContentIndexServiceImplTest_content_queries
    extends ContentIndexServiceTestBase
{


    @Test
    public void testContentQueryWithCategoryFilter()
    {

        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
                                                      new String[][]{{"data/heading", "title1"}} );
        service.index( doc1, false );

        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
                                                      new String[][]{{"data/heading", "title2"}} );
        service.index( doc2, false );

        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 102 ), new ContentTypeKey( 10 ), "title3",
                                                      new String[][]{{"data/heading", "title3"}} );
        service.index( doc3, false );

        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );

        query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 102 ) );
        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
    }

    @Test
    public void testContentQueryWithContentTypeFilter()
    {

        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
                                                      new String[][]{{"data/heading", "title1"}} );
        service.index( doc1, false );

        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
                                                      new String[][]{{"data/heading", "title2"}} );
        service.index( doc2, false );

        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title3",
                                                      new String[][]{{"data/heading", "title3"}} );
        service.index( doc3, false );

        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "", 10 );
        query.setContentTypeFilter( createContentTypeList( 10 ) );
        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );

        query = new ContentIndexQuery( "data/heading CONTAINS 'title'", "", 10 );
        query.setContentTypeFilter( createContentTypeList( 11 ) );
        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
    }

    @Test
    public void testContentQueryWithCategoryFilterAndComplexLogicalExpression()
    {

        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
                                                      new String[][]{{"data/a", "1"}, {"data/b", "2"}, {"data/c", "3"}} );
        service.index( doc1, false );

        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title2",
                                                      new String[][]{{"data/a", "2"}, {"data/b", "2"}, {"data/c", "1"}} );
        service.index( doc2, false );

        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title3",
                                                      new String[][]{{"data/a", "2"}, {"data/b", "1"}, {"data/c", "3"}} );
        service.index( doc3, false );

        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "(data/a = 1 AND data/b = 2)", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{1}, service.query( query ) );

        query = new ContentIndexQuery( "(data/a = 1 AND data/b = 2) OR data/c = 1", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );

        query = new ContentIndexQuery( "(data/a = 1 OR data/b = 2) AND data/c = 1", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{2}, service.query( query ) );

        query = new ContentIndexQuery( "data/a = 3 OR data/b = 1 OR data/c = 1", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{2, 3}, service.query( query ) );

        query = new ContentIndexQuery( "(data/a = 2 OR data/b = 2) AND (data/a = 1 OR data/c = 3)", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{1, 3}, service.query( query ) );

        query = new ContentIndexQuery( "(data/a = 2 AND data/b = 2) OR (data/a = 1 AND data/c = 3)", "", 10 );
        query.setCategoryFilter( createCategoryKeyList( 101 ) );
        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );
    }

    @Test
    public void testContentQueryWithCategoryFilterAndContentTypeNameSearch()
    {

        ContentDocument doc1 = createContentDocument( new ContentKey( 1 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title1",
                                                      new String[][]{{"data/heading", "title1"}} );
        doc1.setContentTypeName( "Article3" );
        service.index( doc1, false );

        ContentDocument doc2 = createContentDocument( new ContentKey( 2 ), new CategoryKey( 101 ), new ContentTypeKey( 10 ), "title2",
                                                      new String[][]{{"data/heading", "title2"}} );
        doc2.setContentTypeName( "Article3" );
        service.index( doc2, false );

        ContentDocument doc3 = createContentDocument( new ContentKey( 3 ), new CategoryKey( 101 ), new ContentTypeKey( 11 ), "title3",
                                                      new String[][]{{"data/heading", "title3"}} );
        doc3.setContentTypeName( "Loooooooooooooong-content-type-name-it-is" );
        service.index( doc3, false );

        letTheIndexFinishItsWork();

        ContentIndexQuery query = new ContentIndexQuery( "contenttype = 'Article3'", "", 10 );
        query.setContentTypeFilter( createContentTypeList( 10 ) );
        assertContentResultSetEquals( new int[]{1, 2}, service.query( query ) );

        query = new ContentIndexQuery( "contenttype = 'Loooooooooooooong-content-type-name-it-is'", "", 10 );
        query.setContentTypeFilter( createContentTypeList( 11 ) );
        assertContentResultSetEquals( new int[]{3}, service.query( query ) );
    }

    @Test
    public void testQueriesOnUserDefinedData()
    {
        setUpStandardTestValues();

        ContentIndexQuery query1 = new ContentIndexQuery( "data/person/age > 9", 10 );
        ContentResultSet res1 = service.query( query1 );
        assertEquals( 3, res1.getLength() );

        ContentIndexQuery query2 = new ContentIndexQuery( "data/person/gender = 'male'", 10 );
        ContentResultSet res2 = service.query( query2 );
        assertEquals( 3, res2.getLength() );

        ContentIndexQuery query3 = new ContentIndexQuery( "data/person/description LIKE '%alcoholic%'", 10 );
        ContentResultSet res3 = service.query( query3 );
        assertEquals( 2, res3.getLength() );

        ContentIndexQuery query4 =
            new ContentIndexQuery( "data/person/gender = 'male' AND data/person/description LIKE '%alcoholic%'", 10 );
        ContentResultSet res4 = service.query( query4 );
        assertEquals( 1, res4.getLength() );
        assertEquals( 1322, res4.getKey( 0 ).toInt() );

        ContentIndexQuery query5 = new ContentIndexQuery( "data/person/description LIKE '%alcoholic%' ORDER BY data/person/age DESC", 10 );
        ContentResultSet res5 = service.query( query5 );
        assertEquals( 2, res5.getLength() );
        assertEquals( 1322, res5.getKey( 0 ).toInt() );
    }

    @Test
    public void testQueriesOnUserDefinedDataWithDot()
    {
        setUpStandardTestValues();

        ContentIndexQuery query1 = new ContentIndexQuery( "data.person.age > 9", 10 );
        ContentResultSet res1 = service.query( query1 );
        assertEquals( 3, res1.getLength() );

        ContentIndexQuery query2 = new ContentIndexQuery( "data.person.gender = 'male'", 10 );
        ContentResultSet res2 = service.query( query2 );
        assertEquals( 3, res2.getLength() );
    }

    private ContentDocument createContentDocument( ContentKey contentKey, CategoryKey categoryKey, ContentTypeKey contentTypeKey,
                                                   String title, String[][] fields )
    {
        return createContentDocument( contentKey, categoryKey, contentTypeKey, 2, title, fields );
    }

    private List<CategoryKey> createCategoryKeyList( Integer... array )
    {
        List<CategoryKey> keys = new ArrayList<CategoryKey>();
        for ( int x : array )
        {
            keys.add( new CategoryKey( x ) );
        }
        return keys;
    }

    private List<ContentTypeKey> createContentTypeList( Integer... array )
    {
        List<ContentTypeKey> keys = new ArrayList<ContentTypeKey>();
        for ( int x : array )
        {
            keys.add( new ContentTypeKey( x ) );
        }
        return keys;
    }


}
