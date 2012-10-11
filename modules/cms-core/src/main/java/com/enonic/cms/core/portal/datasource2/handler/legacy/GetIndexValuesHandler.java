package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetIndexValuesHandler
    extends DataSourceHandler
{
    public GetIndexValuesHandler()
    {
        super( "getIndexValues" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String path = req.param( "path" ).required().asString();
        final Integer[] categoryKeys = req.param( "categoryKeys" ).asIntegerArray();
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        final Integer[] contentTypeKeys = req.param( "contentTypeKeys" ).asIntegerArray();
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 200 );
        final boolean distinct = req.param( "distinct" ).asBoolean( true );
        final String order = req.param( "order" ).asString( "ASC" );

        // TODO: Implement based on DataSourceServiceImpl.getIndexValues(..)
        return null;
    }
}
