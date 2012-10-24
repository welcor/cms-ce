package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuDataHandler
    extends DataSourceHandler
{

    private DataSourceService dataSourceService;

    public GetMenuDataHandler()
    {
        super( "getMenuData" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int siteKey = req.param( "siteKey" ).required().asInteger();

        return this.dataSourceService.getMenuData( req, siteKey ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
