package com.enonic.cms.core.portal.datasource2;

public interface DataSourceExecutorFactory
{
    public DataSourceExecutor create( DataSourceExecutorContext context );
}
