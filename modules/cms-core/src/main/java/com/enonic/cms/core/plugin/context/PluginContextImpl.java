package com.enonic.cms.core.plugin.context;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.host.HostServices;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class PluginContextImpl
    implements PluginContext
{
    private final Bundle bundle;

    private final BundleContext context;

    private HostServices hostServices;

    private PluginConfig config;

    public PluginContextImpl( final Bundle bundle )
    {
        this.bundle = bundle;
        this.context = this.bundle.getBundleContext();
    }

    public String getId()
    {
        return this.bundle.getSymbolicName();
    }

    public String getName()
    {
        return OsgiHelper.getBundleName( this.bundle );
    }

    public String getVersion()
    {
        return this.bundle.getVersion().toString();
    }

    public PluginConfig getConfig()
    {
        return this.config;
    }

    public void register( final Extension ext )
    {
        this.context.registerService( Extension.class.getName(), ext, null );
    }

    public Map<String, Object> getServices()
    {
        return this.hostServices.getServiceMap();
    }

    public void setConfig( final PluginConfig config )
    {
        this.config = config;
    }

    public void setHostServices( final HostServices hostServices )
    {
        this.hostServices = hostServices;
    }
}
