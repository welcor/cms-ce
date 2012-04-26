package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_date
    extends QueryTranslatorBaseTest
{

    protected static final String QUERY_DATE = "2011-11-15";

    @Test
    public void test_equals_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp.date\" : {\n" +
            "            \"from\" : \"2011-11-14T23:00:00.000Z\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp.date\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"2011-11-15T22:59:59.999Z\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp = date('" + QUERY_DATE + "')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_range_between_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp.date\" : {\n" +
            "            \"from\" : \"2011-11-14T23:00:00.000Z\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp.date\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"2011-11-15T22:59:59.000Z\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query =
            createContentQuery( "timestamp >= date('" + QUERY_DATE + "') AND timestamp <= date('2011-11-15T23:59:59')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_after_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp.date\" : {\n" +
            "        \"from\" : \"2011-11-14T23:00:00.000Z\",\n" +
            "        \"to\" : null,\n" +
            "        \"include_lower\" : false,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp > date('2011-11-15')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp.date\" : {\n" +
            "        \"from\" : null,\n" +
            "        \"to\" : \"2011-11-14T23:00:00.000Z\",\n" +
            "        \"include_lower\" : true,\n" +
            "        \"include_upper\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp < date('2011-11-15')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_or_equal_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp.date\" : {\n" +
            "        \"from\" : null,\n" +
            "        \"to\" : \"2011-11-15T22:59:59.999Z\",\n" +
            "        \"include_lower\" : true,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp <= date('2011-11-15')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_empty_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"match_all\" : {\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\" : {\n" +
            "        \"missing\" : {\n" +
            "          \"field\" : \"timestamp.date\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp = ''" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_number_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"timestamp\" : \"12345678\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '12345678'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_wrong_format()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"timestamp\" : \"2011/11/15\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '2011/11/15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_date_function()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"range\" : {\n" +
            "          \"my_date_field.date\" : {\n" +
            "            \"from\" : \"2012-03-21T23:00:00.000Z\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"range\" : {\n" +
            "          \"my_date_field.date\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"2012-03-22T22:59:59.999Z\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "my_date_field = date('2012-03-22')" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
