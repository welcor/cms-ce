/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.google.common.base.Strings;

import com.enonic.cms.framework.util.JDOMUtil;

public class Datasources
{
    private final Element root;

    public Datasources( final Element root )
    {
        if ( root == null )
        {
            this.root = new Element( "datasources" );
        }
        else
        {
            this.root = root;
        }
    }

    public boolean hasSessionContext()
    {
        return hasAttributeValue( "true", "sessioncontext", this.root );
    }

    public boolean hasHttpContext()
    {
        return hasAttributeValue( "true", "httpcontext", this.root );
    }

    public boolean hasCookieContext()
    {
        return hasAttributeValue( "true", "cookiecontext", this.root );
    }

    public List<Datasource> getDatasourceElements()
    {
        final ArrayList<Datasource> datasources = new ArrayList<Datasource>();
        for ( Element datasourceElement : JDOMUtil.getElements( this.root ) )
        {
            datasources.add( new Datasource( datasourceElement ) );
        }
        return datasources;
    }

    public boolean isCacheable()
    {
        boolean cacheable = !hasSessionContext();
        cacheable &= !hasHttpContext();
        cacheable &= !hasCookieContext();
        return cacheable;
    }

    private boolean hasAttributeValue( String expectedValue, String attrName, Element el )
    {
        return expectedValue.equals( el.getAttributeValue( attrName ) );
    }

    public String getResultElementName()
    {
        final String value = this.root.getAttributeValue( "result-element" );
        return Strings.emptyToNull( value );
    }
}
