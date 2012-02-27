package com.enonic.cms.core.plugin.context;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Maps;

final class PluginContextFactory
    implements ServiceFactory
{
    private final Map<String, Object> serviceMap;

    public PluginContextFactory()
    {
        this.serviceMap = Maps.newHashMap();
    }

    public void registerService( final String name, final Object service )
    {
        this.serviceMap.put( name, service );
    }

    public Object getService( final Bundle bundle, final ServiceRegistration reg )
    {
        return new PluginContextImpl( bundle, this.serviceMap );
    }

    public void ungetService( final Bundle bundle, final ServiceRegistration reg, final Object o )
    {
        // Do nothing
    }
}
