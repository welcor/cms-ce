package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.cms.core.portal.datasource.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.core.timezone.TimeZoneService;

public class GetTimeZonesHandlerTest
    extends AbstractDataSourceHandlerTest<GetTimeZonesHandler>
{
    private TimeZoneService timeZoneService;

    public GetTimeZonesHandlerTest()
    {
        super( GetTimeZonesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        final TimeService timeService = Mockito.mock( TimeService.class );
        Mockito.when( timeService.getNowAsDateTime() ).thenReturn( new DateTime( 0 ) );

        this.timeZoneService = Mockito.mock( TimeZoneService.class );

        this.handler.setTimeService( timeService );
        this.handler.setTimeZoneService( this.timeZoneService );
    }

    @Test
    public void testEmpty()
        throws Exception
    {
        final List<DateTimeZone> list = Lists.newArrayList();
        Mockito.when( this.timeZoneService.getTimeZones() ).thenReturn( list );
        testHandle( "getTimeZones_empty" );
    }

    @Test
    public void testList()
        throws Exception
    {
        final List<DateTimeZone> list = Lists.newArrayList( DateTimeZone.UTC, DateTimeZone.forID( "CET" ) );
        Mockito.when( this.timeZoneService.getTimeZones() ).thenReturn( list );
        testHandle( "getTimeZones_list" );
    }
}
