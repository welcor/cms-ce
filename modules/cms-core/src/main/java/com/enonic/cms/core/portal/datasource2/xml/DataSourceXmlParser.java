package com.enonic.cms.core.portal.datasource2.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.enonic.cms.core.portal.datasource2.DataSourceException;

public final class DataSourceXmlParser
{
    private final XMLOutputter xmlOutputter;

    public DataSourceXmlParser()
    {
        this.xmlOutputter = new XMLOutputter( Format.getCompactFormat() );
    }

    public DataSourcesElement parse( final Document doc )
    {
        final Element root = doc.getRootElement();
        if ( root.getName().equals( "data-sources" ) )
        {
            return parseDataSources( root );
        }

        throw new DataSourceException( "Data source document must start with data-sources element" );
    }

    private DataSourcesElement parseDataSources( final Element root )
    {
        final DataSourcesElement result = new DataSourcesElement();
        result.setResultElement( root.getAttributeValue( "result-element" ) );
        result.setHttpContext( "true".equals( root.getAttributeValue( "http-context" ) ) );
        result.setCookieContext( "true".equals( root.getAttributeValue( "cookie-context" ) ) );
        result.setSessionContext( "true".equals( root.getAttributeValue( "session-context" ) ) );

        for ( final Object o : root.getChildren( "data-source" ) )
        {
            result.add( parseDataSource( (Element) o ) );
        }

        return result;
    }

    private DataSourceElement parseDataSource( final Element root )
    {
        final DataSourceElement result = new DataSourceElement( root.getAttributeValue( "name" ) );
        result.setCache( "true".equals( root.getAttributeValue( "cache" ) ) );
        result.setResultElement( root.getAttributeValue( "result-element" ) );
        result.setCondition( root.getAttributeValue( "condition" ) );

        for ( final Object o : root.getChildren( "parameter" ) )
        {
            result.add( parseParameter( (Element) o ) );
        }

        return result;
    }

    private ParameterElement parseParameter( final Element root )
    {
        final ParameterElement result = new ParameterElement( root.getAttributeValue( "name" ) );
        result.setValue( this.xmlOutputter.outputString( root.getContent() ) );
        return result;
    }
}
