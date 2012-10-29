package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

@Component("ds.GetFormattedDateHandler")
public final class GetFormattedDateHandler
    extends ParamsDataSourceHandler<GetFormattedDateParams>
{
    private final CalendarService calendarService;

    private TimeService timeService;

    public GetFormattedDateHandler()
    {
        super( "getFormattedDate", GetFormattedDateParams.class );
        this.calendarService = new CalendarService();
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetFormattedDateParams params )
        throws Exception
    {
        final int offset = params.offset;
        final String dateFormat = params.dateFormat;
        final String language = params.language;
        final String country = params.country;

        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getFormattedDate( now, offset, dateFormat, language, country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
