package com.enonic.cms.core.search.query;

import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.FieldExpr;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/1/11
 * Time: 2:39 PM
 */
public class QueryFieldNameResolverTest
{
    @Test
    public void testNormalizeFieldName()
    {
        String normalized = QueryFieldNameResolver.resolveQueryFieldName( "category/key" );
        assertEquals( "category_key", normalized );

        normalized = QueryFieldNameResolver.resolveQueryFieldName( "category/@key" );
        assertEquals( "category_key", normalized );

        normalized = QueryFieldNameResolver.resolveQueryFieldName( "category/name/key" );
        assertEquals( "category_name_key", normalized );
    }

    @Test
    public void testSectionKeyQueryFieldName()
    {
        String fieldName = QueryFieldNameResolver.getSectionKeyQueryFieldName();
        assertEquals( "contentlocations.menuitemkey_numeric", fieldName );
    }

    @Test
    public void testOrderByQueryFieldNames()
    {
        String orderbyField = QueryFieldNameResolver.getOrderByFieldName( "title" );
        assertEquals( "orderby_title", orderbyField );

        orderbyField = QueryFieldNameResolver.getOrderByFieldName( "data/title" );
        assertEquals( "orderby_data_title", orderbyField );
    }

    @Test
    public void testCustomDataQueryFieldNames()
    {
        String fieldName = QueryFieldNameResolver.resolveQueryFieldName( "customdata/date" );
        assertEquals( "data_date", fieldName );

        fieldName = QueryFieldNameResolver.resolveQueryFieldName( "customdata/customdata/date" );
        assertEquals( "data_customdata_date", fieldName );
    }

    @Test
    public void testQueryFieldNameFromExpression()
    {
        FieldExpr expr = new FieldExpr( "category/key" );

        String normalized = QueryFieldNameResolver.resolveQueryFieldName( expr );
        assertEquals( "category_key", normalized );
    }

}
