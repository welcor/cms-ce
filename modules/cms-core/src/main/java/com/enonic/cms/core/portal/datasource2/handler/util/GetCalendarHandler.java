package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetCalendarHandler
    extends DataSourceHandler
{
    public GetCalendarHandler()
    {
        super( "getCalendar" );
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
        final String language = req.param( "language" ).required().asString();
        final String country = req.param( "country" ).required().asString();

        // TODO: Implement based on DataSourceServiceImpl.getCalendar(..)
        return null;
    }
}
