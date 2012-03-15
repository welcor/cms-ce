package com.enonic.cms.core.search.query;

import java.util.List;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.category.CategoryAccessKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.index.ContentIndexQuery;

public class FilterQueryBuilderTest
    extends QueryTranslatorBaseTest
{
    FilterQueryBuilder filterQueryBuilder = new FilterQueryBuilder();

    @Before
    public void setUp()
    {
    }

    @Test
    public void testContentStatusFilter()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"term\" : {\n" +
            "      \"status_numeric\" : 2\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );
        query.setContentStatusFilter( ContentStatus.APPROVED.getKey() );

        filterQueryBuilder.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }

    @Test
    public void testCategoryAccessFilter_single()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"term\" : {\n" +
            "      \"categoryaccesstype\" : \"ADMIN_BROWSE\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        filterQueryBuilder.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }

    @Test
    public void testCategoryAccessFilter_two_and()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"term\" : {\n" +
            "          \"categoryaccesstype\" : \"ADMIN_BROWSE\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"term\" : {\n" +
            "          \"categoryaccesstype\" : \"APPROVE\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.APPROVE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        filterQueryBuilder.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }

    @Test
    public void testCategoryAccessFilter_two_or()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"should\" : {\n" +
            "        \"term\" : {\n" +
            "          \"categoryaccesstype\" : \"ADMIN_BROWSE\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"should\" : {\n" +
            "        \"term\" : {\n" +
            "          \"categoryaccesstype\" : \"APPROVE\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.APPROVE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.OR );

        filterQueryBuilder.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }
}