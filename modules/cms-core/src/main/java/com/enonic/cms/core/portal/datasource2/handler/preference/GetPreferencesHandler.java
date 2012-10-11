package com.enonic.cms.core.portal.datasource2.handler.preference;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetPreferencesHandler
    extends DataSourceHandler
{
    public GetPreferencesHandler()
    {
        super( "getPreferences" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String[] scopes = req.param( "scope" ).asStringArray( " WINDOW,PORTLET,PAGE,SITE,GLOBAL" );
        final String keyPattern = req.param( "keyPattern" ).asString( "*" );
        final boolean uniqueMatch = req.param( "uniqueMatch" ).asBoolean( true );

        // TODO: Implement based on DataSourceServiceImpl.getPreferences(..)
        return null;
    }
}
