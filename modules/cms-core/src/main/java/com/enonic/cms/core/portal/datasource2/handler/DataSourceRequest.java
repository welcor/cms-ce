package com.enonic.cms.core.portal.datasource2.handler;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.security.user.UserEntity;

public final class DataSourceRequest
    extends DataSourceContext
{
    private String name;

    private final Map<String, String> paramMap;

    public DataSourceRequest()
    {
        this.paramMap = Maps.newHashMap();
    }

    public String getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public DataSourceParam param( final String name )
    {
        final String value = this.paramMap.get( name );
        return new DataSourceParamImpl( this.name, name, value );
    }

    public void addParam( final String name, final String value )
    {
        this.paramMap.put( name, value );
    }

    public UserEntity getCurrentUser()
    {
        return getUser();
    }

    public void setCurrentUser( final UserEntity user )
    {
        setUser( user );
    }
}
