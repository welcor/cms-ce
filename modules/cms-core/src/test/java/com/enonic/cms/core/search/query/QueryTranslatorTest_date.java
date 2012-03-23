package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
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

        DateTime startTime = new DateTime( QUERY_DATE );
        DateTime endTime = new DateTime( 2011, 11, 15, 23, 59, 59, 999 );

        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp\" : {\n" +
            "            \"from\" : \"" + startTime + "\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"" + endTime + "\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp = '" + QUERY_DATE + "'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_range_between_date()
        throws Exception
    {

        DateTime expectedStartTime = new DateTime( QUERY_DATE );
        DateTime expectedEndTime = new DateTime( 2011, 11, 15, 23, 59, 59, 000 );

        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp\" : {\n" +
            "            \"from\" : \"" + expectedStartTime + "\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"range\" : {\n" +
            "          \"timestamp\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"" + expectedEndTime + "\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp >= '" + QUERY_DATE + "' AND timestamp <= '2011-11-15T23:59:59'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_after_date()
        throws Exception
    {
        DateTime expectedStartTime = new DateTime( QUERY_DATE );

        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp\" : {\n" +
            "        \"from\" : \"" + expectedStartTime + "\",\n" +
            "        \"to\" : null,\n" +
            "        \"include_lower\" : false,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp > '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_date()
        throws Exception
    {

        DateTime expectedEndTime = new DateTime( QUERY_DATE );

        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp\" : {\n" +
            "        \"from\" : null,\n" +
            "        \"to\" : \"" + expectedEndTime + "\",\n" +
            "        \"include_lower\" : true,\n" +
            "        \"include_upper\" : false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp < '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_before_or_equal_date()
        throws Exception
    {
        DateTime expectedEndTime = new DateTime( 2011, 11, 15, 23, 59, 59, 999 );

        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"timestamp\" : {\n" +
            "        \"from\" : null,\n" +
            "        \"to\" : \"" + expectedEndTime + "\",\n" +
            "        \"include_lower\" : true,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "timestamp <= '2011-11-15'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void test_empty_date()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"match_all\" : {\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\" : {\n" +
            "        \"missing\" : {\n" +
            "          \"field\" : \"timestamp\"\n" +
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
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"term\" : {\r\n" + "      \"timestamp\" : \"12345678\"\r\n" + "    }\r\n" + "  }\r\n" + "}";

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

}
