package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;

public class AggregatedQueryTranslatorTest
    extends QueryTranslatorTestBase
{

    @Test
    public void test_plain_query()
    {
        String expectedResult = "{\n" +
            "  \"size\" : 0,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : { }\n" +
            "  },\n" +
            "  \"facets\" : {\n" +
            "    \"aggregatedQuery\" : {\n" +
            "      \"statistical\" : {\n" +
            "        \"field\" : \"age.number\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        AggregatedQueryTranslator translator = new AggregatedQueryTranslator();

        AggregatedQuery query = new AggregatedQuery( "age" );

        final SearchSourceBuilder build = translator.build( query );

        compareStringsIgnoreFormatting( expectedResult, build.toString() );
    }

    @Test
    public void test_filtered_query()
    {
        String expectedResult = "{\n" +
            "  \"size\" : 0,\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : { }\n" +
            "  },\n" +
            "  \"facets\" : {\n" +
            "    \"aggregatedQuery\" : {\n" +
            "      \"statistical\" : {\n" +
            "        \"field\" : \"age.number\"\n" +
            "      },\n" +
            "      \"facet_filter\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"categorykey\" : [ \"1\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"contenttypekey\" : [ \"1\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        AggregatedQueryTranslator translator = new AggregatedQueryTranslator();

        AggregatedQuery query = new AggregatedQuery( "age" );
        query.setCategoryFilter( Lists.newArrayList( new CategoryKey( 1 ) ) );
        query.setContentTypeFilter( Lists.newArrayList( new ContentTypeKey( 1 ) ) );

        final SearchSourceBuilder build = translator.build( query );

        compareStringsIgnoreFormatting( expectedResult, build.toString() );
    }
}
