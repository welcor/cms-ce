/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import java.util.List;

import org.jdom.Element;

import com.google.common.base.Strings;

public class Datasource
{
    private final Element root;

    public Datasource( final Element root )
    {
        this.root = root;
    }

    public String getMethodName()
    {
        return this.root.getAttributeValue( "name" );
    }

    public List getParameterElements()
    {
        return this.root.getChildren();
    }

    public String getResultElementName()
    {
        final String value = this.root.getAttributeValue( "result-element" );
        return Strings.emptyToNull( value );
    }

    public String getCondition()
    {
        return this.root.getAttributeValue( "condition" );
    }

    public boolean isCacheable()
    {
        final String cacheableAttr = this.root.getAttributeValue( "cache" );
        if ( cacheableAttr != null )
        {
            return "true".equals( cacheableAttr );
        }

        return true;
    }
}
