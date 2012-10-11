package com.enonic.cms.core.search.query;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslator_filterCombinationsTest
    extends QueryTranslatorTestBase
{
    @Test
    public void testLogicalQuery_category_and_contenttype_filters()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : { }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"categorykey\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttypekey\" : [ \"1001\", \"1002\" ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );
        contentTypeFilter.add( new ContentTypeKey( "1002" ) );

        ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_category_contenttype_filters_with_query()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"key.number\" : {\n" +
            "        \"from\" : 100.0,\n" +
            "        \"to\" : null,\n" +
            "        \"include_lower\" : false,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"categorykey\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttypekey\" : [ \"1001\" ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );

        ContentIndexQuery query = createContentQuery( "key > 100", categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_category_contenttype_filters_with_query_and_count()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 20,\n" +
            "  \"query\" : {\n" +
            "    \"range\" : {\n" +
            "      \"key.number\" : {\n" +
            "        \"from\" : 100.0,\n" +
            "        \"to\" : null,\n" +
            "        \"include_lower\" : false,\n" +
            "        \"include_upper\" : true\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"categorykey\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttypekey\" : [ \"1001\" ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );

        ContentIndexQuery query = createContentQuery( 0, 20, "key > 100", categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

}
