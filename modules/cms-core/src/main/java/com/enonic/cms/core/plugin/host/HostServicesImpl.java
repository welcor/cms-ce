package com.enonic.cms.core.plugin.host;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.cms.api.client.LocalClient;

@Component
public final class HostServicesImpl
    implements HostServices
{
    private final Map<String, Object> serviceMap;

    public HostServicesImpl()
    {
        this.serviceMap = Maps.newHashMap();
        register( "pluginEnvironment", new PluginEnvironmentImpl() );
    }

    private void register( final String name, final Object service )
    {
        this.serviceMap.put( name, service );
    }

    public Map<String, Object> getServiceMap()
    {
        return ImmutableMap.copyOf( this.serviceMap );
    }

    @Autowired
    @Qualifier("localClient")
    public void setClient(final LocalClient client)
    {
        register( "client", client );
    }
}
