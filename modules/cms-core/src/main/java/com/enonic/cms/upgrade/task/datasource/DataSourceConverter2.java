package com.enonic.cms.upgrade.task.datasource;

import java.util.List;

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.enonic.cms.upgrade.task.datasource.method.DataSourceMethodConverter;
import com.enonic.cms.upgrade.task.datasource.method.DataSourceMethodConverters;
import com.enonic.cms.upgrade.task.datasource.method.MethodElementBuilder;

import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.copyAttributeIfExists;
import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.findElements;
import static com.enonic.cms.upgrade.task.datasource.JDOMDocumentHelper.getTextNode;

public final class DataSourceConverter2
    implements DataSourceConverter
{
    private final DataSourceConverterLogger logger;

    private final DataSourceMethodConverters converters;

    private final XMLOutputter xmlOutputter;

    private String currentContext = "";

    public DataSourceConverter2( final DataSourceConverterLogger logger )
    {
        this.logger = logger;
        this.converters = new DataSourceMethodConverters();
        this.xmlOutputter = new XMLOutputter( Format.getCompactFormat() );
    }

    @Override
    public void setCurrentContext( final String context )
    {
        this.currentContext = context;
    }

    @Override
    public Element convert( final Element root )
        throws Exception
    {
        final Element result = new Element( root.getName() );
        copyAttributeIfExists( root, result, "result-element" );
        convertContext( root, result );

        for ( final Element child : findElements( root, "datasource" ) )
        {
            convertMethod( child, result );
        }

        return result;
    }

    private MethodElementBuilder method( final String name )
    {
        return new MethodElementBuilder( name );
    }

    private void convertContext( final Element source, final Element target )
    {
        if ( "true".equals( source.getAttributeValue( "cookie-context" ) ) )
        {
            target.addContent( method( "getCookieContext" ).resultElement( "context" ).build() );
        }

        if ( "true".equals( source.getAttributeValue( "http-context" ) ) )
        {
            target.addContent( method( "getHttpContext" ).resultElement( "context" ).build() );
        }

        if ( "true".equals( source.getAttributeValue( "session-context" ) ) )
        {
            target.addContent( method( "getSessionContext" ).resultElement( "context" ).build() );
        }
    }

    private String[] findParameterValues( final Element source )
    {
        final List<String> list = Lists.newArrayList();
        for ( final Element child : findElements( source, "parameter" ) )
        {
            list.add( getTextNode( child ) );
        }

        return Iterables.toArray( list, String.class );
    }

    private void convertMethod( final Element source, final Element result )
    {
        final String name = source.getAttributeValue( "name" );
        final String[] params = findParameterValues( source );

        final Element target = createMethod( name, params );

        if ( target == null )
        {
            result.addContent( createComment( name, params, source ) );
            return;
        }

        copyAttributeIfExists( source, target, "result-element" );
        copyAttributeIfExists( source, target, "cache" );
        copyAttributeIfExists( source, target, "condition" );
        result.addContent( target );
    }

    private Element createMethod( final String name, final String[] params )
    {
        final DataSourceMethodConverter converter = this.converters.get( name );
        if ( converter == null )
        {
            return createExtension( name, params );
        }

        return converter.convert( params );
    }

    private Comment createComment( final String name, final String[] params, final Element elem )
    {
        if ( this.logger != null )
        {
            this.logger.logWarning(  currentContext + " : method name[" + name + "]with[" + params.length +
                                    "] parameters not found. Commenting out." );
        }

        return new Comment( this.xmlOutputter.outputString( elem ) );
    }

    private Element createExtension( final String name, final String[] params )
    {
        if ( !name.contains( "." ) )
        {
            return null;
        }

        final MethodElementBuilder builder = method( "invokeExtension" );
        builder.param( "name", name );

        for ( int i = 0; i < params.length; i++ )
        {
            builder.param( "param" + ( i + 1 ), params[i] );
        }

        return builder.build();
    }
}
