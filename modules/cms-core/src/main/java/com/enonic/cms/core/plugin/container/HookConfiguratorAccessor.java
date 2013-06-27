/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.container;

import org.eclipse.osgi.baseadaptor.HookConfigurator;

final class HookConfiguratorAccessor
{
    private final static HookConfiguratorAccessor INSTANCE = new HookConfiguratorAccessor();

    private HookConfigurator configurator;

    private HookConfiguratorAccessor()
    {
    }

    public HookConfigurator get()
    {
        return this.configurator;
    }

    public void set( final HookConfigurator configurator )
    {
        this.configurator = configurator;
    }

    public static HookConfiguratorAccessor getInstance()
    {
        return INSTANCE;
    }
}
