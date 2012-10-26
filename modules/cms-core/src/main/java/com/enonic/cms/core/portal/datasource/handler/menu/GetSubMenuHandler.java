package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

public final class GetSubMenuHandler
    extends ParamDataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetSubMenuHandler()
    {
        super( "getSubMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = param( req, "menuItemKey" ).required().asInteger();
        final int tagItem = param( req, "tagItem" ).asInteger( -1 );
        final int levels = param( req, "levels" ).asInteger( 0 );
        final boolean details = param( req, "details" ).asBoolean( false );
        // TODO remove "details" parameter, is not used in DataSourceService

        return this.dataSourceService.getSubMenu( req, menuItemKey, tagItem, levels, details ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
