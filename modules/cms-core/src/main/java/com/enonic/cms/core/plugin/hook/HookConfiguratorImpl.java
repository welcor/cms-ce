package com.enonic.cms.core.plugin.hook;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.springframework.stereotype.Component;

@Component
public final class HookConfiguratorImpl
    implements HookConfigurator
{
    public void addHooks( final HookRegistry registry )
    {
        registry.addAdaptorHook( new LoggingHook() );
        registry.addBundleFileWrapperFactoryHook( new TransformerHook() );
        registry.addClassLoaderDelegateHook( new ParentClassLoaderHook() );
    }
}
