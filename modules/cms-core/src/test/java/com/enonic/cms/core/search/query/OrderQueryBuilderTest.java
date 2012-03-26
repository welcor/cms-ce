package com.enonic.cms.core.search.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderByExpr;
import com.enonic.cms.core.content.index.queryexpression.OrderFieldExpr;

public class OrderQueryBuilderTest
    extends QueryTranslatorBaseTest
{

    @Test
    public void testStuff()
    {
        String expected_result =
            "{\r\n" +
                "  \"sort\" : [ {\r\n" +
                "    \"orderby_key\" : {\r\n" +
                "      \"order\" : \"desc\",\r\n" +
                "      \"ignore_unmapped\" : true\r\n" +
                "    }\r\n" +
                "  } ]\r\n" + "}";

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        FieldExpr field = new FieldExpr( "key" );
        OrderFieldExpr orderField = new OrderFieldExpr( field, true );
        OrderByExpr expr = new OrderByExpr( new OrderFieldExpr[]{orderField} );

        OrderQueryBuilderFactory orderQueryBuilderFactory = new OrderQueryBuilderFactory();
        orderQueryBuilderFactory.buildOrderByExpr( sourceBuilder, expr );

//        assertEquals( "{\n" + "  \"sort\" : [ {\n" + "    \"key_numeric\" : {\n" + "      \"order\" : \"desc\"\n" +
//                              "    }\n" + "  } ]\n" + "}", sourceBuilder.toString() );

        compareStringsIgnoreFormatting( expected_result, sourceBuilder.toString() );
    }
}
