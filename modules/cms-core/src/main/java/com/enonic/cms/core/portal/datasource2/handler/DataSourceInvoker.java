package com.enonic.cms.core.portal.datasource2.handler;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public interface DataSourceInvoker
{
    public Document execute( final DataSourceRequest req )
        throws DataSourceException;
}
