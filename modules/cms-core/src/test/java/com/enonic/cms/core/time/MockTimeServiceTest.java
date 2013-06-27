/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.time;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MockTimeServiceTest
{
    @Test
    public void one_hour()
    {
        DateTimeZone timeZone = DateTimeZone.forID( "Europe/Oslo" );
        DateTime boot = new DateTime( 2012, 1, 28, 0, 0, timeZone );
        DateTime now = new DateTime( 2012, 1, 28, 1, 0, timeZone );

        MockTimeService mockTimeService = new MockTimeService( now );
        mockTimeService.setBootTime( boot );

        Period uptime = mockTimeService.upTime();
        assertEquals( 0, uptime.getMillis() );
        assertEquals( 0, uptime.getSeconds() );
        assertEquals( 1, uptime.getHours() );
    }

    @Test
    public void four_hours_and_four_minutes_and_four_seconds()
    {
        DateTimeZone timeZone = DateTimeZone.forID( "Europe/Oslo" );
        DateTime boot = new DateTime( 2012, 1, 28, 0, 0, timeZone );
        DateTime now = new DateTime( 2012, 1, 28, 4, 4, 4, timeZone );

        MockTimeService mockTimeService = new MockTimeService( now );
        mockTimeService.setBootTime( boot );

        Period uptime = mockTimeService.upTime();
        assertEquals( 0, uptime.getMillis() );
        assertEquals( 4, uptime.getSeconds() );
        assertEquals( 4, uptime.getMinutes() );
        assertEquals( 4, uptime.getHours() );
    }

    @Test
    public void time_in_dst_change_is_also_recorded()
    {
        DateTimeZone timeZone = DateTimeZone.forID( "Europe/Oslo" );
        DateTime beforeDSTChange = new DateTime( 2012, 10, 28, 0, 0, timeZone );
        DateTime afterDSTChange = new DateTime( 2012, 10, 28, 4, 0, timeZone );

        MockTimeService mockTimeService = new MockTimeService( afterDSTChange );
        mockTimeService.setBootTime( beforeDSTChange );

        Period uptime = mockTimeService.upTime();
        assertEquals( 0, uptime.getMillis() );
        assertEquals( 0, uptime.getSeconds() );
        assertEquals( 0, uptime.getMinutes() );
        assertEquals( 5, uptime.getHours() );
    }

    @Test
    public void one_second_and_one_millis_is_rounded_down()
    {
        DateTimeZone timeZone = DateTimeZone.forID( "Europe/Oslo" );
        DateTime boot = new DateTime( 2012, 1, 28, 0, 0, 0, 0, timeZone );
        DateTime now = new DateTime( 2012, 1, 28, 0, 0, 1, 1, timeZone );

        MockTimeService mockTimeService = new MockTimeService( now );
        mockTimeService.setBootTime( boot );

        Period uptime = mockTimeService.upTime();
        assertEquals( 0, uptime.getMillis() );
        assertEquals( 1, uptime.getSeconds() );
    }

    @Test
    public void one_second_and_500_millis_is_rounded_up()
    {
        DateTimeZone timeZone = DateTimeZone.forID( "Europe/Oslo" );
        DateTime boot = new DateTime( 2012, 1, 28, 0, 0, 0, 0, timeZone );
        DateTime now = new DateTime( 2012, 1, 28, 0, 0, 1, 500, timeZone );

        MockTimeService mockTimeService = new MockTimeService( now );
        mockTimeService.setBootTime( boot );

        Period uptime = mockTimeService.upTime();
        assertEquals( 0, uptime.getMillis() );
        assertEquals( 2, uptime.getSeconds() );
    }
}
