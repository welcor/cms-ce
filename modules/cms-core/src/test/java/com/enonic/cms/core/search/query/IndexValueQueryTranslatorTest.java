/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.search.ContentIndexServiceImpl;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/9/12
 * Time: 12:48 PM
 */
public class IndexValueQueryTranslatorTest
    extends QueryTranslatorTestBase
{

    IndexValueQueryTranslator translator = new IndexValueQueryTranslator();


    @Test
    public void testCreateIndexValueQuery()
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + ContentIndexServiceImpl.COUNT_OPTIMIZER_THRESHOULD_VALUE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : { }\n" +
            "  },\n" +
            "  \"sort\" : [ {\n" +
            "    \"title.orderby\" : {\n" +
            "      \"order\" : \"asc\",\n" +
            "      \"ignore_unmapped\" : true\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        IndexValueQuery query = new IndexValueQuery( "title" );

        final SearchSourceBuilder builder = translator.build( query, new QueryField( "title" ) );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


    @Test
    public void testCustomDataField()
    {
        String expected_search_result = "{\n" +
            "  \"from\" : 0,\n" +
            "  \"size\" : " + ContentIndexServiceImpl.COUNT_OPTIMIZER_THRESHOULD_VALUE + ",\n" +
            "  \"query\" : {\n" +
            "    \"match_all\" : { }\n" +
            "  },\n" +
            "  \"sort\" : [ {\n" +
            "    \"data_test.orderby\" : {\n" +
            "      \"order\" : \"asc\",\n" +
            "      \"ignore_unmapped\" : true\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        IndexValueQuery query = new IndexValueQuery( "data/test" );

        final SearchSourceBuilder builder = translator.build( query, new QueryField( "data_test" ) );

        compareStringsIgnoreFormatting( expected_search_result, builder.toString() );
    }


}
