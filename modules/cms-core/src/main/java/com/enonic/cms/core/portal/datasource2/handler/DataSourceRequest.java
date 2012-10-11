package com.enonic.cms.core.portal.datasource2.handler;

import java.util.Map;

import com.google.common.collect.Maps;

public final class DataSourceRequest
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
}
