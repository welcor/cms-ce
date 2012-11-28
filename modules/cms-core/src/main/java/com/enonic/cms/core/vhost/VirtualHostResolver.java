/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * This class implements the virtual host resolver. This should be an interface if
 * multiple implementations will be added.
 */
@Component
public final class VirtualHostResolver
    implements InitializingBean
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostResolver.class );

    /**
     * List of virtual hosts.
     */
    private volatile AtomicReference<ArrayList<VirtualHost>> virtualHosts = new AtomicReference<ArrayList<VirtualHost>>();

    private File configFile;

    /**
     * Initializes the resolver.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        this.virtualHosts.set( new ArrayList<VirtualHost>() );

        configureVirtualHosts();

        watchConfigFile();
    }

    private void watchConfigFile()
    {
        final Runnable configFileChangedHandler = new Runnable()
        {
            @Override
            public void run()
            {
                configureVirtualHosts();
            }
        };

        // after switching to java 7 use NIO.2 file watcher instead of timer
        new FileWatcherByTimer( configFile, configFileChangedHandler, 2000 );
    }

    /**
     * Configure property.
     */
    private void configureVirtualHosts()
    {
        if ( this.configFile != null && this.configFile.exists() )
        {
            try
            {
                final ArrayList<VirtualHost> virtualHosts = new ArrayList<VirtualHost>();

                final Properties properties = PropertiesLoaderUtils.loadProperties( new FileSystemResource( this.configFile ) );

                for ( final Object key : properties.keySet() )
                {
                    final String pattern = key.toString();
                    final String targetPath = properties.getProperty( pattern );
                    addVirtualHost( virtualHosts, pattern, targetPath );
                }

                // strange sort, that may introduce problems.
                Collections.sort( virtualHosts );

                this.virtualHosts.lazySet( virtualHosts );

                LOG.info( "loaded virtual hosts configuration. {} rule(s) found.", properties.size() );
            }
            catch ( Exception e )
            {
                LOG.error( "cannot configure virtual hosts !", e );
            }
        }
    }

    /**
     * Resolve the virtual host. Returns null if no virtual host is found.
     */
    public VirtualHost resolve( HttpServletRequest req )
    {
        final ArrayList<VirtualHost> virtualHosts = this.virtualHosts.get();

        for ( final VirtualHost virtualHost : virtualHosts )
        {
            if ( virtualHost.matches( req ) )
            {
                return virtualHost;
            }
        }

        return null;
    }


    private void addVirtualHost( ArrayList<VirtualHost> virtualHosts, String pattern, String targetPath )
    {
        pattern = pattern.trim();
        if ( !pattern.equals( "" ) )
        {
            try
            {
                virtualHosts.add( new VirtualHost( pattern, targetPath.trim() ) );
            }
            catch ( InvalidVirtualHostPatternException e )
            {
                LOG.warn( e.getMessage() );
            }
        }

    }

    /**
     * Add the virtual host.
     */
    public void addVirtualHost( String pattern, String targetPath )
    {
        addVirtualHost( this.virtualHosts.get(), pattern, targetPath );
    }

    @Value("${cms.home}/config/vhost.properties")
    public void setConfigFile( final File file )
    {
        this.configFile = file;
    }
}
