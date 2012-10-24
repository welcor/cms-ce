package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

public final class GetCalendarHandler
    extends DataSourceHandler
{
    private final CalendarService calendarService;

    private TimeService timeService;

    public GetCalendarHandler()
    {
        super( "getCalendar" );
        this.calendarService = new CalendarService();
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final boolean relative = req.param( "relative" ).asBoolean( false );
        final int year = req.param( "year" ).required().asInteger();
        final int month = req.param( "month" ).required().asInteger();
        final int count = req.param( "count" ).required().asInteger();
        final boolean includeWeeks = req.param( "includeWeeks" ).asBoolean( false );
        final boolean includeDays = req.param( "includeDays" ).asBoolean( false );
        final String language = req.param( "language" ).required().asString();
        final String country = req.param( "country" ).required().asString();

        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getCalendar( now, relative, year, month, count, includeWeeks, includeDays, language, country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
