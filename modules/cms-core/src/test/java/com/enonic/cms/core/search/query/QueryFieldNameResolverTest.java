package com.enonic.cms.core.search.query;

import org.junit.Test;

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
    public void testStuff()
    {
        String normalized = QueryFieldNameResolver.normalizeFieldName( "category/@key" );
        assertEquals( "category_key", normalized );

        String orderbyField = QueryFieldNameResolver.getOrderByFieldName( "title" );
        assertEquals( "orderby_title", orderbyField );

        String fieldName = QueryFieldNameResolver.getSectionKeyNumericFieldName();
        assertEquals( "contentlocations.menuitemkey_numeric", fieldName );

    }
}
