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
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : 2147483647,\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" + "        \"range\" : {\n" + "          \"timestamp\" : {\n" +
                "            \"from\" : \"2011-11-15t00:00:00.000+02:00\",\n" + "            \"to\" : null,\n" +
                "            \"include_lower\" : true,\n" + "            \"include_upper\" : true\n" + "          }\n" + "        }\n" +
                "      }, {\n" + "        \"range\" : {\n" + "          \"timestamp\" : {\n" + "            \"from\" : null,\n" +
                "            \"to\" : \"2011-11-15t23:59:59.999+02:00\",\n" + "            \"include_lower\" : true,\n" +
                "            \"include_upper\" : true\n" + "          }\n" + "        }\n" + "      } ]\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_range_between_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : 2147483647,\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"must\" : [ {\n" + "        \"range\" : {\n" + "          \"timestamp\" : {\n" +
                "            \"from\" : \"2011-11-15t00:00:00.000+02:00\",\n" + "            \"to\" : null,\n" +
                "            \"include_lower\" : true,\n" + "            \"include_upper\" : true\n" + "          }\n" + "        }\n" +
                "      }, {\n" + "        \"range\" : {\n" + "          \"timestamp\" : {\n" + "            \"from\" : null,\n" +
                "            \"to\" : \"2011-11-15t23:59:59.000+02:00\",\n" + "            \"include_lower\" : true,\n" +
                "            \"include_upper\" : true\n" + "          }\n" + "        }\n" + "      } ]\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp >= '2011-11-15T00:00:00' AND timestamp <= '2011-11-15T23:59:59'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_after_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                "      \"timestamp\" : {\n" + "        \"from\" : \"2011-11-15t00:00:00.000+02:00\",\n" + "        \"to\" : null,\n" +
                "        \"include_lower\" : false,\n" + "        \"include_upper\" : true\n" + "      }\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp > '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                "      \"timestamp\" : {\n" + "        \"from\" : null,\n" + "        \"to\" : \"2011-11-15t00:00:00.000+02:00\",\n" +
                "        \"include_lower\" : true,\n" + "        \"include_upper\" : false\n" + "      }\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp < '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_or_equal_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                "      \"timestamp\" : {\n" + "        \"from\" : null,\n" + "        \"to\" : \"2011-11-15t23:59:59.999+02:00\",\n" +
                "        \"include_lower\" : true,\n" + "        \"include_upper\" : true\n" + "      }\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp <= '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_empty_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"term\" : {\n" +
                "      \"timestamp\" : \"\"\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = ''" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_number_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"term\" : {\n" +
                "      \"timestamp\" : \"12345678\"\n" + "    }\n" + "  }\n" + "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '12345678'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void test_not_format_date()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"term\" : {\n" +
                "      \"timestamp\" : \"2011/11/15\"\n" + "    }\n" + "  }\n" + "}";

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
