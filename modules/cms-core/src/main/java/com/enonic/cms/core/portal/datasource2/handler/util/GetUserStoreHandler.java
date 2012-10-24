package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetUserStoreHandler
    extends DataSourceHandler
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
        final String userStore = req.param( "userStore" ).asString();
        return this.dataSourceService.getUserstore( req, userStore ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
