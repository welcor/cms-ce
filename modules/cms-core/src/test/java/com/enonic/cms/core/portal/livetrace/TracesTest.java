/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TracesTest
{
    private DateTime currentTime;

    @Before
    public void before()
    {
        currentTime = new DateTime( 2012, 6, 5, 13, 0, 0 );
    }

    @Test
    public void given_three_traces_then_getTotalPeriodInMilliseconds_has_included_all_three()
    {
        Traces<WindowRenderingTrace> traces = Traces.create();
        traces.add( createWindowRenderingTrace( "1:1", 5 ) );
        traces.add( createWindowRenderingTrace( "1:2", 15 ) );
        traces.add( createWindowRenderingTrace( "1:3", 10 ) );

        assertEquals( 30, traces.getTotalPeriodInMilliseconds() );
        assertEquals( "30 ms", traces.getTotalPeriodInHRFormat() );
    }

    private WindowRenderingTrace createWindowRenderingTrace( String windowKey, int periodInMillis )
    {
        WindowRenderingTrace trace = new WindowRenderingTrace( windowKey );
        trace.setStartTime( currentTime );
        increaseCurrentTime( periodInMillis );
        trace.setStopTime( currentTime );
        return trace;
    }

    private void increaseCurrentTime( int millis )
    {
        currentTime = currentTime.plusMillis( millis );
    }
}
