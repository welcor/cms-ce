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
        final Element elem = this.root.getChild( "methodname" );
        if ( elem == null )
        {
            return null;
        }

        return Strings.emptyToNull( elem.getText() );
    }

    public List getParameterElements()
    {
        final Element parametersElem = this.root.getChild( "parameters" );
        return parametersElem.getChildren();
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
        String methodname = getMethodName();

        // we cant cache getPreferences calls, cause they depend on objectKey, which change within a request
        if ( methodname != null && methodname.startsWith( "getPreferences" ) )
        {
            return false;
        }

        final String cacheableAttr = this.root.getAttributeValue( "cache" );
        if ( cacheableAttr != null )
        {
            return "true".equals( cacheableAttr );
        }

        // default value except plugins
        return true;
    }
}
