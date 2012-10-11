package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetRandomContentByCategoryHandler
    extends DataSourceHandler
{
    public GetRandomContentByCategoryHandler()
    {
        super( "getRandomContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] categoryKeys = req.param( "categoryKeys" ).required().asIntegerArray();
        final int levels = req.param( "levels" ).asInteger( 1 );
        final String query = req.param( "query" ).asString( "" );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean includeData = req.param( "includeData" ).asBoolean( true );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getRandomContentByCategory(..)
        return null;
    }
}
