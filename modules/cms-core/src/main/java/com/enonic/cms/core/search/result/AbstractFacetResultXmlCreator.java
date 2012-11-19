package com.enonic.cms.core.search.result;

import org.jdom.Element;

import com.google.common.base.Strings;

public abstract class AbstractFacetResultXmlCreator
{
    protected Element createFacetRootElement( final AbstractFacetResultSet facet )
    {
        final Element facetEl = new Element( "facet" );
        facetEl.setAttribute( "name", facet.getName() );

        return facetEl;
    }

    protected void addAttributeIfNotNull( Element element, String attributeName, String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return;
        }

        element.setAttribute( attributeName, value );

    }

    protected void addAttributeIfNotNull( Element element, String attributeName, Number value )
    {
        if ( value == null )
        {
            return;
        }

        element.setAttribute( attributeName, value.toString() );

    }


}
