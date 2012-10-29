package com.enonic.cms.core.portal.datasource.handler.menu;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetMenuHandler")
public final class GetMenuHandler
    extends SimpleDataSourceHandler
{
    public GetMenuHandler()
    {
        super( "getMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: siteKey should be optional (use current siteKey if null)
        final int siteKey = param( req, "siteKey" ).required().asInteger();
        final int tagItem = param( req, "tagItem" ).asInteger( -1 );
        final int levels = param( req, "levels" ).asInteger( 0 );

        return this.dataSourceService.getMenu( req, siteKey, tagItem, levels ).getAsJDOMDocument();
    }
}
