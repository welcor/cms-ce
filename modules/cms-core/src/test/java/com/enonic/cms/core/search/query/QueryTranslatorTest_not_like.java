package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_not_like
    extends QueryTranslatorBaseTest
{
    @Test
    public void testNotLike_characters()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"must\" : {\r\n" + "        \"match_all\" : {\r\n" + "        }\r\n" + "      },\r\n" +
                "      \"must_not\" : {\r\n" + "        \"wildcard\" : {\r\n" + "          \"title\" : {\r\n" +
                "            \"wildcard\" : \"b?t*\"\r\n" + "          }\r\n" + "        }\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"B?t*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testNotLike_special_characters()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"must\" : {\r\n" + "        \"match_all\" : {\r\n" + "        }\r\n" + "      },\r\n" +
                "      \"must_not\" : {\r\n" + "        \"wildcard\" : {\r\n" + "          \"title\" : {\r\n" +
                "            \"wildcard\" : \"*$&*\"\r\n" + "          }\r\n" + "        }\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"*$&*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testNotLike_backslash()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"must\" : {\r\n" + "        \"match_all\" : {\r\n" + "        }\r\n" + "      },\r\n" +
                "      \"must_not\" : {\r\n" + "        \"wildcard\" : {\r\n" + "          \"title\" : {\r\n" +
                "            \"wildcard\" : \"*\\\\*\"\r\n" + "          }\r\n" + "        }\r\n" + "      }\r\n" + "    }\r\n" +
                "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"*\\\\*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
