package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetCategoriesHandler
    extends DataSourceHandler
{
    public GetCategoriesHandler()
    {
        super( "getCategories" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int categoryKey = req.param( "categoryKey" ).required().asInteger();
        final int levels = req.param( "levels" ).asInteger(0);
        final boolean includeContentCount = req.param( "includeContentCount" ).asBoolean( false );
        final boolean includeTopCategory = req.param( "includeTopCategory" ).asBoolean( true );

        // TODO: Implement based on DataSourceServiceImpl.getCategories(..)
        return null;
    }
}
