package com.enonic.cms.core.portal.datasource2.xml;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

public final class DataSourceElement
{
    private final Element root;

    private final List<ParameterElement> list;

    public DataSourceElement( final Element root )
    {
        this.root = root;

        this.list = Lists.newArrayList();
        for ( final Object o : this.root.getChildren( "parameter" ) )
        {
            this.list.add( new ParameterElement( (Element) o ) );
        }
    }

    public String getName()
    {
        return this.root.getAttributeValue( "name" );
    }

    public String getResultElement()
    {
        return this.root.getAttributeValue( "result-element" );
    }

    public boolean isCache()
    {
        final String value = this.root.getAttributeValue( "cache" );
        return value != null && "true".equals( value );
    }

    public String getCondition()
    {
        return this.root.getAttributeValue( "condition" );
    }

    public List<ParameterElement> getParameters()
    {
        return this.list;
    }
}
