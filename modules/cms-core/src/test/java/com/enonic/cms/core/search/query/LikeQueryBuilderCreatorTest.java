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
            "    \"_all\" : {\n" +
            "      \"wildcard\" : \"test\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        QueryPath queryPath = QueryPathResolver.resolveQueryPath( "*" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryPath, new QueryValue( "test" ) );

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

        QueryPath queryPath = QueryPathResolver.resolveQueryPath( "attachment_title" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryPath, new QueryValue( "test" ) );

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

        QueryPath queryPath = QueryPathResolver.resolveQueryPath( "data_title" );

        final QueryBuilder queryBuilder = likeQueryBuilderFactory.buildLikeQuery( queryPath, new QueryValue( "test" ) );

        System.out.println( queryBuilder.toString() );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

}
