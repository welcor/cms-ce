/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePropertiesServiceImpl;
import com.enonic.cms.core.vhost.VirtualHostResolver;

@Service
public class ConfigFilesWatcherService
{
    private static final Logger LOG = LoggerFactory.getLogger( ConfigFilesWatcherService.class );

    public static final String SITE_PROPERTIES_FILENAME = "site-(\\d+).properties";

    public static final String[] EMPTY_DIR = {};

    @Autowired
    private VirtualHostResolver virtualHostResolver;

    @Autowired
    private SitePropertiesServiceImpl sitePropertiesService;

    private File configDir;

    private boolean configDirObserved;

    private Map<String, Long> lastModified;

    public ConfigFilesWatcherService()
    {
        lastModified = new HashMap<String, Long>();
    }

    @PostConstruct
    public void initService()
    {
        configDirObserved = configDir.exists();

        if ( configDirObserved )
        {
            final String[] filenames = configDir.list();
            resolveLastModified( filenames );
        }
    }

    private String[] listConfigFiles()
    {
        final String[] filenames = configDir.list();

        checkConfigDirObserved( filenames );

        return filenames != null ? filenames : EMPTY_DIR;
    }

    private void checkConfigDirObserved( final String[] filenames )
    {
        if ( filenames != null && !configDirObserved )
        {
            resolveLastModified( filenames );

            markObservedConfigFilesAsModified();

            LOG.debug( "config directory was created." );
        }
        else if ( filenames == null && configDirObserved )
        {
            markObservedConfigFilesAsModified();

            LOG.debug( "config directory was removed." );
        }

        configDirObserved = filenames != null;
    }

    private void markObservedConfigFilesAsModified()
    {
        for ( final Map.Entry<String, Long> entry : lastModified.entrySet() )
        {
            entry.setValue( 0L );
        }
    }

    private void resolveLastModified( final String[] filenames )
    {
        lastModified.clear();

        for ( final String filename : filenames )
        {
            if ( isObservableFile( filename ) )
            {
                final File file = new File( configDir.getAbsoluteFile() + File.separator + filename );
                lastModified.put( filename, file.lastModified() );
            }
        }
    }

    private boolean isObservableFile( final String filename )
    {
        return filename.equals( "vhost.properties" ) || filename.matches( SITE_PROPERTIES_FILENAME ) ||
            filename.equals( "cms.properties" );
    }

    /**
     * used spring scheduler. Runs every 2 sec.
     * <p/>
     * monitors cms.properties, vhost.properties and site-*.properties
     */
    @Scheduled(fixedRate = 2000)
    private void checkConfigFiles()
    {
        final String[] dir = listConfigFiles();

        final Set<String> filenames = new HashSet<String>();

        filenames.addAll( Arrays.asList( dir ) );
        filenames.addAll( lastModified.keySet() );

        for ( final String filename : filenames )
        {
            if ( !isObservableFile( filename ) || !isFileModified( filename ) )
            {
                continue;
            }

            if ( filename.equals( "vhost.properties" ) )
            {
                virtualHostResolver.configureVirtualHosts();

                LOG.info( "Reloaded vhost configuration." );
            }
            else if ( filename.matches( SITE_PROPERTIES_FILENAME ) )
            {
                final Pattern pattern = Pattern.compile( SITE_PROPERTIES_FILENAME );
                final Matcher matcher = pattern.matcher( filename );

                if ( matcher.matches() )
                {
                    final SiteKey site = new SiteKey( matcher.group( 1 ) );

                    sitePropertiesService.reloadSiteProperties( site );

                    LOG.info( "Reloaded configuration for site #{}.", site );
                }
            }
            else if ( filename.equals( "cms.properties" ) )
            {
                LOG.info( "{} was changed. Please restart the application to load new values.", filename );
            }
        }
    }

    private boolean isFileModified( final String filename )
    {
        final File file = new File( configDir.getAbsoluteFile() + File.separator + filename );

        if ( !file.exists() )
        {
            // config file was removed
            lastModified.remove( filename );
            return true;
        }

        final long lastModifiedNow = file.lastModified();
        final Long lastModifiedPrev = lastModified.put( filename, lastModifiedNow );
        return lastModifiedPrev == null || lastModifiedNow > lastModifiedPrev; // new file or changed
    }

    @Value("${cms.home}/config")
    public void setConfigDir( final File configDir )
    {
        this.configDir = configDir;
    }
}
