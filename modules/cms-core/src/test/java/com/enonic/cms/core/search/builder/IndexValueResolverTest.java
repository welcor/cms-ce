package com.enonic.cms.core.search.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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
    public void testOrderByValueBasics()
    {
        String result = IndexValueResolver.getOrderValueForNumber( null );
        assertNull( result );

        result = IndexValueResolver.getOrderValueForNumber( 123 );
        assertNotNull( result );

        result = IndexValueResolver.getOrderValueForNumber( new Double( 123 ) );
        assertNotNull( result );

        result = IndexValueResolver.getOrderValueForNumber( new Float( 123 ) );
        assertNotNull( result );
    }

    @Ignore
    @Test
    public void testOrderByValueSortingWithLongAndDouble()
    {
        List<String> resultList = new ArrayList<String>();

        String two = IndexValueResolver.getOrderValueForNumber( new Double( 2 ) );
        String hundredAndOne = IndexValueResolver.getOrderValueForNumber( 101L );

        resultList.addAll( Arrays.asList( new String[]{two, hundredAndOne} ) );

        Collections.sort( resultList );

        assertEquals( "2", two, resultList.get( 0 ) );
        assertEquals( "101", hundredAndOne, resultList.get( 1 ) );
    }

    @Test
    public void testOrderByValueSortingWithLongsOnly()
    {
        List<String> resultList = new ArrayList<String>();

        String minusTwo = IndexValueResolver.getOrderValueForNumber( -2L );
        String two = IndexValueResolver.getOrderValueForNumber( 2L );
        String hundredAndOne = IndexValueResolver.getOrderValueForNumber( 101L );

        resultList.addAll( Arrays.asList( new String[]{two, minusTwo, hundredAndOne} ) );

        Collections.sort( resultList );

        assertEquals( "-2", minusTwo, resultList.get( 0 ) );
        assertEquals( "2", two, resultList.get( 1 ) );
        assertEquals( "101", hundredAndOne, resultList.get( 2 ) );
    }

    @Test
    public void testOrderByValueSorting()
    {
        List<String> resultList = new ArrayList<String>();

        String one = IndexValueResolver.getOrderValueForNumber( 1.0 );
        String one_point_one = IndexValueResolver.getOrderValueForNumber( 1.1 );
        String two = IndexValueResolver.getOrderValueForNumber( 2.0 );
        String twelwe = IndexValueResolver.getOrderValueForNumber( 12.0 );
        String ten = IndexValueResolver.getOrderValueForNumber( new Double( 10.0 ) );
        String hundred = IndexValueResolver.getOrderValueForNumber( 100.0 );

        resultList.addAll( Arrays.asList( new String[]{one_point_one, two, one, ten, hundred, twelwe} ) );

        Collections.sort( resultList );

        assertEquals( "1", one, resultList.get( 0 ) );
        assertEquals( "1.0", one_point_one, resultList.get( 1 ) );
        assertEquals( "2", two, resultList.get( 2 ) );
        assertEquals( "10", ten, resultList.get( 3 ) );
        assertEquals( "12", twelwe, resultList.get( 4 ) );
        assertEquals( "100", hundred, resultList.get( 5 ) );
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
