package com.enonic.cms.core.content.category;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.springframework.util.StopWatch;

import com.google.common.collect.Lists;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CategoryMapTest
{
    private static final int PERFORMANCE_SIZE = 10000;

    private static final int PERFORMANCE_MAX_EXPECTED_MILLIS = 300;

    private static final Random RANDOM_WHEEL = new SecureRandom();

    private final static CategoryKey CATEGORY_KEY_1 = new CategoryKey( 1 );

    private final static CategoryKey CATEGORY_KEY_2 = new CategoryKey( 2 );

    private final static CategoryKey CATEGORY_KEY_3 = new CategoryKey( 3 );

    @Test
    public void iterator_returns_category_in_order()
    {
        // setup
        List<CategoryKey> orderMask = Lists.newArrayList( CATEGORY_KEY_3, CATEGORY_KEY_2, CATEGORY_KEY_1 );
        CategoryMap categoryMap = new CategoryMap( orderMask );
        categoryMap.add( createCategory( CATEGORY_KEY_3 ) );
        categoryMap.add( createCategory( CATEGORY_KEY_2 ) );
        categoryMap.add( createCategory( CATEGORY_KEY_1 ) );

        // exercise
        Iterator<CategoryEntity> it = categoryMap.iterator();

        // verify
        assertEquals( createCategory( CATEGORY_KEY_3 ), it.next() );
        assertEquals( createCategory( CATEGORY_KEY_2 ), it.next() );
        assertEquals( createCategory( CATEGORY_KEY_1 ), it.next() );
        assertFalse( it.hasNext() );
    }

    @Test(expected = IllegalStateException.class)
    public void add_throws_exception_when_category_added_does_not_exist_in_order_mask()
    {
        // setup
        List<CategoryKey> orderMask = Lists.newArrayList( CATEGORY_KEY_1 );
        CategoryMap categoryMap = new CategoryMap( orderMask );

        // exercise
        categoryMap.add( createCategory( CATEGORY_KEY_2 ) );
    }

    @Test(expected = IllegalStateException.class)
    public void addAll_throws_exception_when_category_added_does_not_exist_in_order_mask()
    {
        // setup
        List<CategoryKey> orderMask = Lists.newArrayList( CATEGORY_KEY_1 );
        CategoryMap categoryMap = new CategoryMap( orderMask );

        // exercise
        categoryMap.addAll( Lists.newArrayList( createCategory( CATEGORY_KEY_2 ) ) );
    }

    @Test
    public void removeEntriesWithNullValues()
    {
        // setup
        List<CategoryKey> orderMask = Lists.newArrayList( CATEGORY_KEY_3, CATEGORY_KEY_2, CATEGORY_KEY_1 );
        CategoryMap categoryMap = new CategoryMap( orderMask );
        categoryMap.add( createCategory( CATEGORY_KEY_3 ) );
        categoryMap.add( createCategory( CATEGORY_KEY_1 ) );

        // exercise
        categoryMap.removeEntriesWithNullValues();

        Iterator<CategoryEntity> it = categoryMap.iterator();

        // verify
        assertEquals( createCategory( CATEGORY_KEY_3 ), it.next() );
        assertEquals( createCategory( CATEGORY_KEY_1 ), it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void add_performance()
    {
        // setup
        List<CategoryKey> orderMask = new ArrayList<CategoryKey>( PERFORMANCE_SIZE );
        for ( int i = 1; i <= PERFORMANCE_SIZE; i++ )
        {
            orderMask.add( new CategoryKey( i ) );
        }

        List<CategoryKey> randomOrder = Lists.newArrayList( orderMask );
        Collections.shuffle( randomOrder, RANDOM_WHEEL );

        // exercise
        StopWatch stopWatch = new StopWatch( "Creating a CategoryMap and adding " + PERFORMANCE_SIZE + " categories to it" );
        stopWatch.start( "creation" );

        CategoryMap categoryMap = new CategoryMap( orderMask );

        stopWatch.stop();

        stopWatch.start( "adding" );
        for ( int i = 0; i < PERFORMANCE_SIZE; i++ )
        {
            categoryMap.add( createCategory( randomOrder.get( i ) ) );
        }
        stopWatch.stop();
        stopWatch.start( "removeEntriesWithNullValues" );
        categoryMap.removeEntriesWithNullValues();
        stopWatch.stop();
        // verify
        System.out.println( stopWatch.prettyPrint() );
        assertTrue( "Expected performance when adding " + PERFORMANCE_SIZE + " categories to a CategoryMap is breached. " +
                        "Please try run this test again. If this assert is breached continuously then either this computer is slow or the performance of CategoryMap have degraded. ",
                    stopWatch.getTotalTimeMillis() < PERFORMANCE_MAX_EXPECTED_MILLIS );
    }

    private CategoryEntity createCategory( CategoryKey categoryKey )
    {
        CategoryEntity c = new CategoryEntity();
        c.setKey( categoryKey );
        return c;
    }
}
