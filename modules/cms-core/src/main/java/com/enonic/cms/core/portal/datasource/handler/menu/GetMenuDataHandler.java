package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetMenuDataHandler")
public final class GetMenuDataHandler
    extends ParamsDataSourceHandler<GetMenuDataParams>
{
    public GetMenuDataHandler()
    {
        super( "getMenuData", GetMenuDataParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetMenuDataParams params )
        throws Exception
    {
        return this.dataSourceService.getMenuData( req, params.siteKey ).getAsJDOMDocument();
    }
}
