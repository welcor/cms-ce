package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetFormattedDateHandler
    extends DataSourceHandler
{
    public GetFormattedDateHandler()
    {
        super( "getFormattedDate" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int offset = req.param( "offset" ).asInteger(0);
        final String dateFormat = req.param( "dateFormat" ).asString( "EEEE d. MMMM yyyy" );
        final String language = req.param( "language" ).required().asString();
        final String country = req.param( "country" ).required().asString();

        // TODO: Implement based on DataSourceServiceImpl.getFormattedDate(..)
        return null;
    }
}
