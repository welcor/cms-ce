/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetMenuItemHandler")
public final class GetMenuItemHandler
    extends ParamsDataSourceHandler<GetMenuItemParams>
{
    public GetMenuItemHandler()
    {
        super( "getMenuItem", GetMenuItemParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetMenuItemParams params )
        throws Exception
    {
        return this.dataSourceService.getMenuItem( req, params.menuItemKey, params.withParents ).getAsJDOMDocument();
    }
}
