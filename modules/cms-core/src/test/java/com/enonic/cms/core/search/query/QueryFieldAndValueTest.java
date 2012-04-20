package com.enonic.cms.core.search.query;


import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class QueryFieldAndValueTest
    extends IndexFieldNameConstants
{

    @Ignore
    @Test
    public void testWildcardPathGeneration()
    {
        QueryFieldAndValue queryFieldAndValue = new QueryFieldAndValue( "data/*", 35 );
        assertEquals( "_all_userdata", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( "data/*", "35" );
        assertEquals( "_all_userdata", queryFieldAndValue.getFieldName() );

        queryFieldAndValue = new QueryFieldAndValue( "*", 35 );
        assertEquals( "_all_userdata.number", queryFieldAndValue.getFieldName() );
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
