/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.context.DatasourcesContextXmlCreator;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;

/**
 * Apr 20, 2009
 */
@Component
public class DataSourceExecutorFactory
{
    @Autowired
    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    public DataSourceExecutor createDatasourceExecutor( DataSourceExecutorContext datasourceExecutorContext )
    {
        DataSourceExecutor dataSourceExecutor = new DataSourceExecutor( datasourceExecutorContext );
        dataSourceExecutor.setDatasourcesContextXmlCreator( datasourcesContextXmlCreator );
        dataSourceExecutor.setLivePortalTraceService( livePortalTraceService );
        return dataSourceExecutor;
    }
}
