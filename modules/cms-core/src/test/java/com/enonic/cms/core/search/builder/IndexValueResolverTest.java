package com.enonic.cms.core.search.builder;

import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/23/11
 * Time: 12:27 PM
 */
public class IndexValueResolverTest
{


    @Test
    public void testArrayExpressionAsString()
    {
        Object[] result =
            ExpressionValueResolver.toValues( new ArrayExpr( new ValueExpr[]{new ValueExpr( "abc" ), new ValueExpr( "efg" )} ) );

        assertEquals( "abc", result[0].toString() );
        assertEquals( "efg", result[1].toString() );
    }

    @Test
    public void testArrayExpressionAsNumber()
    {
        Object[] result = ExpressionValueResolver.toValues( new ArrayExpr( new ValueExpr[]{new ValueExpr( 1 ), new ValueExpr( 2 )} ) );

        assertEquals( 2, result.length );

        Number r0 = (Number) result[0];
        Number r1 = (Number) result[1];

        assertEquals( new Double( 1 ), r0.doubleValue() );
        assertEquals( new Double( 2 ), r1.doubleValue() );
    }

    @Test
    public void testValueExpressionAsNumber()
    {
        Object[] result = ExpressionValueResolver.toValues( new ValueExpr( 1 ) );

        assertEquals( 1, result.length );

        Number r0 = (Number) result[0];

        assertEquals( new Double( 1 ), r0.doubleValue() );
    }

    @Test
    public void testToValuesString()
    {
        Object result = ExpressionValueResolver.toValue( new ValueExpr( "abc" ) );
        assertTrue( result instanceof String );

        assertEquals( result.toString(), "abc" );
    }

    @Test
    public void testToValuesNumber()
    {
        Object result = ExpressionValueResolver.toValue( new ValueExpr( 123 ) );
        assertTrue( result instanceof Number );

        assertEquals( new Double( 123 ), ( (Number) result ).doubleValue() );

    }

    @Test
    public void testExpressionToValue()
    {
        Object result = ExpressionValueResolver.toValue( new ValueExpr( "123" ) );
        assertTrue( result instanceof String );

        result = ExpressionValueResolver.toValue( new ValueExpr( 123 ) );
        assertEquals( new Float( 123 ), result );

        result = ExpressionValueResolver.toValue( new ValueExpr( new Double( "123.0" ) ) );
        assertTrue( result instanceof Double );

        result = ExpressionValueResolver.toValue( new ValueExpr( new Float( "123.0" ) ) );
        assertTrue( result instanceof Float );

        result = ExpressionValueResolver.toValue( new ValueExpr( new Long( "123" ) ) );
        assertTrue( result instanceof Long );
    }


}
