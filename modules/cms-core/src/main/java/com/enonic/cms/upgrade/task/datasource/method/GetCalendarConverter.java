package com.enonic.cms.upgrade.task.datasource.method;

import org.jdom.Element;

final class GetCalendarConverter
    extends DataSourceMethodConverter
{
    public GetCalendarConverter()
    {
        super( "getCalendar" );
    }

    @Override
    public Element convert( final String[] params )
    {
        if ( params.length != 8 )
        {
            return null;
        }

        return method().params( params, "relative", "year", "month", "count", "includeWeeks", "includeDays", "language",
                                "country" ).build();
    }
}
