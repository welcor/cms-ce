package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

public class LikeQueryBuilderCreatorTest
    extends QueryTranslatorBaseTest
{
    private final LikeQueryBuilderFactory likeQueryBuilderFactory = new LikeQueryBuilderFactory();

    @Test
    public void testWildCardQuery()
    {
        String expected = "{\n" +
            "  \"wildcard\" : {\n" +
            "    \"_all_userdata\" : {\n" +
            "      \"wildcard\" : \"test\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryField queryField = QueryFieldResolver.resolveQueryField( "data/*" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryField, new QueryValue( "test" ) );

        System.out.println( queryBuilder.toString() );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }


    @Test
    public void testAttachmentAsHasChild()
    {
        String expected = "{\n" +
            "  \"has_child\" : {\n" +
            "    \"query\" : {\n" +
            "      \"wildcard\" : {\n" +
            "        \"attachment_title\" : {\n" +
            "          \"wildcard\" : \"test\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"type\" : \"binaries\"\n" +
            "  }\n" +
            "}";

        QueryField queryField = QueryFieldResolver.resolveQueryField( "attachment_title" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryField, new QueryValue( "test" ) );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

    @Test
    public void testLike()
    {
        String expected = "{\n" +
            "  \"wildcard\" : {\n" +
            "    \"data_title\" : {\n" +
            "      \"wildcard\" : \"test\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryField queryField = QueryFieldResolver.resolveQueryField( "data_title" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryField, new QueryValue( "test" ) );

        System.out.println( queryBuilder.toString() );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

}
