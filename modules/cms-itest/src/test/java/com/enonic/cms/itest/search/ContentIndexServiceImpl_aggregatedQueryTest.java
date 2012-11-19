package com.enonic.cms.itest.search;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.query.AggregatedQuery;
import com.enonic.cms.core.search.query.AggregatedResult;
import com.enonic.cms.core.search.query.ContentDocument;

import static junit.framework.Assert.assertEquals;

public class ContentIndexServiceImpl_aggregatedQueryTest
    extends ContentIndexServiceTestBase
{

    @Test
    public void plain_query()
    {
        indexDocuments();

        AggregatedQuery query = new AggregatedQuery( "data/person/age" );
        AggregatedResult result = contentIndexService.query( query );

        assertEquals( 4, result.getCount() );
        assertEquals( 10.0, result.getMinValue() );
        assertEquals( 40.0, result.getMaxValue() );
        assertEquals( 100.0, result.getSumValue() );
        assertEquals( 25.0, result.getAverageValue() );
    }

    @Test
    public void query_with_categoryFilter()
    {
        indexDocuments();

        AggregatedQuery query = new AggregatedQuery( "data/person/age" );
        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 1 ) ) );

        AggregatedResult result = contentIndexService.query( query );

        assertEquals( 2, result.getCount() );
        assertEquals( 10.0, result.getMinValue() );
        assertEquals( 20.0, result.getMaxValue() );
        assertEquals( 30.0, result.getSumValue() );
        assertEquals( 15.0, result.getAverageValue() );
    }

    @Test
    public void query_with_contentTypeFilter()
    {
        indexDocuments();

        AggregatedQuery query = new AggregatedQuery( "data/person/age" );
        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 1 ) ) );

        AggregatedResult result = contentIndexService.query( query );

        assertEquals( 2, result.getCount() );
        assertEquals( 10.0, result.getMinValue() );
        assertEquals( 30.0, result.getMaxValue() );
        assertEquals( 40.0, result.getSumValue() );
        assertEquals( 20.0, result.getAverageValue() );
    }

    @Test
    public void query_with_all_filters()
    {
        indexDocuments();

        AggregatedQuery query = new AggregatedQuery( "data/person/age" );
        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 1 ) ) );
        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 1 ) ) );

        AggregatedResult result = contentIndexService.query( query );

        assertEquals( 1, result.getCount() );
        assertEquals( 10.0, result.getMinValue() );
        assertEquals( 10.0, result.getMaxValue() );
        assertEquals( 10.0, result.getSumValue() );
        assertEquals( 10.0, result.getAverageValue() );
    }


    private void indexDocuments()
    {
        ContentDocument doc1 =
            createContentDocument( new ContentKey( 1 ), new CategoryKey( 1 ), new ContentTypeKey( 1 ), 2, "title1", null );
        doc1.addUserDefinedField( "data/person/age", "10" );
        ContentDocument doc2 =
            createContentDocument( new ContentKey( 2 ), new CategoryKey( 1 ), new ContentTypeKey( 2 ), 2, "title2", null );
        doc2.addUserDefinedField( "data/person/age", "20" );
        ContentDocument doc3 =
            createContentDocument( new ContentKey( 3 ), new CategoryKey( 2 ), new ContentTypeKey( 1 ), 2, "title3", null );
        doc3.addUserDefinedField( "data/person/age", "30" );
        ContentDocument doc4 =
            createContentDocument( new ContentKey( 4 ), new CategoryKey( 2 ), new ContentTypeKey( 2 ), 2, "title4", null );
        doc4.addUserDefinedField( "data/person/age", "40" );

        contentIndexService.index( doc1 );
        contentIndexService.index( doc2 );
        contentIndexService.index( doc3 );
        contentIndexService.index( doc4 );

        flushIndex();
    }


}
