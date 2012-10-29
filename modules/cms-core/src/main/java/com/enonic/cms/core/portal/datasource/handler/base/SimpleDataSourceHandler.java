package com.enonic.cms.core.portal.datasource.handler.base;

import com.google.common.base.Strings;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;

public abstract class SimpleDataSourceHandler
    extends DataSourceHandler
{
    public SimpleDataSourceHandler( final String name )
    {
        super( name );
    }

    protected final String requiredParam( final DataSourceRequest req, String name )
    {
        final String value = req.getParams().get( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            throw new DataSourceException( "Parameter [{0}] is required for data source [{1}]", name, getName() );
        }

        return value;
    }

    protected final DataSourceParam param( final DataSourceRequest req, final String name )
    {
        final String value = req.getParams().get( name );
        return new DataSourceParamImpl( getName(), name, value );
    }
}
