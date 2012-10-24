package com.enonic.cms.upgrade.task.datasource;

import org.jdom.Element;

import com.google.common.base.Strings;

/**
 * This task will convert the following:
 * <p/>
 * 1) datasources element renamed to data-sources
 * 2) datasource element renamed to data-source
 * 3) parameters element is removed
 * 4) httpcontext attribute is renamed to http-context
 * 5) sessioncontext attribute is renamed to session-context
 * 6) cookiecontext attribute is renamed to cookie-context
 * 7) methodname element is now an attribute on data-source
 * 8) When parameter name is set and override="url", then ${select(param.[name], [value]} is inserted as value
 * 9) When parameter name is set and override="session", then ${select(session.[name], [value]} is inserted as value
 */
public final class DataSourceConverter1
    implements DataSourceConverter
{
    public Element convert( final Element elem )
    {
        final Element result = new Element( "datasources" );
        copyAttributeIfExists( elem, result, "result-element" );

        copyAttributeIfExists( elem, result, "httpcontext", "http-context" );
        copyAttributeIfExists( elem, result, "sessioncontext", "session-context" );
        copyAttributeIfExists( elem, result, "cookiecontext", "cookie-context" );

        for ( final Element child : JDOMDocumentHelper.findElements( elem, "datasource" ) )
        {
            result.addContent( convertDataSource( child ) );
        }

        return result;
    }

    private void copyAttributeIfExists( final Element source, final Element target, final String name )
    {
        copyAttributeIfExists( source, target, name, name );
    }

    private void copyAttributeIfExists( final Element source, final Element target, final String name, final String newName )
    {
        final String value = source.getAttributeValue( name );
        if ( value != null )
        {
            target.setAttribute( newName, value );
        }
    }

    private Element convertDataSource( final Element elem )
    {
        final Element result = new Element( "datasource" );
        final String methodName = JDOMDocumentHelper.getTextNode( JDOMDocumentHelper.findElement( elem, "methodName" ) );
        result.setAttribute( "name", methodName );

        copyAttributeIfExists( elem, result, "result-element" );
        copyAttributeIfExists( elem, result, "cache" );
        copyAttributeIfExists( elem, result, "condition" );

        final Element parametersElem = JDOMDocumentHelper.findElement( elem, "parameters" );
        for ( final Element paramElem : JDOMDocumentHelper.findElements( parametersElem, "parameter" ) )
        {
            result.addContent( convertParameter( paramElem ) );
        }

        return result;
    }

    private Element convertParameter( final Element elem )
    {
        final Element result = new Element( "parameter" );
        result.setText( getParameterValue( elem ) );

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

}
