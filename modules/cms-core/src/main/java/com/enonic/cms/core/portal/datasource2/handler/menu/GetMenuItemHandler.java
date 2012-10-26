package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuItemHandler
    extends ParamDataSourceHandler
{

    private DataSourceService dataSourceService;

    public GetMenuItemHandler()
    {
        super( "getMenuItem" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = param( req, "menuItemKey" ).required().asInteger();
        final boolean withParents = param( req, "withParents" ).asBoolean( false );
        final boolean details = param( req, "details" ).asBoolean( false );
        // TODO remove "details" parameter, is not used in DataSourceService

        return this.dataSourceService.getMenuItem( req, menuItemKey, withParents, details).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
