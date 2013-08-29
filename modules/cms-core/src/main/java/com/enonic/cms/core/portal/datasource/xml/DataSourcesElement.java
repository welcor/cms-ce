/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.xml;

import java.util.List;

import com.google.common.collect.Lists;

public final class DataSourcesElement
{
    private final List<DataSourceElement> list;

    private String resultElement;

    public DataSourcesElement()
    {
        this.list = Lists.newArrayList();
    }

    public DataSourcesElement( final DataSourcesElement source )
    {
        this.resultElement = source.resultElement;
        this.list = Lists.newArrayList();
        for ( DataSourceElement dataSourceEl : source.list )
        {
            this.list.add( new DataSourceElement( dataSourceEl ) );
        }
    }

    public String getResultElement()
    {
        return this.resultElement;
    }

    public void setResultElement( final String resultElement )
    {
        this.resultElement = resultElement;
    }

    public List<DataSourceElement> getList()
    {
        return this.list;
    }

    public void add( final DataSourceElement elem )
    {
        this.list.add( elem );
    }
}
