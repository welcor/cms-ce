package com.enonic.cms.upgrade.task.datasource;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

public final class DataSourceConverterHelper
{
    private final DataSourceConverter converter;

    public DataSourceConverterHelper( final DataSourceConverter converter )
    {
        this.converter = converter;
    }

    public String convert( final String xml )
        throws Exception
    {
        final Document sourceDoc = JDOMUtil.parseDocument( xml );
        convertDoc( sourceDoc );
        return JDOMUtil.prettyPrintDocument( sourceDoc );
    }

    private void convertDoc( final Document doc )
        throws Exception
    {
        final Element root = doc.getRootElement();

        Element originalElem = JDOMDocumentHelper.findElement( root, "datasources" );
        if ( originalElem == null )
        {
            return;
        }

        originalElem = (Element) originalElem.detach();
        root.addContent( this.converter.convert( originalElem ) );
    }
}
