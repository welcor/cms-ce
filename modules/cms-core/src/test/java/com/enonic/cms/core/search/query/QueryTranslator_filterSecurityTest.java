/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.security.group.GroupKey;


public class QueryTranslator_filterSecurityTest
    extends QueryTranslatorTestBase
{

    @Test
    public void testFilterQuery_security_filter_one_group()
        throws Exception
    {
        final String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + ContentIndexQuery.DEFAULT_COUNT + ",\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"match_all\" : { }\n" +
            "      },\n" +
            "      \"filter\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"categorykey\" : [ \"42\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"contenttypekey\" : [ \"1234\", \"1235\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"access_read\" : [ \"group1\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        final Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 42 ) );

        final Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1234" ) );
        contentTypeFilter.add( new ContentTypeKey( "1235" ) );

        ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );
        final Collection<GroupKey> filterGroups = new ArrayList<GroupKey>();
        filterGroups.add( new GroupKey( "group1" ) );
        query.setSecurityFilter( filterGroups );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testFilterQuery_security_filter_two_groups()
        throws Exception
    {
        final String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + ContentIndexQuery.DEFAULT_COUNT + ",\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"match_all\" : { }\n" +
            "      },\n" +
            "      \"filter\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"categorykey\" : [ \"42\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"contenttypekey\" : [ \"1234\", \"1235\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"access_read\" : [ \"group1\", \"group2\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        final Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 42 ) );

        final Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1234" ) );
        contentTypeFilter.add( new ContentTypeKey( "1235" ) );

        ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );
        final Collection<GroupKey> filterGroups = new ArrayList<GroupKey>();
        filterGroups.add( new GroupKey( "group1" ) );
        filterGroups.add( new GroupKey( "group2" ) );
        query.setSecurityFilter( filterGroups );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

    @Test
    public void testFilterQuery_security_filter_not_specified()
        throws Exception
    {
        final String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + ContentIndexQuery.DEFAULT_COUNT + ",\n" +
            "  \"query\" : {\n" +
            "    \"filtered\" : {\n" +
            "      \"query\" : {\n" +
            "        \"match_all\" : { }\n" +
            "      },\n" +
            "      \"filter\" : {\n" +
            "        \"bool\" : {\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"categorykey\" : [ \"42\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"terms\" : {\n" +
            "              \"contenttypekey\" : [ \"1234\", \"1235\" ]\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

        final Set<CategoryKey> categoryFilter = new HashSet<CategoryKey>();
        categoryFilter.add( new CategoryKey( 42 ) );

        final Set<ContentTypeKey> contentTypeFilter = new HashSet<ContentTypeKey>();
        contentTypeFilter.add( new ContentTypeKey( "1234" ) );
        contentTypeFilter.add( new ContentTypeKey( "1235" ) );

        ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );
        final Collection<GroupKey> filterGroups = new ArrayList<GroupKey>();
        query.setSecurityFilter( filterGroups );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }
}
