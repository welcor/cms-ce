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
    public void testCustomDataQueryFieldNames()
    {
        String fieldName = QueryFieldNameResolver.resolveQueryFieldName( "contentdata/date" );
        assertEquals( "data_date", fieldName );

        fieldName = QueryFieldNameResolver.resolveQueryFieldName( "contentdata/contentdata/date" );
        assertEquals( "data_contentdata_date", fieldName );
    }

    @Test
    public void testQueryFieldNameFromExpression()
    {
        FieldExpr expr = new FieldExpr( "category/key" );

        String normalized = QueryFieldNameResolver.resolveQueryFieldName( expr );
        assertEquals( "category_key", normalized );
    }

}
