package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

@Component("ds.GetCalendarHandler")
public final class GetCalendarHandler
    extends SimpleDataSourceHandler
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
        final boolean relative = param(req, "relative" ).asBoolean( false );
        final int year = param(req, "year" ).required().asInteger();
        final int month = param(req, "month" ).required().asInteger();
        final int count = param(req, "count" ).required().asInteger();
        final boolean includeWeeks = param(req, "includeWeeks" ).asBoolean( false );
        final boolean includeDays = param(req, "includeDays" ).asBoolean( false );
        final String language = param(req, "language" ).required().asString();
        final String country = param(req, "country" ).required().asString();

        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getCalendar( now, relative, year, month, count, includeWeeks, includeDays, language, country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
