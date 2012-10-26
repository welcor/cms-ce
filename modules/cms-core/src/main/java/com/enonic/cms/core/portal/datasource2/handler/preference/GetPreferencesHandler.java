package com.enonic.cms.core.portal.datasource2.handler.preference;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

public final class GetPreferencesHandler
    extends ParamDataSourceHandler
{
    public GetPreferencesHandler()
    {
        super( "getPreferences" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String[] scopes = param( req, "scope" ).asStringArray( " WINDOW,PORTLET,PAGE,SITE,GLOBAL" );
        final String keyPattern = param( req, "keyPattern" ).asString( "*" );
        final boolean uniqueMatch = param( req, "uniqueMatch" ).asBoolean( true );

        // TODO: Implement based on DataSourceServiceImpl.getPreferences(..)
        return null;
    }
}
