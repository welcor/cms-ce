package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetCategoriesHandler
    extends ParamDataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetCategoriesHandler()
    {
        super( "getCategories" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int categoryKey = param( req, "categoryKey" ).required().asInteger();
        final int levels = param( req, "levels" ).asInteger( 0 );
        final boolean includeContentCount = param( req, "includeContentCount" ).asBoolean( false );
        final boolean includeTopCategory = param( req, "includeTopCategory" ).asBoolean( true );

        return this.dataSourceService.getCategories( req, categoryKey, levels, includeContentCount, includeTopCategory ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
