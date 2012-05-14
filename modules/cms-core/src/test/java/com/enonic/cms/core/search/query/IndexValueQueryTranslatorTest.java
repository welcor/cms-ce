package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.IndexValueQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/9/12
 * Time: 12:48 PM
 */
public class IndexValueQueryTranslatorTest
    extends QueryTranslatorTestBase
{

    IndexValueQueryTranslator translator = new IndexValueQueryTranslator();


    @Test
    public void testCreateIndexValueQuery()
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"fields\" : \"title\"\n" +
            "}";

        IndexValueQuery query = new IndexValueQuery( "title" );

        final SearchSourceBuilder builder = translator.build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


    @Test
    public void testCustomDataField()
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"fields\" : \"data_test\"\n" +
            "}";

        IndexValueQuery query = new IndexValueQuery( "data/test" );

        final SearchSourceBuilder builder = translator.build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


}
