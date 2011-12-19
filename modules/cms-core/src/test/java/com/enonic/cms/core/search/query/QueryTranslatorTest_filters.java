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

import static junit.framework.Assert.assertEquals;

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
    public void testLogicalQuery_category_contenttype_filters()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"match_all\" : {\r\n" + "    }\r\n" + "  },\r\n" + "  \"filter\" : {\r\n" + "    \"bool\" : {\r\n" +
                "      \"must\" : {\r\n" + "        \"terms\" : {\r\n" + "          \"category_key_numeric\" : [ \"15\" ]\r\n" +
                "        }\r\n" + "      },\r\n" + "      \"must\" : {\r\n" + "        \"terms\" : {\r\n" +
                "          \"contenttype_key_numeric\" : [ \"1001\" ]\r\n" + "        }\r\n" + "      }\r\n" + "    }\r\n" + "  }\r\n}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );

        ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_category_contenttype_filters_with_query()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"range\" : {\r\n" + "      \"key_numeric\" : {\r\n" + "        \"from\" : 100.0,\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" +
                "  },\r\n" + "  \"filter\" : {\r\n" + "    \"bool\" : {\r\n" + "      \"must\" : {\r\n" + "        \"terms\" : {\r\n" +
                "          \"category_key_numeric\" : [ \"15\" ]\r\n" + "        }\r\n" + "      },\r\n" + "      \"must\" : {\r\n" +
                "        \"terms\" : {\r\n" + "          \"contenttype_key_numeric\" : [ \"1001\" ]\r\n" + "        }\r\n" + "      }\r\n" +
                "    }\r\n" + "  }\r\n}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );

        ContentIndexQuery query = createContentQuery( "key > 100", categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testLogicalQuery_category_contenttype_filters_with_query_and_count()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : 20,\r\n" + "  \"query\" : {\r\n" + "    \"range\" : {\r\n" +
                "      \"key_numeric\" : {\r\n" + "        \"from\" : 100.0,\r\n" + "        \"to\" : null,\r\n" +
                "        \"include_lower\" : false,\r\n" + "        \"include_upper\" : true\r\n" + "      }\r\n" + "    }\r\n" +
                "  },\r\n" + "  \"filter\" : {\r\n" + "    \"bool\" : {\r\n" + "      \"must\" : {\r\n" + "        \"terms\" : {\r\n" +
                "          \"category_key_numeric\" : [ \"15\" ]\r\n" + "        }\r\n" + "      },\r\n" + "      \"must\" : {\r\n" +
                "        \"terms\" : {\r\n" + "          \"contenttype_key_numeric\" : [ \"1001\" ]\r\n" + "        }\r\n" + "      }\r\n" +
                "    }\r\n" + "  }\r\n}";

        Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 15 ) );

        Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1001" ) );

        ContentIndexQuery query = createContentQuery( 0, 20, "key > 100", categoryFilter, contentTypeFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
    }

    @Test
    public void testFilterQuery_section_filter()
        throws Exception
    {
        String expected_search_result =
            "{\r\n" + "  \"from\" : 0,\r\n" + "  \"size\" : " + QUERY_DEFAULT_SIZE + ",\r\n" + "  \"query\" : {\r\n" +
                "    \"match_all\" : {\r\n" + "    }\r\n" + "  },\r\n" + "  \"filter\" : {\r\n" + "    \"terms\" : {\r\n" +
                "      \"contentlocations.menuitemkey_numeric\" : [ \"22\" ]\r\n" + "    }\r\n" + "  }\r\n" + "}";

        Set<MenuItemEntity> sectionFilter = new HashSet<MenuItemEntity>();
        MenuItemEntity entity = new MenuItemEntity();
        entity.setKey( 22 );
        sectionFilter.add( entity );

        ContentIndexQuery query = createContentQuery( sectionFilter );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        assertEquals( expected_search_result, builder.toString() );
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
