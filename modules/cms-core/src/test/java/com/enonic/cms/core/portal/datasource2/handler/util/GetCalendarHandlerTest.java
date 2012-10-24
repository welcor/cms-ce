package com.enonic.cms.core.portal.datasource2.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.time.TimeService;

public class GetCalendarHandlerTest
    extends AbstractDataSourceHandlerTest<GetCalendarHandler>
{
    public GetCalendarHandlerTest()
    {
        super( GetCalendarHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        final TimeService timeService = Mockito.mock( TimeService.class );
        Mockito.when( timeService.getNowAsMilliseconds() ).thenReturn( 0L );
        this.handler.setTimeService( timeService );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_paramsNotSet()
        throws Exception
    {
        this.handler.handle( this.request );
    }

    @Test
    public void testHandler_defaultParams()
        throws Exception
    {
        this.request.addParam( "year", "1970" );
        this.request.addParam( "month", "1" );
        this.request.addParam( "count", "2" );
        this.request.addParam( "language", "en" );
        this.request.addParam( "country", "uk" );
        testHandle( "getCalendar_default" );
    }

    @Test
    public void testHandler_params()
        throws Exception
    {
        this.request.addParam( "year", "1970" );
        this.request.addParam( "month", "1" );
        this.request.addParam( "count", "2" );
        this.request.addParam( "language", "en" );
        this.request.addParam( "country", "uk" );
        this.request.addParam( "includeWeeks", "true" );
        this.request.addParam( "includeDays", "true" );
        testHandle( "getCalendar_params" );
    }
}
