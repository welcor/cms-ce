package com.enonic.cms.core.portal.datasource2.xml;

import java.util.List;

import com.google.common.collect.Lists;

public final class DataSourcesElement
{
    private boolean httpContext;

    private boolean cookieContext;

    private boolean sessionContext;

    private String resultElement;

    private final List<DataSourceElement> list;

    public DataSourcesElement()
    {
        this.list = Lists.newArrayList();
    }

    public boolean isHttpContext()
    {
        return httpContext;
    }

    public void setHttpContext( final boolean httpContext )
    {
        this.httpContext = httpContext;
    }

    public boolean isCookieContext()
    {
        return cookieContext;
    }

    public void setCookieContext( final boolean cookieContext )
    {
        this.cookieContext = cookieContext;
    }

    public boolean isSessionContext()
    {
        return sessionContext;
    }

    public void setSessionContext( final boolean sessionContext )
    {
        this.sessionContext = sessionContext;
    }

    public String getResultElement()
    {
        return resultElement;
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
