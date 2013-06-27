/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.plugin.config.ConfigFactory;
import com.enonic.cms.core.plugin.host.HostServices;

@Component
public final class ContextFactoryImpl
    implements ContextFactory, ServiceFactory
{
    private ConfigFactory configFactory;

    private HostServices hostServices;

    public void register( final BundleContext context )
    {
        context.registerService( PluginContext.class.getName(), this, null );
    }

    public Object getService( final Bundle bundle, final ServiceRegistration reg )
    {
        final PluginContextImpl context = new PluginContextImpl( bundle );
        context.setConfig( this.configFactory.create( bundle ) );
        context.setHostServices( this.hostServices );
        return context;
    }

    public void ungetService( final Bundle bundle, final ServiceRegistration reg, final Object o )
    {
        // Do nothing
    }

    @Autowired
    public void setConfigFactory( final ConfigFactory configFactory )
    {
        this.configFactory = configFactory;
    }

    @Autowired
    public void setHostServices( final HostServices hostServices )
    {
        this.hostServices = hostServices;
    }
}
