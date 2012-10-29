package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetSubMenuHandler")
public final class GetSubMenuHandler
    extends ParamsDataSourceHandler<GetSubMenuParams>
{
    public GetSubMenuHandler()
    {
        super( "getSubMenu", GetSubMenuParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetSubMenuParams params )
        throws Exception
    {
        return this.dataSourceService.getSubMenu( req, params.menuItemKey, params.tagItem, params.levels ).getAsJDOMDocument();
    }
}
