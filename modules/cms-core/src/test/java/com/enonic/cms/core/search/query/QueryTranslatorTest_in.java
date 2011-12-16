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
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"should\" : [ {\r\n" + "        \"term\" : {\r\n" + "          \"title\" : \"hello\"\r\n" +
                "        }\r\n" + "      }, {\r\n" + "        \"term\" : {\r\n" + "          \"title\" : \"test 2\"\r\n" + "        }\r\n" +
                "      }, {\r\n" + "        \"term\" : {\r\n" + "          \"title\" : \"my testcontent\"\r\n" + "        }\r\n" +
                "      } ]\r\n" + "    }\r\n  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title IN (\"Hello\", \"Test 2\", \"my testcontent\")" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testIn_int()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"should\" : [ {\r\n" + "        \"term\" : {\r\n" + "          \"key\" : 1.0\r\n" +
                "        }\r\n" + "      }, {\r\n" + "        \"term\" : {\r\n" + "          \"key\" : 2.0\r\n" + "        }\r\n" +
                "      }, {\r\n" + "        \"term\" : {\r\n" + "          \"key\" : 3.0\r\n" + "        }\r\n" + "      } ]\r\n" +
                "    }\r\n  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key IN (1, 2, 3)" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
