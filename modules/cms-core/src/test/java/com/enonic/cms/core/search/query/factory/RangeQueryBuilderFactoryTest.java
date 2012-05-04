package com.enonic.cms.core.search.query.factory;

import org.elasticsearch.index.query.QueryBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.enonic.cms.core.search.query.QueryFieldFactory;
import com.enonic.cms.core.search.query.QueryTranslatorBaseTest;
import com.enonic.cms.core.search.query.QueryValue;

import static junit.framework.Assert.assertEquals;

public class RangeQueryBuilderFactoryTest
    extends QueryTranslatorBaseTest
{
    private final RangeQueryBuilderFactory rangeQueryBuilderFactory = new RangeQueryBuilderFactory();

    @Test
    public void testBuildRangeQuery_key_string()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"key\" : {\n" +
            "      \"from\" : \"100\",\n" +
            "      \"to\" : null,\n" +
            "      \"include_lower\" : false,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryBuilder query =
            rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "key" ), new QueryValue( "100" ), null, false,
                                                      true );

        System.out.println( query.toString() );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_key_int()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"key.number\" : {\n" +
            "      \"from\" : 100.0,\n" +
            "      \"to\" : null,\n" +
            "      \"include_lower\" : false,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryBuilder query =
            rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "key" ), new QueryValue( 100 ), null, false,
                                                      true );

        System.out.println( query.toString() );
        assertEquals( expected_result, query.toString() );
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuildRangeQuery_null_range()
    {
        rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "key" ), null, null, false, true );
    }

    @Test
    public void testBuildRangeQuery_key_int_low_include()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"key.number\" : {\n" +
            "      \"from\" : 100.0,\n" +
            "      \"to\" : 300.0,\n" +
            "      \"include_lower\" : true,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryBuilder query = rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "key" ), new QueryValue( 100 ),
                                                                       new QueryValue( 300 ), true, true );
        System.out.println( query.toString() );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_date_low()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"my_date_field.date\" : {\n" +
            "      \"from\" : \"2012-03-23T14:23:45.678Z\",\n" +
            "      \"to\" : null,\n" +
            "      \"include_lower\" : false,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        DateTime initTime = new DateTime( 2012, 3, 23, 15, 23, 45, 678, DateTimeZone.forID( "Europe/Oslo" ) );
        QueryBuilder query =
            rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "my_date_field" ), new QueryValue( initTime ),
                                                      null, false, true );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_date_low_include()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"my_date_field.date\" : {\n" +
            "      \"from\" : \"2012-03-23T14:23:45.678Z\",\n" +
            "      \"to\" : null,\n" +
            "      \"include_lower\" : true,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        DateTime initTime = new DateTime( 2012, 3, 23, 15, 23, 45, 678, DateTimeZone.forID( "Europe/Oslo" ) );
        QueryBuilder query =
            rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "my_date_field" ), new QueryValue( initTime ),
                                                      null, true, true );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_date_low_high()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"my_date_field.date\" : {\n" +
            "      \"from\" : \"2012-03-23T14:23:45.678Z\",\n" +
            "      \"to\" : \"2012-03-24T04:01:23.456Z\",\n" +
            "      \"include_lower\" : false,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        DateTime initTime = new DateTime( 2012, 3, 23, 15, 23, 45, 678, DateTimeZone.forID( "Europe/Oslo" ) );
        DateTime endTime = new DateTime( 2012, 3, 24, 5, 1, 23, 456, DateTimeZone.forID( "Europe/Oslo" ) );
        QueryBuilder query =
            rangeQueryBuilderFactory.buildRangeQuery( QueryFieldFactory.resolveQueryField( "my_date_field" ), new QueryValue( initTime ),
                                                      new QueryValue( endTime ), false, true );

        assertEquals( expected_result, query.toString() );
    }
}
