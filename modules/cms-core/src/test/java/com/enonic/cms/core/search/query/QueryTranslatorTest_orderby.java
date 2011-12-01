package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.ContentSearchQuery;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: udu
 * Date: 11/29/11
 * Time: 2:29 PM
 */
public class QueryTranslatorTest_orderby
        extends QueryTranslatorBaseTest
{
    @Test
    public void testEquals_key_string()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : "+ QUERY_DEFAULT_SIZE +",\n" + "  \"query\" : {\n" + "    \"match_all\" : {\n" +
                        "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" +
                        "  \"sort\" : [ {\n" + "    \"orderby_key\" : {\n" + "      \"order\" : \"desc\"\n" +
                        "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "ORDER BY key DESC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
