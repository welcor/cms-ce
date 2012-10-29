package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetMenuDataHandler")
public final class GetMenuDataHandler
    extends SimpleDataSourceHandler
{
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
}
