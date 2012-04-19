package com.enonic.cms.itest.search;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.IndexValueQuery;
import com.enonic.cms.core.content.index.IndexValueResultSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/9/12
 * Time: 12:27 PM
 */
public class ContentIndexServiceImpl_indexValueQueryTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void testQueryIndexValues()
    {
        setUpStandardTestValues();

        IndexValueQuery query = new IndexValueQuery( "title" );

        final IndexValueResultSet result = contentIndexService.query( query );

        assertEquals( 4, result.getCount() );

        List<String> foundValues = getValueList( result );

        assertTrue( foundValues.contains( "homer" ) );
        assertTrue( foundValues.contains( "fry" ) );
        assertTrue( foundValues.contains( "bart" ) );
        assertTrue( foundValues.contains( "bender" ) );
    }


    @Test
    public void testQueryIndexValues_customData()
    {
        setUpStandardTestValues();

        IndexValueQuery query = new IndexValueQuery( "data/person/age" );

        final IndexValueResultSet result = contentIndexService.query( query );

        assertEquals( 4, result.getCount() );

        List<String> foundValues = getValueList( result );

        assertTrue( foundValues.contains( "39" ) );
        assertTrue( foundValues.contains( "28" ) );
        assertTrue( foundValues.contains( "10" ) );
        assertTrue( foundValues.contains( "5" ) );
    }


    @Test
    public void testQueryIndexValues_categoryFilter()
    {
        setUpStandardTestValues();

        IndexValueQuery query = new IndexValueQuery( "data/person/age" );
        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 7 ) ) );

        IndexValueResultSet result = contentIndexService.query( query );

        assertEquals( 1, result.getCount() );
        List<String> foundValues = getValueList( result );
        assertTrue( foundValues.contains( "28" ) );

        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 9 ) ) );

        result = contentIndexService.query( query );

        assertEquals( 3, result.getCount() );
        foundValues = getValueList( result );
        assertTrue( foundValues.contains( "39" ) );
        assertTrue( foundValues.contains( "10" ) );
        assertTrue( foundValues.contains( "5" ) );
    }


    @Test
    public void testQueryIndexValues_contentTypeFilter()
    {
        setUpStandardTestValues();

        IndexValueQuery query = new IndexValueQuery( "data/person/age" );
        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 37 ) ) );

        IndexValueResultSet result = contentIndexService.query( query );

        assertEquals( 1, result.getCount() );
        List<String> foundValues = getValueList( result );
        assertTrue( foundValues.contains( "10" ) );

        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 32 ) ) );

        result = contentIndexService.query( query );

        assertEquals( 3, result.getCount() );
        foundValues = getValueList( result );
        assertTrue( foundValues.contains( "39" ) );
        assertTrue( foundValues.contains( "28" ) );
        assertTrue( foundValues.contains( "5" ) );
    }


    @Test
    public void testQueryIndexValues_category_and_contentTypeFilter()
    {
        /*

          doc1.setCategoryKey( new CategoryKey( 9 ) );
        doc1.setContentTypeKey( new ContentTypeKey( 32 ) );
          doc1.addUserDefinedField( "data/person/age", "38" );

          doc2.setCategoryKey( new CategoryKey( 7 ) );
        doc2.setContentTypeKey( new ContentTypeKey( 32 ) );
          doc2.addUserDefinedField( "data/person/age", "28" );

         doc3.setCategoryKey( new CategoryKey( 9 ) );
        doc3.setContentTypeKey( new ContentTypeKey( 37 ) );
         doc3.addUserDefinedField( "data/person/age", "10" );

          doc4.setCategoryKey( new CategoryKey( 9 ) );
        doc4.setContentTypeKey( new ContentTypeKey( 32 ) );
         doc4.addUserDefinedField( "data/person/age", "5" );

         */

        setUpStandardTestValues();

        IndexValueQuery query = new IndexValueQuery( "data/person/age" );
        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 32 ) ) );
        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 9 ) ) );

        IndexValueResultSet result = contentIndexService.query( query );

        assertEquals( 2, result.getCount() );
        List<String> foundValues = getValueList( result );
        assertTrue( foundValues.contains( "5" ) );
        assertTrue( foundValues.contains( "39" ) );

        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 37 ) ) );

        result = contentIndexService.query( query );

        assertEquals( 1, result.getCount() );
        foundValues = getValueList( result );
        assertTrue( foundValues.contains( "10" ) );
    }

    @Ignore
    @Test
    public void testQueryIndexValues_orderby()
    {
        fail();
    }


    private List<String> getValueList( IndexValueResultSet result )
    {
        List<String> foundValues = Lists.newArrayList();

        for ( int i = 0; i < result.getCount(); i++ )
        {
            foundValues.add( result.getIndexValue( i ).getValue() );
        }
        return foundValues;
    }


}
