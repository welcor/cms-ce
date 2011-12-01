package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.ContentSearchQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_range
        extends QueryTranslatorBaseTest
{

    @Test
    public void testGreaterThan_key_int()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : "+ QUERY_DEFAULT_SIZE +",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                        "      \"key_numeric\" : {\n" + "        \"from\" : 100.0,\n" + "        \"to\" : null,\n" +
                        "        \"include_lower\" : false,\n" + "        \"include_upper\" : true\n" + "      }\n" +
                        "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" +
                        "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "key > 100" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testGreaterThan_key_double()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : "+ QUERY_DEFAULT_SIZE +",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                        "      \"key_numeric\" : {\n" + "        \"from\" : 100.0,\n" + "        \"to\" : null,\n" +
                        "        \"include_lower\" : false,\n" + "        \"include_upper\" : true\n" + "      }\n" +
                        "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" +
                        "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "key > 100.0" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );

    }

    @Test
    public void testGreaterThan_key_string()
            throws Exception
    {
        String expected_search_result =
                "{\n" + "  \"from\" : 0,\n" + "  \"size\" : "+ QUERY_DEFAULT_SIZE +",\n" + "  \"query\" : {\n" + "    \"range\" : {\n" +
                        "      \"key\" : {\n" + "        \"from\" : \"100\",\n" + "        \"to\" : null,\n" +
                        "        \"include_lower\" : false,\n" + "        \"include_upper\" : true\n" + "      }\n" +
                        "    }\n" + "  },\n" + "  \"filter\" : {\n" + "    \"match_all\" : {\n" + "    }\n" + "  },\n" +
                        "  \"sort\" : [ {\n" + "    \"_score\" : {\n" + "    }\n" + "  } ]\n" + "}";

        ContentIndexQuery query = createContentQuery( "key > '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }


}
