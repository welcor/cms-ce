package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.service.CalendarService;
import com.enonic.cms.core.time.TimeService;

@Component("ds.GetFormattedDateHandler")
public final class GetFormattedDateHandler
    extends SimpleDataSourceHandler
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
        final int offset = param(req, "offset" ).asInteger( 0 );
        final String dateFormat = param(req, "dateFormat" ).asString( "EEEE d. MMMM yyyy" );
        final String language = param(req, "language" ).required().asString();
        final String country = param(req, "country" ).required().asString();

        final long now = this.timeService.getNowAsMilliseconds();
        return this.calendarService.getFormattedDate( now, offset, dateFormat, language, country );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
