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

    @Autowired
    private VirtualHostResolver virtualHostResolver;

    @Autowired
    private SitePropertiesServiceImpl sitePropertiesService;

    private File configDir;

    private Map<String, Long> lastModified;

    public ConfigFilesWatcherService()
    {
        lastModified = new HashMap<String, Long>();
    }

    @PostConstruct
    public void initService()
    {
        final String[] filenames = configDir.list();

        for ( final String filename : filenames )
        {
            if ( filename.equals( "vhost.properties" ) || filename.matches( SITE_PROPERTIES_FILENAME ) ||
                filename.equals( "cms.properties" ) )
            {
                final File file = new File( configDir.getAbsoluteFile() + File.separator + filename );
                lastModified.put( filename, file.lastModified() );
            }
        }
    }

    /**
     * used spring scheduler. Runs every 2 sec.
     * <p/>
     * monitors cms.properties, vhost.properties and site-*.properties
     */
    @Scheduled(fixedRate = 2000)
    private void checkConfigDirectory()
    {
        final String[] dir = configDir.list();

        final Set<String> filenames = new HashSet<String>();

        filenames.addAll( Arrays.asList( dir ) );
        filenames.addAll( lastModified.keySet() );

        for ( final String filename : filenames )
        {
            if ( !filename.endsWith( ".properties" ) )
            {
                continue;
            }

            if ( filename.equals( "vhost.properties" ) && isFileModified( filename ) )
            {
                virtualHostResolver.configureVirtualHosts();

                LOG.info( "Reloaded vhost configuration." );
            }
            else if ( filename.matches( SITE_PROPERTIES_FILENAME ) && isFileModified( filename ) )
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
            else if ( filename.equals( "cms.properties" ) && isFileModified( filename ) )
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
