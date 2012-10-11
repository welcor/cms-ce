package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetMenuItemHandler
    extends DataSourceHandler
{
    public GetMenuItemHandler()
    {
        super( "getMenuItem" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = req.param( "menuItemKey" ).required().asInteger();
        final boolean tagItem = req.param( "withParents" ).asBoolean( false );
        final boolean details = req.param( "details" ).asBoolean( false );

        // TODO: Implement based on DataSourceServiceImpl.getMenuItem(..)
        return null;
    }
}
