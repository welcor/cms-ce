package com.enonic.cms.core.portal.datasource2.handler;

import org.jdom.Document;

public abstract class DataSourceHandler
{
    private final String name;

    public DataSourceHandler( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract Document handle( final DataSourceRequest req )
        throws Exception;
}
