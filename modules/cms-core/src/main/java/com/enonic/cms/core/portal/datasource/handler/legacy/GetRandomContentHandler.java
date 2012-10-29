package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetRandomContentHandler")
public final class GetRandomContentHandler
    extends ParamsDataSourceHandler<GetRandomContentParams>
{
    public GetRandomContentHandler()
    {
        super( "getRandomContent", GetRandomContentParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetRandomContentParams params )
        throws Exception
    {
        // TODO: Implement based on DataSourceServiceImpl.getRandomContent(..)
        return null;
    }
}
