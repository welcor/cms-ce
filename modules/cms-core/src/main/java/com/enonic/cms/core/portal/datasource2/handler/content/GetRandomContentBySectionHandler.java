package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetRandomContentBySectionHandler
    extends DataSourceHandler
{
    public GetRandomContentBySectionHandler()
    {
        super( "getRandomContentBySection" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: Implement based on DataSourceServiceImpl.getRandomContentBySection(..)
        return null;
    }
}
