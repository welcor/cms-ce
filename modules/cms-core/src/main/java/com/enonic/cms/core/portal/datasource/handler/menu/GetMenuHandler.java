package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuHandler
    extends ParamDataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetMenuHandler()
    {
        super( "getMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: siteKey should be optional (use current siteKey if null)
        final int siteKey = param( req, "siteKey" ).required().asInteger();
        final int tagItem = param( req, "tagItem" ).asInteger( -1 );
        final int levels = param( req, "levels" ).asInteger( 0 );
        final boolean details = param( req, "details" ).asBoolean( true );
        // TODO remove "details" parameter, is not used in DataSourceService

        return this.dataSourceService.getMenu( req, siteKey, tagItem, levels, details ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
