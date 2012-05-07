package com.enonic.cms.core.content;


import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class OrderContentKeysByGivenOrderComparatorTest
{
    private final ContentKey CONTENTKEY_1 = new ContentKey( 1 );

    private final ContentKey CONTENTKEY_2 = new ContentKey( 2 );

    private final ContentKey CONTENTKEY_3 = new ContentKey( 3 );

    private final ContentKey CONTENTKEY_4 = new ContentKey( 4 );

    @Test
    public void sort_when_list_to_sort_contains_one_value()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENTKEY_1 );
        OrderContentKeysByGivenOrderComparator comparator = new OrderContentKeysByGivenOrderComparator( orderMask );

        // exercise
        List<ContentKey> sortedList = Lists.newArrayList( CONTENTKEY_1 );
        Collections.sort( sortedList, comparator );

        // verify
        assertEquals( Lists.newArrayList( CONTENTKEY_1 ), sortedList );
    }

    @Test
    public void sort_when_list_to_sort_is_already_sorted()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 );
        OrderContentKeysByGivenOrderComparator comparator = new OrderContentKeysByGivenOrderComparator( orderMask );

        // exercise
        List<ContentKey> sortedList = Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 );
        Collections.sort( sortedList, comparator );

        // verify
        assertEquals( Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 ), sortedList );
    }

    @Test
    public void sort_when_list_to_sort_is_reversed()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 );
        OrderContentKeysByGivenOrderComparator comparator = new OrderContentKeysByGivenOrderComparator( orderMask );

        // exercise
        List<ContentKey> sortedList = Lists.newArrayList( CONTENTKEY_3, CONTENTKEY_2, CONTENTKEY_1 );
        Collections.sort( sortedList, comparator );

        // verify
        assertEquals( Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 ), sortedList );
    }

    @Test
    public void sort_when_list_to_sort_does_not_contain_all_values_in_order_mask()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENTKEY_2, CONTENTKEY_1, CONTENTKEY_3, CONTENTKEY_4 );
        OrderContentKeysByGivenOrderComparator comparator = new OrderContentKeysByGivenOrderComparator( orderMask );

        // exercise
        List<ContentKey> sortedList = Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 );
        Collections.sort( sortedList, comparator );

        // verify
        assertEquals( Lists.newArrayList( CONTENTKEY_2, CONTENTKEY_1, CONTENTKEY_3 ), sortedList );
    }

    @Test
    public void sort_when_orderMask_does_not_contain_all_values_in_list_to_sort()
    {
        // setup
        List<ContentKey> orderMask = Lists.newArrayList( CONTENTKEY_3, CONTENTKEY_2 );
        OrderContentKeysByGivenOrderComparator comparator = new OrderContentKeysByGivenOrderComparator( orderMask );

        // exercise
        List<ContentKey> sortedList = Lists.newArrayList( CONTENTKEY_1, CONTENTKEY_2, CONTENTKEY_3 );
        Collections.sort( sortedList, comparator );

        // verify
        assertEquals( Lists.newArrayList( CONTENTKEY_3, CONTENTKEY_2, CONTENTKEY_1 ), sortedList );
    }
}
