package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetCategoriesHandler")
public final class GetCategoriesHandler
    extends SimpleDataSourceHandler
{
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
}
