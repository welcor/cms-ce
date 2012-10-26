package com.enonic.cms.core.portal.datasource.handler.base;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;

public abstract class ParamDataSourceHandler
    extends DataSourceHandler
{
    public ParamDataSourceHandler( final String name )
    {
        super( name );
    }

    protected final DataSourceParam param( final DataSourceRequest req, final String name )
    {
        final String value = req.getParams().get( name );
        return new DataSourceParamImpl( getName(), name, value );
    }
}
