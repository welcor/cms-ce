package com.enonic.cms.core.portal.datasource2.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.enonic.cms.core.portal.datasource2.DataSourceExecutor;
import com.enonic.cms.core.portal.datasource2.DataSourceExecutorFactory;
import com.enonic.cms.core.portal.datasource2.cache.InvocationCacheImpl;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceInvoker;

public final class DataSourceExecutorFactoryImpl
    implements DataSourceExecutorFactory
{
    private String defaultResultRoot;

    private DataSourceInvoker dataSourceInvoker;

    @Override
    public DataSourceExecutor create()
    {
        final DataSourceExecutorImpl executor = new DataSourceExecutorImpl();
        executor.setDefaultResultRoot( this.defaultResultRoot );
        executor.setInvocationCache( new InvocationCacheImpl() );
        executor.setInvoker( this.dataSourceInvoker );

        return executor;
    }

    @Value("${cms.datasource.defaultResultRootElement}")
    public void setDefaultResultRoot( final String defaultResultRoot )
    {
        this.defaultResultRoot = defaultResultRoot;
    }

    @Autowired
    public void setDataSourceInvoker( final DataSourceInvoker dataSourceInvoker )
    {
        this.dataSourceInvoker = dataSourceInvoker;
    }
}
