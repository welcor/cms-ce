/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.plugin.container;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.eclipse.core.runtime.internal.adaptor.EclipseLogHook;
import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;
import org.eclipse.osgi.internal.baseadaptor.BaseHookConfigurator;
import org.eclipse.osgi.launch.EquinoxFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.cms.core.plugin.PluginManager;

public abstract class OsgiContainer
    implements Constants, PluginManager
{
    private final static Logger LOG = LoggerFactory.getLogger( OsgiContainer.class );

    private static final String VFS_CONTENTS_FOLDER = "contents";

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
        URL location = FrameworkProperties.class.getProtectionDomain().getCodeSource().getLocation();

        final String locationFile = location.getFile();

        LOG.info( "Location of framework.jar : " +  locationFile );

        if ( locationFile.endsWith( ".jar!/" ) ) // for IBM Websphere 8.5 Liberty Profile
        {
            String absolutePath = locationFile.substring( 0, locationFile.length() - 2 );

            location = new URL( absolutePath );
        }

        else

        if ( ResourceUtils.URL_PROTOCOL_VFS.equals( location.getProtocol() ) ) // JBOSS 7.1.1 VFS
        {
            final URI uri = ResourceUtils.toURI( location );
            final UrlResource urlResource = new UrlResource( uri );
            final File file = urlResource.getFile();

            String absolutePath = file.getAbsolutePath();

            if ( !absolutePath.endsWith( urlResource.getFilename() ) )
            {
                // removing /contents folder from path and adding unpacked jar to path.
                absolutePath =
                    absolutePath.substring( 0, absolutePath.length() - VFS_CONTENTS_FOLDER.length() ) + urlResource.getFilename();
            }

            final StringBuilder stringBuilder = new StringBuilder( "file:/" );
            if ( !SystemUtils.IS_OS_WINDOWS ) // windows already has one slash in path like /c:/Program Files/....
            {
                stringBuilder.append( '/' );
            }
            stringBuilder.append( absolutePath );

            location = new URL( stringBuilder.toString() );
        }

        LOG.info( "Copying " +  location.toString()  + " to " + targetFile.toString() );
        Files.copy( Resources.newInputStreamSupplier( location ), targetFile );
    }
}
