package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

public final class GetFormattedDateHandler
    extends DataSourceHandler
{
    private final CalendarService calendarService;

    private TimeService timeService;

    public GetFormattedDateHandler()
    {
        super( "getFormattedDate" );
        this.calendarService = new CalendarService();
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int offset = req.param( "offset" ).asInteger( 0 );
        final String dateFormat = req.param( "dateFormat" ).asString( "EEEE d. MMMM yyyy" );
        final String language = req.param( "language" ).required().asString();
        final String country = req.param( "country" ).required().asString();

        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getFormattedDate( now, offset, dateFormat, language, country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
