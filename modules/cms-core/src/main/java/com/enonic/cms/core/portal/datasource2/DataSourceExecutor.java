package com.enonic.cms.core.portal.datasource2;

import org.jdom.Document;

public interface DataSourceExecutor
{
    public Document execute( final Document input )
        throws DataSourceException;
}
