package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.index.IndexValueQuery;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/9/12
 * Time: 12:48 PM
 */
public class IndexValueQueryTranslatorTest
    extends QueryTranslatorBaseTest
{

    IndexValueQueryTranslator translator = new IndexValueQueryTranslator();


    @Before
    public void setUp()
    {

    }

    @Test
    public void testStuff()
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
