package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslator_attachmentsTest
    extends QueryTranslatorTestBase
{

    @Test
    public void testAttachmentQuery()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"has_child\" : {\n" +
            "      \"query\" : {\n" +
            "        \"term\" : {\n" +
            "          \"attachment\" : \"test\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"type\" : \"binaries\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "attachment/* = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }

    @Test
    public void testAliasedAttachementField()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"has_child\" : {\n" +
            "      \"query\" : {\n" +
            "        \"term\" : {\n" +
            "          \"attachment\" : \"test\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"type\" : \"binaries\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        ContentIndexQuery query = createContentQuery( "fulltext = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }


}
