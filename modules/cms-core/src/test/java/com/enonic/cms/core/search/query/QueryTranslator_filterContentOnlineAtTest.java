package com.enonic.cms.core.search.query;

import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;


public class QueryTranslator_filterContentOnlineAtTest
    extends QueryTranslatorTestBase
{

    @Test
    public void testFilterQuery_contentOnlineAtFilter()
        throws Exception
    {
        final String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : 200,\n" +
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
            "              \"contenttypekey\" : [ \"1234\" ]\n" +
            "            }\n" +
            "          },\n" +
            "          \"must\" : {\n" +
            "            \"and\" : {\n" +
            "              \"filters\" : [ {\n" +
            "                \"range\" : {\n" +
            "                  \"publishfrom.date\" : {\n" +
            "                    \"from\" : null,\n" +
            "                    \"to\" : \"2011-11-15T16:23:00.000Z\",\n" +
            "                    \"include_lower\" : true,\n" +
            "                    \"include_upper\" : true\n" +
            "                  }\n" +
            "                }\n" +
            "              }, {\n" +
            "                \"or\" : {\n" +
            "                  \"filters\" : [ {\n" +
            "                    \"missing\" : {\n" +
            "                      \"field\" : \"publishto.date\"\n" +
            "                    }\n" +
            "                  }, {\n" +
            "                    \"range\" : {\n" +
            "                      \"publishto.date\" : {\n" +
            "                        \"from\" : \"2011-11-15T16:23:00.000Z\",\n" +
            "                        \"to\" : null,\n" +
            "                        \"include_lower\" : false,\n" +
            "                        \"include_upper\" : true\n" +
            "                      }\n" +
            "                    }\n" +
            "                  } ]\n" +
            "                }\n" +
            "              } ]\n" +
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

        final ContentIndexQuery query = createContentQuery( categoryFilter, contentTypeFilter );

        final DateTime onlineAtDateTime = new DateTime( 2011, 11, 15, 16, 23, 0, 0, DateTimeZone.UTC );
        query.setContentOnlineAtFilter( onlineAtDateTime.toDate() );

        final SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }

}
