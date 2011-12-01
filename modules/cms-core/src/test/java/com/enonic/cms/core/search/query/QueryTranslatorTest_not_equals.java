package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.ContentSearchQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_not_equals
        extends QueryTranslatorBaseTest
{

    @Test
    public void testNotQuery_key()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : "+ QUERY_DEFAULT_SIZE +",\n" + "  \"query\" : {\n" + "    \"bool\" : {\n" +
                        "      \"must\" : {\n" + "        \"match_all\" : {\n" + "        }\n" + "      },\n" +
                        "      \"must_not\" : {\n" + "        \"term\" : {\n" + "          \"key_numeric\" : 100.0\n" +
                        "        }\n" + "      }\n" + "    }\n" + "  },\n" + "  \"filter\" : {\n" +
                        "    \"match_all\" : {\n" + "    }\n" + "  },\n" + "  \"sort\" : [ {\n" +
                        "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "key != 100" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );

    }
}
