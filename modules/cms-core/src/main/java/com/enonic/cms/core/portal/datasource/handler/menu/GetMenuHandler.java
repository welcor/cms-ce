/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetMenuHandler")
public final class GetMenuHandler
    extends ParamsDataSourceHandler<GetMenuParams>
{
    public GetMenuHandler()
    {
        super( "getMenu", GetMenuParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetMenuParams params )
        throws Exception
    {
        Integer siteKey = params.siteKey;

        if ( siteKey == null )
        {
            siteKey = req.getSiteKey().toInt();
        }

        return this.dataSourceService.getMenu( req, siteKey, params.tagItem, params.levels, params.includeHidden ).getAsJDOMDocument();
    }
}
