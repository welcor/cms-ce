/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.base;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public abstract class ParamsDataSourceHandler<T>
    extends DataSourceHandler
{
    private final ParameterBeanMapper<T> beanMapper;

    public ParamsDataSourceHandler( final String name, final Class<T> paramsType )
    {
        super( name );
        this.beanMapper = ParameterBeanMapper.create( paramsType, getName() );
    }

    @Override
    public final Document handle( final DataSourceRequest req )
        throws Exception
    {
        return handle( req, this.beanMapper.newBean( req.getParams() ) );
    }

    protected abstract Document handle( final DataSourceRequest req, final T params )
        throws Exception;
}
