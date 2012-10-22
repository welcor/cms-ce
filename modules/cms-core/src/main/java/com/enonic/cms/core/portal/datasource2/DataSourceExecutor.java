package com.enonic.cms.core.portal.datasource2;

import org.jdom.Document;

public interface DataSourceExecutor
{
    public DataSourceExecutor input( Document value );

    public Document execute();
}
