package com.enonic.cms.core.portal.datasource2.handler.util;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetUserStoreHandler
    extends DataSourceHandler
{
    public GetUserStoreHandler()
    {
        super( "getUserStore" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String userStore = req.param( "userStore" ).asString();

        // TODO: Implement based on DataSourceServiceImpl.getUserStore(..)
        return null;
    }
}
