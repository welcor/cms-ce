package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

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
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"sort\" : [ {\n" +
            "    \"orderby_key\" : {\n" +
            "      \"order\" : \"desc\",\n" +
            "      \"ignore_unmapped\" : true\r\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "ORDER BY key DESC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testOrderBy_key_asc()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"sort\" : [ {\n" +
            "    \"orderby_key\" : {\n" +
            "      \"order\" : \"asc\",\n" +
            "      \"ignore_unmapped\" : true\r\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "ORDER BY key ASC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testEquals_key_int_order_by_key_asc()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"ids\" : {\n" +
            "      \"type\" : \"content\",\n" +
            "      \"values\" : [ \"100\" ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"sort\" : [ {\n" +
            "    \"orderby_key\" : {\n" +
            "      \"order\" : \"asc\",\n" +
            "      \"ignore_unmapped\" : true\r\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100 ORDER BY key ASC" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
