package com.enonic.cms.core.search.query;

import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class QueryValueResolverTest
{
    @Test
    public void testToValue_string()
    {
        Expression expression = new ValueExpr( "100.0" );

        assertEquals( "100.0", QueryValueResolver.toValues( expression )[0] );
    }

    @Test
    public void testToValue_int()
    {
        Expression expression = new ValueExpr( 100.0 );

        assertEquals( 100.0, QueryValueResolver.toValues( expression )[0] );
    }

    @Test
    public void testToValues_else()
    {
        Expression expression = new FieldExpr( "" );

        assertNotNull( QueryValueResolver.toValues( expression ) );

        assertEquals( 0, QueryValueResolver.toValues( expression ).length );
    }
}
