package com.enonic.cms.core.search.query;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Created by IntelliJ IDEA.
 * User: udu
 * Date: 11/29/11
 * Time: 1:32 PM
 */
public class QueryTranslatorTest_filters
    extends QueryTranslatorBaseTest
{
    @Test
    public void testLogicalQuery_category_and_contenttype_filters()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"category_key_numeric\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttype_key_numeric\" : [ \"1002\", \"1001\" ]\n" +
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
            "      \"key_numeric\" : {\n" +
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
            "          \"category_key_numeric\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttype_key_numeric\" : [ \"1001\" ]\n" +
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
            "      \"key_numeric\" : {\n" +
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
            "          \"category_key_numeric\" : [ \"15\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"contenttype_key_numeric\" : [ \"1001\" ]\n" +
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

    @Test
    public void testFilterQuery_section_filter()
        throws Exception
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : {\n" +
            "    }\n" +
            "  },\n" +
            "  \"filter\" : {\n" +
            "    \"terms\" : {\n" +
            "      \"contentlocations.menuitemkey_numeric\" : [ \"22\" ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

        Set<MenuItemEntity> sectionFilter = new HashSet<MenuItemEntity>();
        MenuItemEntity entity = new MenuItemEntity();
        entity.setKey( 22 );
        sectionFilter.add( entity );

        ContentIndexQuery query = createContentQuery( sectionFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


    @Test
    public void testFilterQuery_content_filter()
        throws Exception
    {
        Set<ContentKey> contentKeys = new HashSet<ContentKey>();
        contentKeys.add( new ContentKey( "1" ) );
        contentKeys.add( new ContentKey( "2" ) );
        contentKeys.add( new ContentKey( "3" ) );

        ContentIndexQuery query = createContentQueryContentFilter( contentKeys );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        System.out.println( builder.toString() );

    }

}
