package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetSubMenuHandler")
public final class GetSubMenuHandler
    extends ParamDataSourceHandler
{
    public GetSubMenuHandler()
    {
        super( "getSubMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int menuItemKey = param( req, "menuItemKey" ).required().asInteger();
        final int tagItem = param( req, "tagItem" ).asInteger( -1 );
        final int levels = param( req, "levels" ).asInteger( 0 );

        return this.dataSourceService.getSubMenu( req, menuItemKey, tagItem, levels ).getAsJDOMDocument();
    }
}
