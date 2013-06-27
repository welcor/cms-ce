/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.xml;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.enonic.cms.framework.util.JDOMUtil;

public final class DataSourceXmlFactory
{
    private final XMLOutputter xmlOutputter;

    public DataSourceXmlFactory()
    {
        this.xmlOutputter = new XMLOutputter( Format.getCompactFormat() );
    }

    public DataSourcesElement create( final Element root )
    {
        final DataSourcesElement result = new DataSourcesElement();
        if ( root != null )
        {
            populate( result, root );
        }

        return result;
    }

    private void populate( final DataSourcesElement result, final Element root )
    {
        result.setResultElement( root.getAttributeValue( "result-element" ) );

        for ( final Element child : JDOMUtil.getElements( root ) )
        {
            result.add( createDataSource( child ) );
        }
    }

    private DataSourceElement createDataSource( final Element root )
    {
        final DataSourceElement result = new DataSourceElement( root.getAttributeValue( "name" ) );
        result.setResultElement( root.getAttributeValue( "result-element" ) );
        result.setCondition( root.getAttributeValue( "condition" ) );

        final String cacheAttr = root.getAttributeValue( "cache" );
        if ( cacheAttr == null )
        {
            result.setCache( true );
        }
        else
        {
            result.setCache( "true".equals( cacheAttr ) );
        }

        for ( final Element child : JDOMUtil.getElements( root ) )
        {
            addParameter( result, child );
        }

        return result;
    }

    private void addParameter( final DataSourceElement result, final Element root )
    {
        final String name = root.getAttributeValue( "name" );
        String value = root.getTextNormalize();

        if ( isXmlParameter( root ) )
        {
            value = this.xmlOutputter.outputString( root.getContent() );
        }

        result.addParameter( name, value );
    }

    private boolean isXmlParameter( final Element elem )
    {
        return !elem.getChildren().isEmpty();
    }
}
