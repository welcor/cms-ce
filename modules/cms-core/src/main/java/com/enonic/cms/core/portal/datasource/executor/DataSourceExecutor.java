/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.core.portal.datasource.xml.DataSourcesElement;

public interface DataSourceExecutor
{
    public XMLDocument execute( final DataSourcesElement element );
}
