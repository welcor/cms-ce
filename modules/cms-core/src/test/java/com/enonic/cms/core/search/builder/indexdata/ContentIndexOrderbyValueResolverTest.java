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
        String result = ContentIndexOrderbyValueResolver.getNumericOrderBy( null );
        assertNull( result );

        result = ContentIndexOrderbyValueResolver.getNumericOrderBy( 123 );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getNumericOrderBy( new Double( 123 ) );
        assertNotNull( result );

        result = ContentIndexOrderbyValueResolver.getNumericOrderBy( new Float( 123 ) );
        assertNotNull( result );
    }

    @Test
    public void testOrderByValueSortingWithLongsOnly()
    {
        List<String> resultList = new ArrayList<String>();

        String minusTwo = ContentIndexOrderbyValueResolver.getNumericOrderBy( -2L );
        String two = ContentIndexOrderbyValueResolver.getNumericOrderBy( 2L );
        String hundredAndOne = ContentIndexOrderbyValueResolver.getNumericOrderBy( 101L );

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

        String one = ContentIndexOrderbyValueResolver.getNumericOrderBy( 1.0 );
        String one_point_one = ContentIndexOrderbyValueResolver.getNumericOrderBy( 1.1 );
        String two = ContentIndexOrderbyValueResolver.getNumericOrderBy( 2.0 );
        String twelwe = ContentIndexOrderbyValueResolver.getNumericOrderBy( 12.0 );
        String ten = ContentIndexOrderbyValueResolver.getNumericOrderBy( new Double( 10.0 ) );
        String hundred = ContentIndexOrderbyValueResolver.getNumericOrderBy( 100.0 );

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

        String result = ContentIndexOrderbyValueResolver.getOrderbyValueForString( "TestSTring" );
        assertNotNull( result );

        assertEquals( "teststring", result );
    }


    @Test
    //TODO: Decide date-format
    public void testOrderByValueDates()
    {

        String result = ContentIndexOrderbyValueResolver.getOrderbyValueForDate( new DateTime( 2010, 1, 1, 10, 0 ).toDate() );
        assertNotNull( result );

    }

    @Test
    public void testOrderbyValueForNumber()
    {
        Set<Object> valueSet = new HashSet<Object>();

        String result = ContentIndexOrderbyValueResolver.getNumericOrderBy( 2.0 );
        assertNotNull( result );
    }
}
