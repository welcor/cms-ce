package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: udu
 * Date: 11/29/11
 * Time: 3:46 PM
 */
public class RangeQueryBuilderTest
{
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

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", "100", null, false, true );

        System.out.println( query.toString() );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_key_int()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"key_numeric\" : {\n" +
            "      \"from\" : 100,\n" +
            "      \"to\" : null,\n" +
            "      \"include_lower\" : false,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", 100, null, false, true );

        System.out.println( query.toString() );
        assertEquals( expected_result, query.toString() );
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testBuildRangeQuery_null_range()
    {
        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", null, null, false, true );
    }

    @Test
    public void testBuildRangeQuery_key_int_low_include()
    {
        String expected_result = "{\n" +
            "  \"range\" : {\n" +
            "    \"key_numeric\" : {\n" +
            "      \"from\" : 100,\n" +
            "      \"to\" : 300,\n" +
            "      \"include_lower\" : true,\n" +
            "      \"include_upper\" : true\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", 100, 300, true, true );
        System.out.println( query.toString() );

        assertEquals( expected_result, query.toString() );
    }
}
