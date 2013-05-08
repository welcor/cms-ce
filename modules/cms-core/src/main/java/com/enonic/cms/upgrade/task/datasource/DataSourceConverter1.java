package com.enonic.cms.upgrade.task.datasource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.google.common.base.Strings;

import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.copyAttributeIfExists;
import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.findElement;
import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.findElements;
import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.getTextNode;

public final class DataSourceConverter1
    extends DataSourceConverter
{
    public DataSourceConverter1( final DataSourceConverterLogger logger )
    {
        super( logger );
    }

    public Element convert( final Element elem )
    {
        final Element result = new Element( "datasources" );
        copyAttributeIfExists( elem, result, "result-element" );

        copyAttributeIfExists( elem, result, "httpcontext", "http-context" );
        copyAttributeIfExists( elem, result, "sessioncontext", "session-context" );
        copyAttributeIfExists( elem, result, "cookiecontext", "cookie-context" );

        for ( final Element child : findElements( elem, "datasource" ) )
        {
            result.addContent( convertDataSource( child ) );
        }

        return result;
    }

    private Element convertDataSource( final Element elem )
    {
        final Element result = new Element( "datasource" );
        final String methodName = getTextNode( findElement( elem, "methodName" ) );

        if ( Strings.isNullOrEmpty( methodName ) )
        {
            this.logger.logWarning( this.currentContext + " : method name is missing in data source. Setting to empty." );
        }

        result.setAttribute( "name", methodName != null ? methodName : "" );

        copyAttributeIfExists( elem, result, "result-element" );
        copyAttributeIfExists( elem, result, "cache" );
        copyAttributeIfExists( elem, result, "condition" );

        final Element parametersElem = findElement( elem, "parameters" );
        for ( final Element paramElem : findElements( parametersElem, "parameter" ) )
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

        if ( !Strings.isNullOrEmpty( name ) )
        {
            if ( "url".equalsIgnoreCase( override ) )
            {
                value = composeSelect( "param", name, value );
            }
            else if ( "session".equalsIgnoreCase( override ) )
            {
                value = composeSelect( "session", name, value );
            }
        }

        return value;
    }

    private String composeSelect( String prefix, String name, String value )
    {
        boolean isNonAscii = isNonWordCharacter( name );
        return isNonAscii ? composeSpecialSelect( prefix, name, value ) : composeNonSpecialSelect( prefix + "." + name, value );
    }

    /**
     * Check for special characters
     * @param string to process
     * @return true if special character exists, false otherwise
     */
    private boolean isNonWordCharacter( String string )
    {
        final char[] chars = string.toCharArray();

        for ( final char c : chars )
        {
            // A-Z: 65..90; a-z: 97..122
            if ( c < 65 || ( c > 90 && c < 97) || c > 122 )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Result sequence like this:
     * <parameter name="param10">${select(param['n.template'], '')}</parameter>
     */
    private String composeSpecialSelect( String prefix, String left, String right )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "${select(" ).append( prefix ).append( "['" ).append( left ).append( "']" ).append( ", " );

        if ( isElExpression( right ) )
        {
            str.append( getStrippedElExpression( right ) ).append( ")}" );
        }
        else
        {
            str.append( "'" ).append( right != null ? right : "" ).append( "')}" );
        }

        return str.toString();
    }

    /**
     * Result sequence like this:
     * <parameter name="param10">${select(param.n.template, '')}</parameter>
     */
    private String composeNonSpecialSelect( final String left, final String right )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "${select(" ).append( left ).append( ", " );

        if ( isElExpression( right ) )
        {
            str.append( getStrippedElExpression( right ) ).append( ")}" );
        }
        else
        {
            str.append( "'" ).append( right != null ? right : "" ).append( "')}" );
        }

        return str.toString();
    }

    private boolean isElExpression( final String right )
    {
        return StringUtils.startsWithIgnoreCase( right, "${" ) && StringUtils.endsWith( right, "}" );
    }

    private String getStrippedElExpression( String expression )
    {
        expression = StringUtils.stripStart( expression, "${" );
        expression = StringUtils.stripEnd( expression, "}" );
        return expression;
    }


}
