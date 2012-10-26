package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuDataHandler
    extends ParamDataSourceHandler
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
        // TODO: siteKey should be optional (use current siteKey if null)
        final int siteKey = param( req, "siteKey" ).required().asInteger();

        return this.dataSourceService.getMenuData( req, siteKey ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
