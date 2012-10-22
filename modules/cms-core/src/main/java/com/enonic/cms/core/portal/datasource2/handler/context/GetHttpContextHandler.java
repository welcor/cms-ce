package com.enonic.cms.core.portal.datasource2.handler.context;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetHttpContextHandler
    extends AbstractContextHandler
{
    public GetHttpContextHandler()
    {
        super( "getHttpContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        return null;
    }
}
