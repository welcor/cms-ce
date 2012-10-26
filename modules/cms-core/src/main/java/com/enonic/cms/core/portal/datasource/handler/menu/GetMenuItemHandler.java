package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

@Component("ds.GetMenuItemHandler")
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

        return this.dataSourceService.getMenuItem( req, menuItemKey, withParents ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
