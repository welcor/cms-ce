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
        String expected_result =
            "{\r\n" + "  \"range\" : {\r\n" + "    \"key\" : {\r\n" + "      \"from\" : \"100\",\r\n" + "      \"to\" : null,\r\n" +
                "      \"include_lower\" : false,\r\n" + "      \"include_upper\" : true\r\n" + "    }\r\n" + "  }\r\n" + "}";

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", "100", null, false, true );

        assertEquals( expected_result, query.toString() );
    }

    @Test
    public void testBuildRangeQuery_key_int()
    {
        String expected_result =
            "{\r\n" + "  \"range\" : {\r\n" + "    \"key_numeric\" : {\r\n" + "      \"from\" : 100,\r\n" + "      \"to\" : null,\r\n" +
                "      \"include_lower\" : false,\r\n" + "      \"include_upper\" : true\r\n" + "    }\r\n" + "  }\r\n" + "}";

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", 100, null, false, true );

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
        String expected_result =
            "{\r\n" + "  \"range\" : {\r\n" + "    \"key_numeric\" : {\r\n" + "      \"from\" : 100,\r\n" + "      \"to\" : 300,\r\n" +
                "      \"include_lower\" : true,\r\n" + "      \"include_upper\" : true\r\n" + "    }\r\n" + "  }\r\n" + "}";

        QueryBuilder query = RangeQueryBuilder.buildRangeQuery( "key", 100, 300, true, true );

        assertEquals( expected_result, query.toString() );
    }
}
