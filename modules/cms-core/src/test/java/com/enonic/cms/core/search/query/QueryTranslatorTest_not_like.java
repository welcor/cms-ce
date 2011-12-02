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
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"must\" : {\n" + "        \"match_all\" : {\n" + "        }\n" + "      },\n" + "      \"must_not\" : {\n" +
                "        \"wildcard\" : {\n" + "          \"title\" : {\n" + "            \"wildcard\" : \"b?t*\"\n" + "          }\n" +
                "        }\n" + "      }\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" +
                "  },\n" + "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"B?t*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testNotLike_special_characters()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"must\" : {\n" + "        \"match_all\" : {\n" + "        }\n" + "      },\n" + "      \"must_not\" : {\n" +
                "        \"wildcard\" : {\n" + "          \"title\" : {\n" + "            \"wildcard\" : \"*$&*\"\n" + "          }\n" +
                "        }\n" + "      }\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" +
                "  },\n" + "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"*$&*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testNotLike_backslash()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"must\" : {\n" + "        \"match_all\" : {\n" + "        }\n" + "      },\n" + "      \"must_not\" : {\n" +
                "        \"wildcard\" : {\n" + "          \"title\" : {\n" + "            \"wildcard\" : \"*\\\\*\"\n" + "          }\n" +
                "        }\n" + "      }\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" +
                "  },\n" + "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "title NOT LIKE \"*\\\\*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
