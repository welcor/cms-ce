package com.enonic.cms.core.search.query;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class QueryFieldAndValueTest
    extends IndexFieldNameConstants
{

    @Test
    public void testEmptyValue()
    {
        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( "test", null );
        assertEquals( "test", queryFieldAndValue.getFieldName() );
        assertEquals( "", queryFieldAndValue.getValue() );
        assertEquals( "", queryFieldAndValue.getValueForIdQuery() );
    }


    @Test
    public void testWildcardPathGeneration()
    {
        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( "data/*", 35 );
        assertEquals( "_all_userdata.number", queryFieldAndValue.getFieldName() );
        assertEquals( 35.0, queryFieldAndValue.getValue() );

        queryFieldAndValue = new QueryFieldAndValue( "data/*", "35" );
        assertEquals( "_all_userdata", queryFieldAndValue.getFieldName() );
        assertEquals( "35", queryFieldAndValue.getValue() );

        queryFieldAndValue = new QueryFieldAndValue( "*", 35 );
        assertEquals( "_all_userdata.number", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( "*", new DateTime( 2010, 8, 1, 10, 00 ) );
        assertEquals( "_all_userdata.date", queryFieldAndValue.getFieldName() );
        assertEquals( toUTCTimeZone( new DateTime( 2010, 8, 1, 10, 00 ) ), queryFieldAndValue.getValue() );
    }

    private ReadableDateTime toUTCTimeZone( final ReadableDateTime dateTime )
    {
        if ( DateTimeZone.UTC.equals( dateTime.getZone() ) )
        {
            return dateTime;
        }
        final MutableDateTime dateInUTC = dateTime.toMutableDateTime();
        dateInUTC.setZone( DateTimeZone.UTC );
        return dateInUTC.toDateTime();
    }

    @Test
    public void testNumericPathGeneration()
    {
        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( "data_person_age", 35 );
        assertEquals( "data_person_age.number", queryFieldAndValue.getFieldName() );
        assertTrue( queryFieldAndValue.getValue() instanceof Number );

        queryFieldAndValue = new QueryFieldAndValue( "data_person_age", "35" );
        assertEquals( "data_person_age", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( "person_age", 35 );
        assertEquals( "person_age.number", queryFieldAndValue.getFieldName() );
        assertTrue( queryFieldAndValue.getValue() instanceof Number );
    }


    @Test
    public void testDatePathGeneration()
    {
        DateTime validDateTime = new DateTime( 2010, 8, 1, 10, 00, 00, 00 );

        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( "data_person_birthdate", validDateTime );
        assertEquals( "data_person_birthdate.date", queryFieldAndValue.getFieldName() );
        assertTrue( queryFieldAndValue.getValue() instanceof ReadableDateTime );

        queryFieldAndValue = new QueryFieldAndValue( "data_person_birthdate", validDateTime.toString() );
        assertEquals( "data_person_birthdate", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( "person_birthdate", validDateTime );
        assertEquals( "person_birthdate.date", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( PUBLISH_FROM_FIELDNAME, validDateTime );
        assertEquals( PUBLISH_FROM_FIELDNAME + ".date", queryFieldAndValue.getFieldName() );
        assertTrue( queryFieldAndValue.getValue() instanceof ReadableDateTime );

        queryFieldAndValue = new QueryFieldAndValue( PUBLISH_FROM_FIELDNAME, null );
        assertEquals( PUBLISH_FROM_FIELDNAME + ".date", queryFieldAndValue.getFieldName() );
    }

}
