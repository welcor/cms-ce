package com.enonic.cms.itest.search;

import java.util.Map;

import org.elasticsearch.search.SearchHitField;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/6/12
 * Time: 9:57 AM
 */
public class ContentIndexServiceImpl_queryCustomdataTest
    extends ContentIndexServiceTestBase
{
    @Test
    public void testQueryOnNumericAndNotNumericValue()
    {
        setUpStandardTestValues();

        final ContentKey contentKey = new ContentKey( 1322 );
        final Map<String, SearchHitField> fieldMapForId = getFieldMapForId( contentKey );

        final SearchHitField data_person_age = fieldMapForId.get( "data_person_age" );
        assertNotNull( data_person_age );
        final SearchHitField data_person_age_numeric = fieldMapForId.get( "data_person_age.number" );
        assertNotNull( data_person_age_numeric );

        final Object age = data_person_age.getValue();
        ContentIndexQuery query = new ContentIndexQuery( "data/person/age = " + age + " AND " +
                                                             "data/person/age = '" + age + "'" );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 1, resultSet.getTotalCount() );
        assertTrue( resultSet.containsContent( contentKey ) );
    }

    @Test
    public void testDoubleTermQuery()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/age = 5 or data/person/age = 38.0 " );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 2, resultSet.getTotalCount() );
    }

    @Test
    public void testDoubleRangeQuery()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/age > 5 and data/person/age < 38.0 " );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 2, resultSet.getTotalCount() );
    }

    @Test
    public void testDoubleInQuery()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/age in (28.0, 10.0)" );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 2, resultSet.getTotalCount() );
    }

    @Test
    public void testDoubleAndStringInQuery()
    {
        setUpStandardTestValues();

        ContentIndexQuery query = new ContentIndexQuery( "data/person/age in (28.0, '10')" );

        final ContentResultSet resultSet = contentIndexService.query( query );
        assertEquals( 2, resultSet.getTotalCount() );
    }
}
