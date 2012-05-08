package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_like
    extends QueryTranslatorBaseTest
{
    @Test
    public void testLike_characters()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"wildcard\" : {\n" +
            "      \"title\" : \"boat*\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"Boat*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLike_special_characters()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"wildcard\" : {\n" +
            "      \"title\" : \"*$&*\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"*$&*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLike_backslash()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"wildcard\" : {\n" +
            "      \"title\" : \"*\\\\*\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "title LIKE \"*\\\\*\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
