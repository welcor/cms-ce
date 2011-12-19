package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

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
    public void testOrderBy_key_desc()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"match_all\" : {\r\n" + "    }\r\n" + "  },\r\n" + "  \"sort\" : [ {\r\n" + "    \"orderby_key\" : {\r\n" +
                "      \"order\" : \"desc\"\r\n" + "    }\r\n" + "  } ]\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "ORDER BY key DESC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testOrderBy_key_asc()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"match_all\" : {\r\n" + "    }\r\n" + "  },\r\n" + "  \"sort\" : [ {\r\n" + "    \"orderby_key\" : {\r\n" +
                "      \"order\" : \"asc\"\r\n" + "    }\r\n" + "  } ]\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "ORDER BY key ASC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testEquals_key_int_order_by_key_asc()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"term\" : {\r\n" + "      \"key_numeric\" : 100.0\r\n" + "    }\r\n" + "  },\r\n" + "  \"sort\" : [ {\r\n" +
                "    \"orderby_key\" : {\r\n" + "      \"order\" : \"asc\"\r\n" + "    }\r\n" + "  } ]\r\n" + "}";

        ContentIndexQuery query = createContentQuery( "key = 100 ORDER BY key ASC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
