package com.enonic.cms.core.plugin.container;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.internal.adaptor.EclipseLogHook;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;
import org.eclipse.osgi.internal.baseadaptor.BaseHookConfigurator;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.cms.core.plugin.PluginManager;

public abstract class OsgiContainer
    implements Constants, PluginManager
{
    private Framework framework;

    private BundleInstaller bundleInstaller;

    private File tmpDir;

    private Map<String, String> createConfigMap()
        throws Exception
    {
        final Map<String, String> map = Maps.newHashMap();

        map.put( FRAMEWORK_STORAGE, this.tmpDir.getAbsolutePath() );
        map.put( FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );
        map.put( HookRegistry.PROP_HOOK_CONFIGURATORS, BaseHookConfigurator.class.getName() +
            "," + DynamicHookConfigurator.class.getName() );
        map.put( HookRegistry.PROP_HOOK_CONFIGURATORS_EXCLUDE, EclipseLogHook.class.getName() );

        final File installArea = new File( this.tmpDir, "__install" );
        installArea.mkdirs();

        map.put( "osgi.install.area", installArea.toURI().toString() );

        final File frameworkJar = new File( installArea, "framework.jar" );
        map.put( "osgi.framework", frameworkJar.toURI().toString() );

        copyFrameworkJar( frameworkJar );
        return map;
    }

    @PostConstruct
    public final void start()
        throws Exception
    {
        this.tmpDir = Files.createTempDir();

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
        FileUtils.deleteDirectory( this.tmpDir );
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

    private void copyFrameworkJar( final File targetFile )
        throws Exception
    {
        final URL location = FrameworkProperties.class.getProtectionDomain().getCodeSource().getLocation();
        Files.copy( Resources.newInputStreamSupplier(location), targetFile );
    }
}
