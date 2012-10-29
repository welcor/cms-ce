package com.enonic.cms.core.portal.datasource.handler.util;

import java.util.Collection;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.core.timezone.TimeZoneService;
import com.enonic.cms.core.timezone.TimeZoneXmlCreator;

@Component("ds.GetTimeZonesHandler")
public final class GetTimeZonesHandler
    extends SimpleDataSourceHandler
{
    private TimeService timeService;

    private TimeZoneService timeZoneService;

    public GetTimeZonesHandler()
    {
        super( "getTimeZones" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Collection<DateTimeZone> timeZones = this.timeZoneService.getTimeZones();
        final DateTime now = this.timeService.getNowAsDateTime();
        final TimeZoneXmlCreator timeZoneXmlCreator = new TimeZoneXmlCreator( now );
        return timeZoneXmlCreator.createTimeZonesDocument( timeZones );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setTimeZoneService( final TimeZoneService timeZoneService )
    {
        this.timeZoneService = timeZoneService;
    }
}
