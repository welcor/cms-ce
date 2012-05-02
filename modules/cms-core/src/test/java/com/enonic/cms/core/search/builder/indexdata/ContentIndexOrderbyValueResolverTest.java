package com.enonic.cms.core.search.builder.indexdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class ContentIndexOrderbyValueResolverTest
{

    @Test
    public void testOrderByValueBasics()
    {
        String result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( null );
        assertNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 123 );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( new Double( 123 ) );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( new Float( 123 ) );
        assertNotNull( result );
    }

    @Test
    public void testOrderByValueSortingWithLongsOnly()
    {
        List<String> resultList = new ArrayList<String>();

        String minusTwo = ContentIndexOrderbyValueResolver.getOrderValueForNumber( -2L );
        String two = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 2L );
        String hundredAndOne = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 101L );

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

        String one = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 1.0 );
        String one_point_one = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 1.1 );
        String two = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 2.0 );
        String twelwe = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 12.0 );
        String ten = ContentIndexOrderbyValueResolver.getOrderValueForNumber( new Double( 10.0 ) );
        String hundred = ContentIndexOrderbyValueResolver.getOrderValueForNumber( 100.0 );

        resultList.addAll( Arrays.asList( new String[]{one_point_one, two, one, ten, hundred, twelwe} ) );

        Collections.sort( resultList );

        assertEquals( "1", one, resultList.get( 0 ) );
        assertEquals( "1.1", one_point_one, resultList.get( 1 ) );
        assertEquals( "2", two, resultList.get( 2 ) );
        assertEquals( "10", ten, resultList.get( 3 ) );
        assertEquals( "12", twelwe, resultList.get( 4 ) );
        assertEquals( "100", hundred, resultList.get( 5 ) );
    }


    @Test
    public void testOrderbyString()
    {
        String result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( null );
        assertNull( result );

        Set<Object> valueSet = new HashSet<Object>();
        valueSet.add( "testValue" );

        result = ContentIndexOrderbyValueResolver.resolveOrderbyValue( valueSet );
        assertNotNull( result );

        assertEquals( "testvalue", result );
    }


    @Test
    public void testOrderByValueDates()
    {
        String result = ContentIndexOrderbyValueResolver.getOrderValueForNumber( null );
        assertNull( result );

        Set<Object> valueSet = new HashSet<Object>();
        valueSet.add( new DateTime( 2010, 1, 1, 10, 0 ) );

        result = ContentIndexOrderbyValueResolver.resolveOrderbyValue( valueSet );
        assertNotNull( result );

    }

    @Test
    public void testOrderbyValueForNumberSet()
    {
        Set<Object> valueSet = new HashSet<Object>();
        valueSet.add( 3.0 );
        valueSet.add( 2.0 );
        valueSet.add( 1.0 );

        String result = ContentIndexOrderbyValueResolver.resolveOrderbyValue( valueSet );
        assertNotNull( result );
    }
}
