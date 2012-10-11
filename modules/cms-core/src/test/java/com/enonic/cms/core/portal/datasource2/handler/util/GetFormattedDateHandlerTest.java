package com.enonic.cms.core.portal.datasource2.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.time.TimeService;

public class GetFormattedDateHandlerTest
    extends AbstractDataSourceHandlerTest<GetFormattedDateHandler>
{
    public GetFormattedDateHandlerTest()
    {
        super( GetFormattedDateHandler.class );
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
        this.request.addParam( "language", "no" );
        this.request.addParam( "country", "no" );
        testHandle( "getFormattedDate_default" );
    }

    @Test
    public void testHandler_params()
        throws Exception
    {
        this.request.addParam( "offset", "1" );
        this.request.addParam( "dateFormat", "yyyy" );
        this.request.addParam( "language", "no" );
        this.request.addParam( "country", "no" );
        testHandle( "getFormattedDate_params" );
    }
}
