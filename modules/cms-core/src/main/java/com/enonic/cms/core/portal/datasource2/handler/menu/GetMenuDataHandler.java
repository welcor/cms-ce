package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetMenuDataHandler
    extends DataSourceHandler
{
    public GetMenuDataHandler()
    {
        super( "getMenuData" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: Implement based on DataSourceServiceImpl.getMenuData(..)
        return null;
    }
}
