package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

@Component("ds.GetCalendarHandler")
public final class GetCalendarHandler
    extends ParamsDataSourceHandler<GetCalendarParams>
{
    private final CalendarService calendarService;

    private TimeService timeService;

    public GetCalendarHandler()
    {
        super( "getCalendar", GetCalendarParams.class );
        this.calendarService = new CalendarService();
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetCalendarParams params )
        throws Exception
    {
        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getCalendar( now, params.relative, params.year, params.month, params.count, params.includeWeeks,
                                                 params.includeDays, params.language, params.country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
