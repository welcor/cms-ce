package com.enonic.cms.core.portal.datasource2.handler.menu;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMenuHandler
    extends DataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetMenuHandler()
    {
        super( "getMenu" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int siteKey = req.param( "siteKey" ).required().asInteger();
        final int tagItem = req.param( "tagItem" ).asInteger( -1 );
        final int levels = req.param( "levels" ).asInteger( 0 );
        final boolean details = req.param( "details" ).asBoolean( true );

        return this.dataSourceService.getMenu( req, siteKey, tagItem, levels, details ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
