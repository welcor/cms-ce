package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetContentByQueryHandler
    extends DataSourceHandler
{
    public GetContentByQueryHandler()
    {
        super( "getContentByQuery" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: Implement based on DataSourceServiceImpl.getContentByQuery(..)
        return null;
    }
}
