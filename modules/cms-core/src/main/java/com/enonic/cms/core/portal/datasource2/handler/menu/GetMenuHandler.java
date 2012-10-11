package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetMenuHandler
    extends DataSourceHandler
{
    public GetMenuHandler()
    {
        super( "getMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuKey = req.param( "menuKey" ).required().asInteger();
        final int menuItemKey = req.param( "menuItemKey" ).asInteger( -1 );
        final int levels = req.param( "levels" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getMenu(..)
        return null;
    }
}
