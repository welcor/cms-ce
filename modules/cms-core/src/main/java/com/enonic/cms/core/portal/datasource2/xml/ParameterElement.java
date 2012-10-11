package com.enonic.cms.core.portal.datasource2.xml;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public final class ParameterElement
{
    private final Element root;

    private final String value;

    public ParameterElement( final Element root )
    {
        this.root = root;
        final XMLOutputter out = new XMLOutputter( Format.getCompactFormat() );
        this.value = out.outputString( this.root.getContent() );
    }

    public String getName()
    {
        return this.root.getAttributeValue( "name" );
    }

    public String getValue()
    {
        return this.value;
    }
}
