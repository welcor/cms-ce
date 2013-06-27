/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.container;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;

public final class DynamicHookConfigurator
    implements HookConfigurator
{
    public void addHooks( final HookRegistry registry )
    {
        HookConfiguratorAccessor.getInstance().get().addHooks( registry );
    }
}
