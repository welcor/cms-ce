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
        // TODO: Implement based on DataSourceServiceImpl.getRandomContentByCategory(..)
        return null;
    }
}
