package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private final ConfigProperties props;

    public GlobalConfigImpl( final ConfigProperties props )
    {
        this.props = props;
    }

    public File getHomeDir()
    {
        return this.props.getFile( "cms.home" );
    }

    public File getConfigDir()
    {
        return new File( getHomeDir(), "config" );
    }

    public String getJdbcDialect()
    {
        return this.props.getString( "cms.jdbc.dialect" );
    }

    public File getPluginConfigDir()
    {
        return this.props.getFile( "cms.plugin.configDir" );
    }

    public File getPluginDeployDir()
    {
        return this.props.getFile( "cms.plugin.deployDir" );
    }

    public long getPluginScanPeriod()
    {
        return this.props.getLong( "cms.plugin.scanPeriod" );
    }

    public File getCountriesFile()
    {
        return new File( getConfigDir(), "countries.xml" );
    }

    public File getVirtualHostConfigFile()
    {
        return new File( getConfigDir(), "vhost.properties" );
    }

    public boolean getJdbcLogging()
    {
        return this.props.getBoolean( "cms.jdbc.logging" );
    }

    public boolean getJdbcConnectionTrace()
    {
        return this.props.getBoolean( "cms.jdbc.connectionTrace" );
    }

    public int getTxDefaultTimeout()
    {
        return this.props.getInteger( "cms.tx.defaultTimeout" );
    }

    public File getBlobStoreDir()
    {
        return this.props.getFile( "cms.blobstore.dir" );
    }

    public int getAutoLoginTimeout()
    {
        return this.props.getInteger( "com.enonic.vertical.presentation.autologinTimeout" );
    }

    public String getAdminDefaultLanguage()
    {
        return this.props.getString( "cms.admin.defaultLanguage" );
    }

    public String getMainSmtpHost()
    {
        return this.props.getString( "cms.mail.smtpHost" );
    }

    public String getAdminEmail()
    {
        return this.props.getString( "cms.admin.email" );
    }

    public String getAdminPassword()
    {
        return this.props.getString( "cms.admin.password" );
    }

    public boolean getLivePortalTraceEnabled()
    {
        return this.props.getBoolean( "cms.livePortalTrace.enabled" );
    }

    public int getLivePortalTraceLongestSize()
    {
        return this.props.getInteger( "cms.livePortalTrace.longest.size" );
    }

    public int getLivePortalTraceHistorySize()
    {
        return this.props.getInteger( "cms.livePortalTrace.history.size" );
    }

    public boolean isSchedulerEnabled()
    {
        return this.props.getBoolean( "cms.scheduler.enabled" );
    }

    public int getSchedulerTxTimeout()
    {
        return this.props.getInteger( "cms.scheduler.tx.timeout" );
    }

    public File getElasticStorageDir()
    {
        return this.props.getFile( "cms.search.index.dir" );
    }

    public Map<String, String> getMap()
    {
        return this.props.getMap();
    }

    public Properties getProperties()
    {
        return this.props.getProperties();
    }
}
