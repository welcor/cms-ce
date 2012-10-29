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
        // TODO: siteKey should be optional (use current siteKey if null)
        return this.dataSourceService.getMenu( req, params.siteKey, params.tagItem, params.levels ).getAsJDOMDocument();
    }
}
