package com.enonic.cms.core.portal.datasource.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

@Component("ds.GetUserStoreHandler")
public final class GetUserStoreHandler
    extends ParamDataSourceHandler
{
    private DataSourceService dataSourceService;

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

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
