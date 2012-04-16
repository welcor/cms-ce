package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Ignore;
import org.junit.Test;


public class TermQueryBuilderCreatorTest
    extends QueryTranslatorBaseTest
{
    private final TermQueryBuilderFactory termQueryBuilderFactory = new TermQueryBuilderFactory();

    @Test
    public void testIdQueries()
    {
        String expected = "{\n" +
            "  \"ids\" : {\n" +
            "    \"type\" : \"content\",\n" +
            "    \"values\" : [ \"123\" ]\n" +
            "  }\n" +
            "}";

        final QueryBuilder queryBuilder = termQueryBuilderFactory.buildTermQuery( new QueryFieldAndValue( "key", "123" ) );
        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

    @Test
    public void testWildcardQuery()
    {
        String expected = "{\n" +
            "  \"term\" : {\n" +
            "    \"_all_userdata\" : \"123\"\n" +
            "  }\n" +
            "}";

        final QueryBuilder queryBuilder = termQueryBuilderFactory.buildTermQuery( new QueryFieldAndValue( "data/*", "123" ) );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }


    @Test
    public void testWildcardQueryWithNumeric()
    {
        String expected = "{\n" +
            "  \"term\" : {\n" +
            "    \"_all_userdata\" : 123.0\n" +
            "  }\n" +
            "}";

        final QueryBuilder queryBuilder = termQueryBuilderFactory.buildTermQuery( new QueryFieldAndValue( "data/*", 123 ) );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }

    @Ignore //The handling of attachments/* must be checked
    @Test
    public void testWrapInHasChild()
    {
        String expected = "{\n" +
            "  \"has_child\" : {\n" +
            "    \"query\" : {\n" +
            "      \"term\" : {\n" +
            "        \"_all\" : \"123\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"type\" : \"binaries\"\n" +
            "  }\n" +
            "}";

        final QueryBuilder queryBuilder = termQueryBuilderFactory.buildTermQuery( new QueryFieldAndValue( "attachments/*", "123" ) );

        compareStringsIgnoreFormatting( expected, queryBuilder.toString() );
    }


}
