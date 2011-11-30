package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.ContentSearchQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_logical
        extends QueryTranslatorBaseTest
{

    @Test
    public void testLogicalQuery_or_key()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : 0,\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                        "      \"should\" : [ {\n" + "        \"term\" : {\n" + "          \"key_numeric\" : 100.0\n" +
                        "        }\n" + "      }, {\n" + "        \"term\" : {\n" +
                        "          \"key_numeric\" : 200.0\n" + "        }\n" + "      } ]\n" + "    }\n" + "  },\n" +
                        "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" + "  \"sort\" : [ {\n" +
                        "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentSearchQuery query = createContentQuery( "key = 100 OR key = 200" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_and_key_and_title()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : 0,\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                        "      \"must\" : [ {\n" + "        \"term\" : {\n" + "          \"key_numeric\" : 100.0\n" +
                        "        }\n" + "      }, {\n" + "        \"term\" : {\n" + "          \"title\" : \"test\"\n" +
                        "        }\n" + "      } ]\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" +
                        "    \"match_all\" : {\n" + "    }\n" + "  },\n" + "  \"sort\" : [ {\n" +
                        "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentSearchQuery query = createContentQuery( "key = 100 AND title = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );

    }


}
