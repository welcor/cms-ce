package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslator_logicalTest
    extends QueryTranslatorTestBase
{

    @Test
    public void testLogicalQuery_or_key()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"should\" : [ {\n" +
            "            \"ids\" : {\n" +
            "              \"type\" : \"content\",\n" +
            "              \"values\" : [ \"100\" ]\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"ids\" : {\n" +
            "              \"type\" : \"content\",\n" +
            "              \"values\" : [ \"200\" ]\n" +
            "            }\n" +
            "          } ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100 OR key = 200" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_and_key_and_title()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : [ {\n" +
            "            \"ids\" : {\n" +
            "              \"type\" : \"content\",\n" +
            "              \"values\" : [ \"100\" ]\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"term\" : {\n" +
            "              \"title\" : \"test\"\n" +
            "            }\n" +
            "          } ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100 AND title = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }


}
