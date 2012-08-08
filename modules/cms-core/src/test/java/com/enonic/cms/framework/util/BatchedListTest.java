/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Apr 16, 2009
 */
public class BatchedListTest
{

    @Test
    public void testGetNextBatch()
    {
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );

        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList<Integer>( sourceList, 3 );
        assertArrayEquals( new Integer[]{1, 2, 3}, list.getNextBatch().toArray() );
        assertArrayEquals( new Integer[]{4, 5}, list.getNextBatch().toArray() );
        assertNull( list.getNextBatch() );
    }

    @Test
    public void testGetNextBatchWithEmptySourceList()
    {
        List<Integer> sourceList = new ArrayList<Integer>();

        BatchedList list = new BatchedList<Integer>( sourceList, 3 );
        assertNull( list.getNextBatch() );
    }


    @Test
    public void testGetNextBatchWithSizeLessThanBatchSize()
    {
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList<Integer>( sourceList, 7 );
        assertArrayEquals( new Integer[]{1, 2, 3, 4, 5}, list.getNextBatch().toArray() );
        assertNull( list.getNextBatch() );
    }


    @Test
    public void testHasMoreBatches()
    {
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList<Integer>( sourceList, 3 );

        // two batches should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the first batch
        list.getNextBatch();

        // one batch should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the last batch
        list.getNextBatch();

        // no more batches should be available
        assertFalse( list.hasMoreBatches() );
    }

    @Test
    public void testHasMoreBatchesWithSizeLessThanBatchSize()
    {
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList<Integer>( sourceList, 10 );

        // one batch should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the one and only batch
        assertNotNull( list.getNextBatch() );

        // more more batches available
        assertFalse( list.hasMoreBatches() );
    }

    @Test
    public void testHasMoreBatchesWithSizeEqualToBatchSize()
    {
        List<Integer> sourceList = new ArrayList<Integer>();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList<Integer>( sourceList, 5 );

        // one batch should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the one and only batch
        assertNotNull( list.getNextBatch() );

        // no more batches available
        assertFalse( list.hasMoreBatches() );
        assertNull( list.getNextBatch() );
    }

    @Test
    public void testhasMoreBatchesWithEmptySourceList()
    {
        List<Integer> sourceList = new ArrayList<Integer>();

        BatchedList list = new BatchedList<Integer>( sourceList, 3 );
        assertFalse( list.hasMoreBatches() );
        assertNull( list.getNextBatch() );
    }
}
