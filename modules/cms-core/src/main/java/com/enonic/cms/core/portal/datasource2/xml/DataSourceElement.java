package com.enonic.cms.core.portal.datasource2.xml;

import java.util.List;

import com.google.common.collect.Lists;

public final class DataSourceElement
{
    private final String name;

    private final List<ParameterElement> list;

    private boolean cache;

    private String resultElement;

    private String condition;

    public DataSourceElement( final String name )
    {
        this.name = name;
        this.list = Lists.newArrayList();
    }

    public String getName()
    {
        return this.name;
    }

    public String getResultElement()
    {
        return resultElement;
    }

    public void setResultElement( final String resultElement )
    {
        this.resultElement = resultElement;
    }

    public boolean isCache()
    {
        return cache;
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

    public List<ParameterElement> getParameters()
    {
        return this.list;
    }

    public void add( final ParameterElement param )
    {
        this.list.add( param );
    }
}
