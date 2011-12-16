package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.queryexpression.QueryParserException;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_date
    extends QueryTranslatorBaseTest
{
    @Test
    public void test_equals_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : 2147483647,\r\n" + "  \"query\" : {\r\n" + "    \"bool\" : {\r\n" +
                "      \"must\" : [ {\r\n" + "        \"range\" : {\r\n" + "          \"timestamp\" : {\r\n" +
                "            \"from\" : \"2011-11-15t00:00:00.000+02:00\",\r\n" + "            \"to\" : null,\r\n" +
                "            \"include_lower\" : true,\r\n" + "            \"include_upper\" : true\r\n" + "          }\r\n" +
                "        }\r\n" + "      }, {\r\n" + "        \"range\" : {\r\n" + "          \"timestamp\" : {\r\n" +
                "            \"from\" : null,\r\n" + "            \"to\" : \"2011-11-15t23:59:59.999+02:00\",\r\n" +
                "            \"include_lower\" : true,\r\n" + "            \"include_upper\" : true\r\n" + "          }\r\n" +
                "        }\r\n" + "      } ]\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_range_between_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : 2147483647,\r\n" + "  \"query\" : {\r\n" + "    \"bool\" : {\r\n" +
                "      \"must\" : [ {\r\n" + "        \"range\" : {\r\n" + "          \"timestamp\" : {\r\n" +
                "            \"from\" : \"2011-11-15t00:00:00.000+02:00\",\r\n" + "            \"to\" : null,\r\n" +
                "            \"include_lower\" : true,\r\n" + "            \"include_upper\" : true\r\n" + "          }\r\n" +
                "        }\r\n" + "      }, {\r\n" + "        \"range\" : {\r\n" + "          \"timestamp\" : {\r\n" +
                "            \"from\" : null,\r\n" + "            \"to\" : \"2011-11-15t23:59:59.000+02:00\",\r\n" +
                "            \"include_lower\" : true,\r\n" + "            \"include_upper\" : true\r\n" + "          }\r\n" +
                "        }\r\n" + "      } ]\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp >= '2011-11-15T00:00:00' AND timestamp <= '2011-11-15T23:59:59'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_after_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"timestamp\" : {\r\n" + "        \"from\" : \"2011-11-15t00:00:00.000+02:00\",\r\n" +
                "        \"to\" : null,\r\n" + "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" +
                "      }\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp > '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"timestamp\" : {\r\n" + "        \"from\" : null,\r\n" +
                "        \"to\" : \"2011-11-15t00:00:00.000+02:00\",\r\n" + "        \"include_lower\" : true,\r\n" +
                "        \"include_upper\" : false\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp < '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_or_equal_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"timestamp\" : {\r\n" + "        \"from\" : null,\r\n" +
                "        \"to\" : \"2011-11-15t23:59:59.999+02:00\",\r\n" + "        \"include_lower\" : true,\r\n" +
                "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp <= '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_empty_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"term\" : {\r\n" + "      \"timestamp\" : \"\"\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = ''" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_number_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"term\" : {\r\n" + "      \"timestamp\" : \"12345678\"\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '12345678'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_not_format_date()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"term\" : {\r\n" + "      \"timestamp\" : \"2011/11/15\"\r\n" + "    }\r\n" + "  }\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '2011/11/15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test(expected = QueryParserException.class)
    public void test_null_date()
        throws Exception
    {
        ContentIndexQuery query = createContentQuery( "timestamp = " + null );

        SearchSourceBuilder builder = getQueryTranslator().build( query );
    }

}
