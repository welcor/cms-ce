package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_logical
    extends QueryTranslatorBaseTest
{

    @Test
    public void testLogicalQuery_or_key()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"should\" : [ {\n" +
            "        \"term\" : {\n" +
            "          \"key_numeric\" : 100.0\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"term\" : {\n" +
            "          \"key_numeric\" : 200.0\n" +
            "        }\n" +
            "      } ]\n" +
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
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : [ {\n" +
            "        \"term\" : {\n" +
            "          \"key_numeric\" : 100.0\n" +
            "        }\n" +
            "      }, {\n" +
            "        \"term\" : {\n" +
            "          \"title\" : \"test\"\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100 AND title = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }


}
