package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_not_in
    extends QueryTranslatorBaseTest
{
    @Test
    public void testNotIn_string()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"match_all\" : {\n" +
            "        }\n" +
            "      },\n" +
            "      \"must_not\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"should\" : [ {\n" +
            "            \"term\" : {\n" +
            "              \"title\" : \"hello\"\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"term\" : {\n" +
            "              \"title\" : \"test 2\"\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"term\" : {\n" +
            "              \"title\" : \"my testcontent\"\n" +
            "            }\n" +
            "          } ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "title NOT IN (\"Hello\", \"Test 2\", \"my testcontent\")" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testNotIn_int()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"match_all\" : {\n" +
            "        }\n" +
            "      },\n" +
            "      \"must_not\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"should\" : [ {\n" +
            "            \"term\" : {\n" +
            "              \"myIntField_numeric\" : 1.0\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"term\" : {\n" +
            "              \"myIntField_numeric\" : 2.0\n" +
            "            }\n" +
            "          }, {\n" +
            "            \"term\" : {\n" +
            "              \"myIntField_numeric\" : 3.0\n" +
            "            }\n" +
            "          } ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "myIntField NOT IN (1, 2, 3)" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
