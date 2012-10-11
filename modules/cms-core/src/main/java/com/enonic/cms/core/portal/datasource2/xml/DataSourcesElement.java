package com.enonic.cms.core.portal.datasource2.xml;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

public final class DataSourcesElement
{
    private final Element root;

    private final List<DataSourceElement> list;

    public DataSourcesElement( final Element root )
    {
        this.root = root;

        this.list = Lists.newArrayList();
        for ( final Object o : this.root.getChildren( "data-source" ) )
        {
            this.list.add( new DataSourceElement( (Element) o ) );
        }
    }

    public boolean isHttpContext()
    {
        final String value = this.root.getAttributeValue( "http-context" );
        return value != null && "true".equals( value );
    }

    public boolean isCookieContext()
    {
        final String value = this.root.getAttributeValue( "cookie-context" );
        return value != null && "true".equals( value );
    }

    public boolean isSessionContext()
    {
        final String value = this.root.getAttributeValue( "session-context" );
        return value != null && "true".equals( value );
    }

    public String getResultElement()
    {
        return this.root.getAttributeValue( "result-element" );
    }

    public List<DataSourceElement> getList()
    {
        return this.list;
    }
}
