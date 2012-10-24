package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslator_rangeTest
    extends QueryTranslatorTestBase
{

    @Test
    public void testGreaterThan_key_int()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key.number\" : {\n" +
            "            \"from\" : 100.0,\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : false,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key > 100" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testGreaterThan_key_double()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key.number\" : {\n" +
            "            \"from\" : 100.0,\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : false,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key > 100.0" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }

    @Test
    public void testGreaterThan_key_string()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key\" : {\n" +
            "            \"from\" : \"100\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : false,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key > '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testGreaterThanEquals_key_string()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key\" : {\n" +
            "            \"from\" : \"100\",\n" +
            "            \"to\" : null,\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key >= '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLessThan_key_string()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"100\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : false\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key < '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLessThanEquals_key_string()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"range\" : {\n" +
            "          \"key\" : {\n" +
            "            \"from\" : null,\n" +
            "            \"to\" : \"100\",\n" +
            "            \"include_lower\" : true,\n" +
            "            \"include_upper\" : true\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key <= '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


}
