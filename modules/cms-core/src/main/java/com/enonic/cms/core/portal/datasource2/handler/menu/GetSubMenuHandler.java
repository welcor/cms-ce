package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetSubMenuHandler
    extends DataSourceHandler
{
    public GetSubMenuHandler()
    {
        super( "getSubMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = req.param( "menuItemKey" ).required().asInteger();
        final int tagItem = req.param( "tagItem" ).asInteger( -1 );
        final int levels = req.param( "levels" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getSubMenu(..)
        return null;
    }
}
