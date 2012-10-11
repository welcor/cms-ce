package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetContentBySectionHandler
    extends DataSourceHandler
{
    public GetContentBySectionHandler()
    {
        super( "getContentBySection" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] menuItemKeys = req.param( "menuItemKeys" ).required().asIntegerArray();
        final int levels = req.param( "levels" ).asInteger( 1 );
        final String query = req.param( "query" ).asString( "" );
        final String orderBy = req.param( "orderBy" ).asString( "" );
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean includeData = req.param( "includeData" ).asBoolean( true );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getContentBySection(..)
        return null;
    }
}
