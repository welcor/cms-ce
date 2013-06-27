/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.AbstractDataSourceHandlerTest;
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
        final DateTime now = new DateTime( 0, DateTimeZone.UTC );
        Mockito.when( timeService.getNowAsDateTime() ).thenReturn( now );
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
        this.request.addParam( "language", "en" );
        this.request.addParam( "country", "uk" );
        testHandle( "getFormattedDate_default" );
    }

    @Test
    public void testHandler_params()
        throws Exception
    {
        this.request.addParam( "offset", "1" );
        this.request.addParam( "dateFormat", "yyyy" );
        this.request.addParam( "language", "en" );
        this.request.addParam( "country", "uk" );
        testHandle( "getFormattedDate_params" );
    }
}
