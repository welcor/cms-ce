package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslator_notTest
    extends QueryTranslatorTestBase
{
    @Test
    public void testNotExpression()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"match_all\" : { }\n" +
            "          },\n" +
            "          \"must_not\" : {\n" +
            "            \"bool\" : {\n" +
            "              \"should\" : [ {\n" +
            "                \"term\" : {\n" +
            "                  \"title\" : \"hello\"\n" +
            "                }\n" +
            "              }, {\n" +
            "                \"term\" : {\n" +
            "                  \"title\" : \"test 2\"\n" +
            "                }\n" +
            "              }, {\n" +
            "                \"term\" : {\n" +
            "                  \"title\" : \"my testcontent\"\n" +
            "                }\n" +
            "              } ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "NOT (title IN (\"Hello\", \"Test 2\", \"my testcontent\"))" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
