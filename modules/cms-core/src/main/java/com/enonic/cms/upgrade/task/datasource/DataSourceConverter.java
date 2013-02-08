package com.enonic.cms.upgrade.task.datasource;

import org.jdom.Element;

public abstract class DataSourceConverter
{
    protected final DataSourceConverterLogger logger;

    protected String currentContext = "";

    public DataSourceConverter( final DataSourceConverterLogger logger )
    {
        this.logger = logger != null ? logger : new NopDataSourceConverterLogger();
    }

    public final void setCurrentContext( String context )
    {
        this.currentContext = context;
    }

    public abstract Element convert( Element root )
        throws Exception;
}
