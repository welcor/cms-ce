package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_logical
    extends QueryTranslatorBaseTest
{

    @Test
    public void testLogicalQuery_or_key()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"should\" : [ {\r\n" + "        \"term\" : {\r\n" +
                "          \"key_numeric\" : 100.0\r\n" + "        }\r\n" + "      }, {\r\n" + "        \"term\" : {\r\n" +
                "          \"key_numeric\" : 200.0\r\n" + "        }\r\n" + "      } ]\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key = 100 OR key = 200" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_and_key_and_title()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"bool\" : {\r\n" + "      \"must\" : [ {\r\n" + "        \"term\" : {\r\n" + "          \"key_numeric\" : 100.0\r\n" +
                "        }\r\n" + "      }, {\r\n" + "        \"term\" : {\r\n" + "          \"title\" : \"test\"\r\n" + "        }\r\n" +
                "      } ]\r\n" + "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "key = 100 AND title = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );

    }


}
