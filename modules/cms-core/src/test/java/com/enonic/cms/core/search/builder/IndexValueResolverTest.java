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
        Object[] result = IndexValueResolver.toValues( new ArrayExpr( new ValueExpr[]{new ValueExpr( "abc" ), new ValueExpr( "efg" )} ) );

        assertEquals( "abc", result[0].toString() );
        assertEquals( "efg", result[1].toString() );
    }

    @Test
    public void testArrayExpressionAsNumber()
    {
        Object[] result = IndexValueResolver.toValues( new ArrayExpr( new ValueExpr[]{new ValueExpr( 1 ), new ValueExpr( 2 )} ) );

        assertEquals( 2, result.length );

        Number r0 = (Number) result[0];
        Number r1 = (Number) result[1];

        assertEquals( new Double( 1 ), r0.doubleValue() );
        assertEquals( new Double( 2 ), r1.doubleValue() );
    }

    @Test
    public void testValueExpressionAsNumber()
    {
        Object[] result = IndexValueResolver.toValues( new ValueExpr( 1 ) );

        assertEquals( 1, result.length );

        Number r0 = (Number) result[0];

        assertEquals( new Double( 1 ), r0.doubleValue() );
    }

    @Test
    public void testToValuesString()
    {
        Object result = IndexValueResolver.toValue( new ValueExpr( "abc" ) );
        assertTrue( result instanceof String );

        assertEquals( result.toString(), "abc" );
    }

    @Test
    public void testToValuesNumber()
    {
        Object result = IndexValueResolver.toValue( new ValueExpr( 123 ) );
        assertTrue( result instanceof Number );

        assertEquals( new Double( 123 ), ( (Number) result ).doubleValue() );

    }

    @Test
    public void testExpressionToValue()
    {
        Object result = IndexValueResolver.toValue( new ValueExpr( "123" ) );
        assertTrue( result instanceof String );

        result = IndexValueResolver.toValue( new ValueExpr( 123 ) );
        assertEquals( new Float( 123 ), result );

        result = IndexValueResolver.toValue( new ValueExpr( new Double( "123.0" ) ) );
        assertTrue( result instanceof Double );

        result = IndexValueResolver.toValue( new ValueExpr( new Float( "123.0" ) ) );
        assertTrue( result instanceof Float );

        result = IndexValueResolver.toValue( new ValueExpr( new Long( "123" ) ) );
        assertTrue( result instanceof Long );
    }


}
