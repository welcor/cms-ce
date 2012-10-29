package com.enonic.cms.core.portal.datasource.handler.preference;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetPreferencesHandler")
public final class GetPreferencesHandler
    extends ParamsDataSourceHandler<GetPreferencesParams>
{
    public GetPreferencesHandler()
    {
        super( "getPreferences", GetPreferencesParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetPreferencesParams params )
        throws Exception
    {
        return this.dataSourceService.getPreferences( req, params.scope, params.keyPattern, params.uniqueMatch ).getAsJDOMDocument();
    }
}
