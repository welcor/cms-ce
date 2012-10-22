package com.enonic.cms.core.portal.datasource2.cache;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.xml.DataSourceElement;

public interface InvocationCache
{
    public Document get( DataSourceElement elem );

    public void put( DataSourceElement elem, Document doc );
}
