/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetMenuBranchHandler")
public final class GetMenuBranchHandler
    extends ParamsDataSourceHandler<GetMenuBranchParams>
{
    public GetMenuBranchHandler()
    {
        super( "getMenuBranch", GetMenuBranchParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetMenuBranchParams params )
        throws Exception
    {
        return this.dataSourceService.getMenuBranch( req, params.menuItemKey, params.includeTopLevel, params.startLevel,
                                                     params.levels ).getAsJDOMDocument();
    }
}
