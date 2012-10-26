/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.context.DataSourcesContextXmlCreator;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;

@Component
public final class DataSourceExecutorFactory
{
    private DataSourcesContextXmlCreator dataSourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    public DataSourceExecutor createDataSourceExecutor( final DataSourceExecutorContext datasourceExecutorContext )
    {
        DataSourceExecutor dataSourceExecutor = new DataSourceExecutor( datasourceExecutorContext );
        dataSourceExecutor.setDataSourcesContextXmlCreator( dataSourcesContextXmlCreator );
        dataSourceExecutor.setLivePortalTraceService( livePortalTraceService );
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
}
