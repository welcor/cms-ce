package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class QueryTranslatorTest_attachments
    extends QueryTranslatorBaseTest
{

    @Test
    public void testAttachmentQuery()
        throws Exception
    {
        String expected_search_result = "";

        ContentIndexQuery query = createContentQuery( "attachment/* = 'test'" );

        SearchSourceBuilder builder = getQueryTranslator().build( query );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );

    }


}
