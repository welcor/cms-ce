package com.enonic.cms.core.portal.datasource.handler.preference;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetPreferencesHandler")
public final class GetPreferencesHandler
    extends SimpleDataSourceHandler
{
    public GetPreferencesHandler()
    {
        super( "getPreferences" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String scope = param( req, "scope" ).asString( "*" );
        final String keyPattern = param( req, "keyPattern" ).asString( "*" );
        final boolean uniqueMatch = param( req, "uniqueMatch" ).asBoolean( true );

        return this.dataSourceService.getPreferences( req, scope, keyPattern, uniqueMatch ).getAsJDOMDocument();
    }
}
