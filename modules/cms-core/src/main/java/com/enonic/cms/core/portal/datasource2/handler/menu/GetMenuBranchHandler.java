package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetMenuBranchHandler
    extends DataSourceHandler
{
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

        // TODO: Implement based on DataSourceServiceImpl.getMenuBranch(..)
        return null;
    }
}
