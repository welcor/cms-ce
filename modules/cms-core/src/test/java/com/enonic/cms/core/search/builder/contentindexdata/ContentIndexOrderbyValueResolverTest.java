/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder.contentindexdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.cms.core.search.builder.ContentIndexOrderbyValueResolver;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class ContentIndexOrderbyValueResolverTest
{

    @Test
    public void testOrderByValueBasics()
    {
        String result = ContentIndexOrderbyValueResolver.getOrderbyValue( null );
        assertNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderbyValue( 123 );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderbyValue( new Double( 123 ) );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getOrderbyValue( new Float( 123 ) );
        assertNotNull( result );
    }

    @Test
    public void testOrderByValueSortingWithLongsOnly()
    {
        List<String> resultList = new ArrayList<String>();

        String minusTwo = ContentIndexOrderbyValueResolver.getOrderbyValue( -2L );
        String two = ContentIndexOrderbyValueResolver.getOrderbyValue( 2L );
        String hundredAndOne = ContentIndexOrderbyValueResolver.getOrderbyValue( 101L );

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

        String one = ContentIndexOrderbyValueResolver.getOrderbyValue( 1.0 );
        String one_point_one = ContentIndexOrderbyValueResolver.getOrderbyValue( 1.1 );
        String two = ContentIndexOrderbyValueResolver.getOrderbyValue( 2.0 );
        String twelwe = ContentIndexOrderbyValueResolver.getOrderbyValue( 12.0 );
        String ten = ContentIndexOrderbyValueResolver.getOrderbyValue( new Double( 10.0 ) );
        String hundred = ContentIndexOrderbyValueResolver.getOrderbyValue( 100.0 );

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

        String result = ContentIndexOrderbyValueResolver.getOrderbyValue( "TestSTring" );
        assertNotNull( result );

        assertEquals( "teststring", result );
    }


    @Test
    public void testOrderByValueDates()
    {
        String result = ContentIndexOrderbyValueResolver.getOrderbyValue( new DateTime( 2010, 1, 1, 10, 30, 30, 333 ).toDate() );
        assertNotNull( result );

        System.out.println( result );
    }

    @Test
    public void testOrderbyValueForNumber()
    {
        Set<Object> valueSet = new HashSet<Object>();

        String result = ContentIndexOrderbyValueResolver.getOrderbyValue( 2.0 );
        assertNotNull( result );
    }
}
