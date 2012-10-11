package com.enonic.cms.core.portal.datasource2.xml;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.portal.datasource2.DataSourceException;

public final class DataSourceXmlParser
{
    public DataSourcesElement parse( final Document doc )
    {
        final Element root = doc.getRootElement();
        if ( root.getName().equals( "data-sources" ) )
        {
            return new DataSourcesElement( root );
        }

        throw new DataSourceException( "Data source document must start with data-sources element" );
    }
}
