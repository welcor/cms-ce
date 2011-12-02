package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_in
    extends QueryTranslatorBaseTest
{
    @Test
    public void testIn_string()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                "      \"should\" : [ {\n" + "        \"term\" : {\n" + "          \"title\" : \"hello\"\n" + "        }\n" +
                "      }, {\n" + "        \"term\" : {\n" + "          \"title\" : \"test 2\"\n" + "        }\n" + "      }, {\n" +
                "        \"term\" : {\n" + "          \"title\" : \"my testcontent\"\n" + "        }\n" + "      } ]\n" + "    }\n" +
                "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" + "  \"sort\" : [ {\n" +
                "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "title IN (\"Hello\", \"Test 2\", \"my testcontent\")" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testIn_int()
        throws Exception
    {
        String expected_search_result =
            "{\n" + "  \"from\" : 0,\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" + "      \"should\" : [ {\n" +
                "        \"term\" : {\n" + "          \"key\" : 1.0\n" + "        }\n" + "      }, {\n" + "        \"term\" : {\n" +
                "          \"key\" : 2.0\n" + "        }\n" + "      }, {\n" + "        \"term\" : {\n" + "          \"key\" : 3.0\n" +
                "        }\n" + "      } ]\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" +
                "  },\n" + "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "key IN (1, 2, 3)" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
