package com.enonic.cms.core.plugin.context;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class ContextActivator
    extends OsgiContributor
{
    private final static LogFacade LOG = LogFacade.get( ContextActivator.class );

    private final PluginContextFactory contextFactory;

    private final PluginConfigFactory configFactory;

    public ContextActivator()
    {
        super( 1 );
        this.contextFactory = new PluginContextFactory();
        this.configFactory = new PluginConfigFactory();

        this.contextFactory.registerService( "pluginEnvironment", new PluginEnvironmentImpl() );
    }

    @Value("#{config.pluginConfigDir}")
    public void setConfigDir( final File configDir )
    {
        LOG.info( "Plugin configuration is loaded from [{0}]", configDir.getAbsolutePath() );
        this.configFactory.setConfigDir( configDir );
    }

    public void start( final BundleContext context )
        throws Exception
    {

        context.registerService( PluginConfig.class.getName(), this.configFactory, null );
        context.registerService( PluginContext.class.getName(), this.contextFactory, null );
    }

    @Autowired
    @Qualifier("localClient")
    public void setClient(final LocalClient client)
    {
        this.contextFactory.registerService( "client", client );
    }
}
