package com.enonic.cms.core.plugin.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.util.LogFacade;

@Component
public final class ConfigFactoryImpl
    implements ConfigFactory
{
    private final static LogFacade LOG = LogFacade.get( ConfigFactoryImpl.class );

    private File configDir;

    private Map<String, String> globalProperties;

    @Value("#{config.pluginConfigDir}")
    public void setConfigDir( final File configDir )
    {
        this.configDir = configDir;
    }

    public PluginConfig create( final Bundle bundle )
    {
        final File file = getConfigFile( bundle );

        final Map<String, String> config = new HashMap<String, String>();
        config.putAll( PluginConfigHelper.loadDefaultProperties( bundle ) );
        config.putAll( PluginConfigHelper.loadProperties( file ) );

        if ( file.exists() )
        {
            LOG.info( "Loaded configuration for bundle [{0}] from [{1}]", bundle.getSymbolicName(), file.getAbsolutePath() );
        }

        return new PluginConfigImpl( PluginConfigHelper.interpolate( this.globalProperties, config ) );
    }

    private File getConfigFile( final Bundle bundle )
    {
        final String id = getConfigurationId( bundle );
        return new File( this.configDir, id + ".properties" );
    }

    private String getConfigurationId( final Bundle bundle )
    {
        return bundle.getSymbolicName();
    }

    @Value("#{config.map}")
    public final void setGlobalProperties( final Map<String, String> properties )
    {
        this.globalProperties = properties;
    }
}
