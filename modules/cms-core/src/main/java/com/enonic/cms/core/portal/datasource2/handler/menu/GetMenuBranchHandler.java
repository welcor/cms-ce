package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuBranchHandler
    extends DataSourceHandler
{

    private DataSourceService dataSourceService;

    public GetMenuBranchHandler()
    {
        super( "getMenuBranch" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = req.param( "menuItemKey" ).required().asInteger();
        final boolean includeTopLevel = req.param( "includeTopLevel" ).asBoolean( false );
        final int startLevel = req.param( "startLevel" ).asInteger( 0 );
        final int levels = req.param( "levels" ).asInteger( 0 );

        return this.dataSourceService.getMenuBranch( req, menuItemKey, includeTopLevel, startLevel, levels ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
