package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

import static junit.framework.Assert.assertEquals;

public class QueryTranslatorTest_like
    extends QueryTranslatorBaseTest
{
    @Test
    public void testLike_characters()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"wildcard\" : {\r\n" + "      \"title\" : {\r\n" + "        \"wildcard\" : \"b?t*\"\r\n" + "      }\r\n" +
                "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"B?t*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLike_special_characters()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"wildcard\" : {\r\n" + "      \"title\" : {\r\n" + "        \"wildcard\" : \"*$&*\"\r\n" + "      }\r\n" +
                "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"*$&*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLike_backslash()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"wildcard\" : {\r\n" + "      \"title\" : {\r\n" + "        \"wildcard\" : \"*\\\\*\"\r\n" + "      }\r\n" +
                "    }\r\n" + "  }\r\n}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"*\\\\*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }
}
