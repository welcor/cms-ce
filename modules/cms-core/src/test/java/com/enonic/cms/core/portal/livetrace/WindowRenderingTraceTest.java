/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class WindowRenderingTraceTest
{
    @Test
    public void isConcurrencyBlocked_returns_false_when_concurrency_block_timer_not_stopped()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( "1:1" );
        trace.getCacheUsage().startConcurrencyBlockTimer();
        assertFalse( trace.getCacheUsage().isConcurrencyBlocked() );
    }

    @Test
    public void isConcurrencyBlocked_returns_true_when_concurrency_block_was_timed_to_be_larger_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( "1:1" );
        trace.getCacheUsage().startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 50 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.getCacheUsage().stopConcurrencyBlockTimer();
        assertTrue( trace.getCacheUsage().isConcurrencyBlocked() );
    }

    @Test
    public void getConcurrencyBlockingTime_returns_zero_when_concurrency_block_timer_not_stopped()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( "1:1" );
        trace.getCacheUsage().startConcurrencyBlockTimer();
        assertTrue( trace.getCacheUsage().getConcurrencyBlockingTime() == 0 );
    }

    @Test
    public void getConcurrencyBlockingTime_returns_zero_when_concurrency_block_was_timed_to_be_less_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( "1:1" );
        trace.getCacheUsage().startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 1 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.getCacheUsage().stopConcurrencyBlockTimer();
        assertEquals( 0, trace.getCacheUsage().getConcurrencyBlockingTime() );
    }

    @Test
    @Ignore
    public void getConcurrencyBlockingTime_returns_larger_than_zero_when_concurrency_block_was_timed_to_be_larger_than_threshold()
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( "1:1" );
        trace.getCacheUsage().startConcurrencyBlockTimer();
        try
        {
            Thread.sleep( 50 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        trace.getCacheUsage().stopConcurrencyBlockTimer();
        assertTrue( trace.getCacheUsage().getConcurrencyBlockingTime() >= 50 );
    }
}
