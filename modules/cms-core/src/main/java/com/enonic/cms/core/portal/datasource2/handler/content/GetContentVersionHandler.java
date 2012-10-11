package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetContentVersionHandler
    extends DataSourceHandler
{
    public GetContentVersionHandler()
    {
        super( "getContentVersion" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] versionKeys = req.param( "versionKeys" ).required().asIntegerArray();
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );

        // TODO: Implement based on DataSourceServiceImpl.getContentVersion(..)
        return null;
    }
}
