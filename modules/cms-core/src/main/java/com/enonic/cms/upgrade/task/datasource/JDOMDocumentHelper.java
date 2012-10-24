package com.enonic.cms.upgrade.task.datasource;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;

final class JDOMDocumentHelper
{
    public static Element findElement( final Element parent, final String name )
    {
        final List<Element> list = findElements( parent, name );
        return list.isEmpty() ? null : list.get( 0 );
    }

    public static List<Element> findElements( final Element parent, final String name )
    {
        final List<Element> list = Lists.newArrayList();

        if ( parent != null )
        {
            for ( final Object o : parent.getContent( new ElementNameFilter( name ) ) )
            {
                list.add( (Element) o );
            }
        }

        return list;
    }

    public static String getTextNode( final Element parent )
    {
        if ( parent != null )
        {
            return parent.getTextNormalize().trim();
        }
        else
        {
            return null;
        }
    }

    public static void copyAttributeIfExists( final Element source, final Element target, final String name )
    {
        copyAttributeIfExists( source, target, name, name );
    }

    public static void copyAttributeIfExists( final Element source, final Element target, final String name, final String newName )
    {
        final String value = source.getAttributeValue( name );
        if ( value != null )
        {
            target.setAttribute( newName, value );
        }
    }
}
