package com.enonic.cms.core.search.query;

import java.util.Set;

import org.elasticsearch.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryFieldAndMultiValueTest
{

    @Test
    public void testMultiValueNumeric()
    {
        Set<QueryValue> queryValues = Sets.newHashSet();

        queryValues.add( new QueryValue( 1 ) );
        queryValues.add( new QueryValue( 2 ) );
        queryValues.add( new QueryValue( null ) );

        QueryFieldAndMultiValue queryFieldAndMultiValue = new QueryFieldAndMultiValue( "test", queryValues );

        final Object[] values = queryFieldAndMultiValue.getValues();

        assertEquals( 2, values.length );

        for ( Object value : values )
        {
            assertTrue( value instanceof Double );
        }

        final String fieldName = queryFieldAndMultiValue.getFieldName();
        assertEquals( "test.number", fieldName );
    }

    @Test
    public void testMultiValueDates()
    {
        Set<QueryValue> queryValues = Sets.newHashSet();

        queryValues.add( new QueryValue( new DateTime( 2012, 5, 2, 9, 46 ) ) );
        queryValues.add( new QueryValue( new DateTime( 2011, 5, 2, 9, 46 ) ) );
        queryValues.add( new QueryValue( null ) );

        QueryFieldAndMultiValue queryFieldAndMultiValue = new QueryFieldAndMultiValue( "test", queryValues );

        final Object[] values = queryFieldAndMultiValue.getValues();

        assertEquals( 2, values.length );

        for ( Object value : values )
        {
            assertTrue( value instanceof ReadableDateTime );
        }

        final String fieldName = queryFieldAndMultiValue.getFieldName();
        assertEquals( "test.date", fieldName );
    }

    @Test
    public void testEmptyValues()
    {
        Set<QueryValue> queryValues = Sets.newHashSet();

        queryValues.add( new QueryValue( null ) );
        queryValues.add( new QueryValue( null ) );
        queryValues.add( new QueryValue( null ) );

        QueryFieldAndMultiValue queryFieldAndMultiValue = new QueryFieldAndMultiValue( "test", queryValues );

        final Object[] values = queryFieldAndMultiValue.getValues();

        assertEquals( 0, values.length );

        final String fieldName = queryFieldAndMultiValue.getFieldName();
        assertEquals( "test", fieldName );
    }

    @Test
    public void testMixedValuesShouldYieldString()
    {
        Set<QueryValue> queryValues = Sets.newHashSet();

        queryValues.add( new QueryValue( 1 ) );
        queryValues.add( new QueryValue( new DateTime( 2012, 5, 2, 9, 46 ) ) );
        queryValues.add( new QueryValue( null ) );
        queryValues.add( new QueryValue( "testValue" ) );

        QueryFieldAndMultiValue queryFieldAndMultiValue = new QueryFieldAndMultiValue( "test", queryValues );

        final Object[] values = queryFieldAndMultiValue.getValues();

        assertEquals( 3, values.length );

        for ( Object value : values )
        {
            assertTrue( value instanceof String );
        }

        final String fieldName = queryFieldAndMultiValue.getFieldName();
        assertEquals( "test", fieldName );
    }


}


