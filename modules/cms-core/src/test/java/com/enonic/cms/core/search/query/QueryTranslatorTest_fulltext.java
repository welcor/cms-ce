package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_fulltext
    extends QueryTranslatorBaseTest
{
    @Test
    public void testFulltextQuery()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + +QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"term\" : {\n" +
            "      \"title._tokenized\" : \"world\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "title FT \"world\"" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
