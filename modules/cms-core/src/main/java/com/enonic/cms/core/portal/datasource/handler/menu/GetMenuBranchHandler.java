package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetMenuBranchHandler")
public final class GetMenuBranchHandler
    extends ParamDataSourceHandler
{
    public GetMenuBranchHandler()
    {
        super( "getMenuBranch" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = param( req, "menuItemKey" ).required().asInteger();
        final boolean includeTopLevel = param( req, "includeTopLevel" ).asBoolean( false );
        final int startLevel = param( req, "startLevel" ).asInteger( 0 );
        final int levels = param( req, "levels" ).asInteger( 0 );

        return this.dataSourceService.getMenuBranch( req, menuItemKey, includeTopLevel, startLevel, levels ).getAsJDOMDocument();
    }
}
