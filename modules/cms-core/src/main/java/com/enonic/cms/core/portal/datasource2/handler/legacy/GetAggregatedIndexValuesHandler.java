package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetAggregatedIndexValuesHandler
    extends DataSourceHandler
{
    public GetAggregatedIndexValuesHandler()
    {
        super( "getAggregatedIndexValues" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String path = req.param( "path" ).required().asString();
        final Integer[] categoryKeys = req.param( "categoryKeys" ).asIntegerArray();
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        final Integer[] contentTypeKeys = req.param( "contentTypeKeys" ).asIntegerArray();

        // TODO: Implement based on DataSourceServiceImpl.getAggregatedIndexValues(..)
        return null;
    }
}
