package com.enonic.cms.core.portal.datasource2.upgrade;

import java.util.List;

import org.jdom.Element;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

abstract class MethodConverter
{
    public abstract boolean canHandle( final String methodName );

    private List<String> getParameterValues( final Element elem )
    {
        final List<String> result = Lists.newArrayList();
        final List<Element> list = JDOMDocumentHelper.findElements( JDOMDocumentHelper.findElement( elem, "parameters" ), "parameter" );

        for ( final Element current : list )
        {
            result.add( getParameterValue( current ) );
        }

        return result;
    }

    private String getParameterValue( final Element elem )
    {
        final String override = elem.getAttributeValue( "override" );
        final String name = elem.getAttributeValue( "name" );
        String value = JDOMDocumentHelper.getTextNode( elem );

        if ( !Strings.isNullOrEmpty( name ) && "url".equalsIgnoreCase( override ) )
        {
            value = composeSelect( "param." + name, value );
        }
        else if ( !Strings.isNullOrEmpty( name ) && "session".equalsIgnoreCase( override ) )
        {
            value = composeSelect( "session." + name, value );
        }

        return value;
    }

    private String composeSelect( final String left, final String right )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "${select(" ).append( left ).append( ", " );
        str.append( "'" ).append( right != null ? right : "" ).append( "')}" );
        return str.toString();
    }

    protected final void setName( final Element result, final String name )
    {
        result.setAttribute( "name", name );
    }

    public final Element convert( final Element source, final String methodName )
    {
        final Element result = new Element( "data-source" );
        final List<String> values = getParameterValues( source );

        doConvert( result, methodName, values );
        return result;
    }

    protected final void addParameter( final Element result, final String name, final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return;
        }

        final Element elem = new Element( "parameter" );
        elem.setAttribute( "name", name );
        elem.setText( value );

        result.addContent( elem );
    }

    protected final void addParameter( final Element result, final List<String> values, final int pos, final String name )
    {
        final String value = ( pos < values.size() ) ? values.get( pos ) : null;
        addParameter( result, name, value );
    }

    public abstract void doConvert( final Element result, final String methodName, final List<String> values );
}
