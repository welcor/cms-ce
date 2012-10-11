package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetCountriesHandler
    extends DataSourceHandler
{
    public GetCountriesHandler()
    {
        super( "getCountries" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String[] countryCodes = req.param( "countryCodes" ).required().asStringArray();
        final boolean includeRegions = req.param( "includeRegions" ).asBoolean( false );

        // TODO: Implement based on DataSourceServiceImpl.getCountries(..)
        return null;
    }
}
