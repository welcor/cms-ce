package com.enonic.cms.core.portal.datasource2.handler.context;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetCookieContextHandler
    extends AbstractContextHandler
{
    public GetCookieContextHandler()
    {
        super( "getCookieContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        return null;
    }
}
