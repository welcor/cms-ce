/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.context.DataSourcesContextXmlCreator;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;

@Component
public final class DataSourceExecutorFactory
{
    private DataSourcesContextXmlCreator dataSourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    private String defaultDataSourceRootElementName;

    private DataSourceInvoker dataSourceInvoker;

    public DataSourceExecutor createDataSourceExecutor( final DataSourceExecutorContext context )
    {
        final DataSourceExecutorImpl dataSourceExecutor = new DataSourceExecutorImpl( context );
        dataSourceExecutor.setDataSourcesContextXmlCreator( dataSourcesContextXmlCreator );
        dataSourceExecutor.setLivePortalTraceService( livePortalTraceService );
        dataSourceExecutor.setDefaultResultRootElementName( this.defaultDataSourceRootElementName );
        dataSourceExecutor.setInvoker( this.dataSourceInvoker );
        return dataSourceExecutor;
    }

    @Autowired
    public void setDataSourcesContextXmlCreator( final DataSourcesContextXmlCreator dataSourcesContextXmlCreator )
    {
        this.dataSourcesContextXmlCreator = dataSourcesContextXmlCreator;
    }

    @Autowired
    public void setLivePortalTraceService( final LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    @Autowired
    public void setDataSourceInvoker( final DataSourceInvoker dataSourceInvoker )
    {
        this.dataSourceInvoker = dataSourceInvoker;
    }

    @Value("${cms.datasource.defaultResultRootElement}")
    public void setDefaultDataSourceRootElementName( final String defaultDataSourceRootElementName )
    {
        this.defaultDataSourceRootElementName = defaultDataSourceRootElementName;
    }
}
