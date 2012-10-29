package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetUserStoreHandler")
public final class GetUserStoreHandler
    extends SimpleDataSourceHandler
{
    public GetUserStoreHandler()
    {
        super( "getUserStore" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String userStore = param(req, "userStore" ).asString();
        return this.dataSourceService.getUserstore( req, userStore ).getAsJDOMDocument();
    }
}
