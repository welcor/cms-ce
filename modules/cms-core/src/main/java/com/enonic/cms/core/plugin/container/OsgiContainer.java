package com.enonic.cms.core.plugin.container;

import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.internal.adaptor.EclipseLogHook;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public abstract class OsgiContainer
    implements Constants
{
    private Framework framework;

    private BundleInstaller bundleInstaller;

    private Map<String, String> createConfigMap()
    {
        final Map<String, String> map = Maps.newHashMap();

        map.put( FRAMEWORK_STORAGE, Files.createTempDir().getAbsolutePath() );
        map.put( FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );
        map.put( HookRegistry.PROP_HOOK_CONFIGURATORS_INCLUDE, DynamicHookConfigurator.class.getName() );
        map.put( HookRegistry.PROP_HOOK_CONFIGURATORS_EXCLUDE, EclipseLogHook.class.getName() );

        return map;
    }

    @PostConstruct
    public final void start()
        throws Exception
    {
        this.framework = new EquinoxFactory().newFramework( createConfigMap() );
        this.framework.start();
        this.bundleInstaller = new BundleInstaller( this.framework.getBundleContext() );
        start( this.framework.getBundleContext() );
    }

    @PreDestroy
    public final void stop()
        throws Exception
    {
        this.framework.stop();
        this.framework.waitForStop( 5000 );
    }

    protected abstract void start( BundleContext context )
        throws Exception;

    @Autowired
    public final void setHookConfigurator( final HookConfigurator configurator )
    {
        HookConfiguratorAccessor.getInstance().set( configurator );
    }

    public final void install( final File file )
    {
        this.bundleInstaller.install( file );
    }

    public final void uninstall( final File file )
    {
        this.bundleInstaller.uninstall( file );
    }
}
