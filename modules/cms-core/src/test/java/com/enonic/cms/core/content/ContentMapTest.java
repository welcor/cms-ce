package com.enonic.cms.core.content;

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

public class ContentMapTest
{
    private static final int PERFORMANCE_SIZE = 10000;

    private static final int PERFORMANCE_MAX_EXPECTED_MILLIS = 300;

    private static final Random RANDOM_WHEEL = new SecureRandom();

    private final static ContentKey CONTENT_KEY_1 = new ContentKey( 1 );

    private final static ContentKey CONTENT_KEY_2 = new ContentKey( 2 );

    private final static ContentKey CONTENT_KEY_3 = new ContentKey( 3 );

    @Test
    public void iterator_returns_content_in_order()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENT_KEY_3, CONTENT_KEY_2, CONTENT_KEY_1 );
        ContentMap contentMap = new ContentMap( orderMask );
        contentMap.add( createContent( CONTENT_KEY_3 ) );
        contentMap.add( createContent( CONTENT_KEY_2 ) );
        contentMap.add( createContent( CONTENT_KEY_1 ) );

        // exercise
        Iterator<ContentEntity> it = contentMap.iterator();

        // verify
        assertEquals( createContent( CONTENT_KEY_3 ), it.next() );
        assertEquals( createContent( CONTENT_KEY_2 ), it.next() );
        assertEquals( createContent( CONTENT_KEY_1 ), it.next() );
        assertFalse( it.hasNext() );
    }

    @Test(expected = IllegalStateException.class)
    public void add_throws_exception_when_content_added_does_not_exist_in_order_mask()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENT_KEY_1 );
        ContentMap contentMap = new ContentMap( orderMask );

        // exercise
        contentMap.add( createContent( CONTENT_KEY_2 ) );
    }

    @Test(expected = IllegalStateException.class)
    public void addAll_throws_exception_when_content_added_does_not_exist_in_order_mask()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENT_KEY_1 );
        ContentMap contentMap = new ContentMap( orderMask );

        // exercise
        contentMap.addAll( Lists.newArrayList( createContent( CONTENT_KEY_2 ) ) );
    }

    @Test
    public void removeEntriesWithNullValues()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENT_KEY_3, CONTENT_KEY_2, CONTENT_KEY_1 );
        ContentMap contentMap = new ContentMap( orderMask );
        contentMap.add( createContent( CONTENT_KEY_3 ) );
        contentMap.add( createContent( CONTENT_KEY_1 ) );

        // exercise
        contentMap.removeEntriesWithNullValues();

        Iterator<ContentEntity> it = contentMap.iterator();

        // verify
        assertEquals( createContent( CONTENT_KEY_3 ), it.next() );
        assertEquals( createContent( CONTENT_KEY_1 ), it.next() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void add_performance()
    {
        // setup
        List<ContentKey> orderMask = new ArrayList<ContentKey>( PERFORMANCE_SIZE );
        for ( int i = 1; i <= PERFORMANCE_SIZE; i++ )
        {
            orderMask.add( new ContentKey( i ) );
        }

        List<ContentKey> randomOrder = Lists.newArrayList( orderMask );
        Collections.shuffle( randomOrder, RANDOM_WHEEL );

        // exercise
        StopWatch stopWatch = new StopWatch( "Creating a ContentMap and adding " + PERFORMANCE_SIZE + " content to it" );
        stopWatch.start( "creation" );

        ContentMap contentMap = new ContentMap( orderMask );

        stopWatch.stop();

        stopWatch.start( "adding" );
        for ( int i = 0; i < PERFORMANCE_SIZE; i++ )
        {
            contentMap.add( createContent( randomOrder.get( i ) ) );
        }
        stopWatch.stop();
        stopWatch.start( "removeEntriesWithNullValues" );
        contentMap.removeEntriesWithNullValues();
        stopWatch.stop();
        // verify
        System.out.println( stopWatch.prettyPrint() );
        assertTrue( "Expected performance when adding " + PERFORMANCE_SIZE + " content to a ContentMap is breached. " +
                        "Please try run this test again. If this assert is breached continuously then either this computer is slow or the performance of ContentMap have degraded. ",
                    stopWatch.getTotalTimeMillis() < PERFORMANCE_MAX_EXPECTED_MILLIS );
    }

    private ContentEntity createContent( ContentKey contentKey )
    {
        ContentEntity c = new ContentEntity();
        c.setKey( contentKey );
        return c;
    }
}
