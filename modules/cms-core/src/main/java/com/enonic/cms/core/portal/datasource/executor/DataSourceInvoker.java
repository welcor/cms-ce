/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.executor;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public interface DataSourceInvoker
{
    public Document execute( final DataSourceRequest req )
        throws DataSourceException;
}
