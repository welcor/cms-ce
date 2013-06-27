/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task.datasource;

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

    public String convert( final DatasourceInfoHolder dataSource )
        throws Exception
    {
        final Document sourceDoc = JDOMUtil.parseDocument( dataSource.getXml() );
        convertDoc( sourceDoc, dataSource );
        return JDOMUtil.prettyPrintDocument( sourceDoc );
    }

    private void convertDoc( final Document doc, DatasourceInfoHolder datasourceInfoHolder )
        throws Exception
    {
        final Element root = doc.getRootElement();

        Element originalElem = JDOMDocumentHelper.findElement( root, "datasources" );
        if ( originalElem == null )
        {
            return;
        }

        originalElem = (Element) originalElem.detach();

        this.converter.setCurrentContext( datasourceInfoHolder.getContextString() );
        root.addContent( this.converter.convert( originalElem ) );
    }
}
