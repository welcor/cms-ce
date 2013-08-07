/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.portal.datasource.context.DataSourcesContextXmlCreator;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesService;

@Component
public final class DataSourceExecutorFactory
{
    private DataSourcesContextXmlCreator dataSourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    private String defaultDataSourceRootElementName;

    private DataSourceInvoker dataSourceInvoker;

    private ConfigProperties cmsProperties;

    private SitePropertiesService sitePropertiesService;

    public DataSourceExecutor createDataSourceExecutor( final DataSourceExecutorContext context )
    {
        context.setRootProperties( this.cmsProperties );

        if ( context.getSiteProperties() == null )
        {
            final SiteKey key = context.getSite().getKey();
            final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( key );
            context.setSiteProperties( siteProperties );
        }

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

    @Autowired
    public void setCmsProperties( final ConfigProperties cmsProperties )
    {
        this.cmsProperties = cmsProperties;
    }

    @Autowired
    public void setSitePropertiesService( final SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    @Value("${cms.datasource.defaultResultRootElement}")
    public void setDefaultDataSourceRootElementName( final String defaultDataSourceRootElementName )
    {
        this.defaultDataSourceRootElementName = defaultDataSourceRootElementName;
    }
}
