/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.xml;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public final class DataSourceElement
{
    private final String name;

    private boolean cache;

    private String resultElement;

    private String condition;

    private final Map<String, String> parameters;

    public DataSourceElement( final String name )
    {
        this.name = name;
        this.parameters = Maps.newHashMap();
    }

    public String getName()
    {
        return this.name;
    }

    public String getResultElement()
    {
        return this.resultElement;
    }

    public void setResultElement( final String resultElement )
    {
        this.resultElement = resultElement;
    }

    public boolean isCache()
    {
        return this.cache;
    }

    public void setCache( final boolean cache )
    {
        this.cache = cache;
    }

    public String getCondition()
    {
        return condition;
    }

    public void setCondition( final String condition )
    {
        this.condition = condition;
    }

    public Map<String, String> getParameters()
    {
        return this.parameters;
    }

    public void addParameter( final String name, final String value )
    {
        if ( !Strings.isNullOrEmpty( name ) )
        {
            this.parameters.put( name, value );
        }
    }
}
