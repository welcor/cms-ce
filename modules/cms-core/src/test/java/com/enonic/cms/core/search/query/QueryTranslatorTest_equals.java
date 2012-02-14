package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_equals
    extends QueryTranslatorBaseTest
{

    @Test
    public void testEquals_key_string()
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
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = '100'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


    @Test
    public void testEquals_key_int()
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
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testEquals_key_double()
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
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "key = 100.0" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

}
