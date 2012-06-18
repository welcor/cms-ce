package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.Collection;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.search.query.factory.FilterQueryBuilderFactory;
import com.enonic.cms.core.security.group.GroupKey;

public class QueryTranslator_filterCategoryAccessFilterTest
    extends QueryTranslatorTestBase
{
    FilterQueryBuilderFactory filterQueryBuilderFactory = new FilterQueryBuilderFactory();

    @Test
    public void testCategoryAccessFilter_single()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"access_read\" : [ \"group_a\", \"group_b\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"term\" : {\n" +
            "          \"access_category_browse\" : [ \"group_a\", \"group_b\" ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        Collection<GroupKey> securityFilter = getSecurityFilter();
        query.setSecurityFilter( securityFilter );

        filterQueryBuilderFactory.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }

    @Test
    public void testCategoryAccessFilter_two_and()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"access_read\" : [ \"group_a\", \"group_b\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"term\" : {\n" +
            "              \"access_category_browse\" : [ \"group_a\", \"group_b\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"term\" : {\n" +
            "              \"access_category_approve\" : [ \"group_a\", \"group_b\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.APPROVE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );

        Collection<GroupKey> securityFilter = getSecurityFilter();
        query.setSecurityFilter( securityFilter );

        filterQueryBuilderFactory.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }

    @Test
    public void testCategoryAccessFilter_two_or()
    {
        String expected = "{\n" +
            "  \"filter\" : {\n" +
            "    \"bool\" : {\n" +
            "      \"must\" : {\n" +
            "        \"terms\" : {\n" +
            "          \"access_read\" : [ \"group_a\", \"group_b\" ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"must\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"should\" : {\n" +
            "            \"term\" : {\n" +
            "              \"access_category_browse\" : [ \"group_a\", \"group_b\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"should\" : {\n" +
            "            \"term\" : {\n" +
            "              \"access_category_approve\" : [ \"group_a\", \"group_b\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        SearchSourceBuilder builder = new SearchSourceBuilder();

        ContentIndexQuery query = new ContentIndexQuery( "" );

        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.APPROVE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.OR );

        Collection<GroupKey> securityFilter = getSecurityFilter();
        query.setSecurityFilter( securityFilter );

        filterQueryBuilderFactory.buildFilterQuery( builder, query );

        compareStringsIgnoreFormatting( expected, builder.toString() );
    }


    private Collection<GroupKey> getSecurityFilter()
    {
        Collection<GroupKey> securityFilter = new ArrayList<GroupKey>();
        GroupKey groupA = new GroupKey( "group_A" );
        securityFilter.add( groupA );
        GroupKey groupB = new GroupKey( "group_B" );
        securityFilter.add( groupB );
        return securityFilter;
    }

}