package com.enonic.cms.core.portal.datasource.handler.context;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

public final class GetSessionContextHandler
    extends ParamDataSourceHandler
{
    public GetSessionContextHandler()
    {
        super( "getSessionContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        return null;
    }
}
