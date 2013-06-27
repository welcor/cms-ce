/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetUserStoreHandler")
public final class GetUserStoreHandler
    extends ParamsDataSourceHandler<GetUserStoreParams>
{
    public GetUserStoreHandler()
    {
        super( "getUserStore", GetUserStoreParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetUserStoreParams params )
        throws Exception
    {
        return this.dataSourceService.getUserstore( req, params.name ).getAsJDOMDocument();
    }
}
