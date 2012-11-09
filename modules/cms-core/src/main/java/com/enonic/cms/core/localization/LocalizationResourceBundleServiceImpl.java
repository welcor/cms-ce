/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SiteEntity;

@Component
public final class LocalizationResourceBundleServiceImpl
    implements LocalizationResourceBundleService, InitializingBean
{
    private ResourceService resourceService;

    private CacheFacade cacheFacade;

    private long checkInterval = 5000;

    private LocalizationPropertiesCache propertiesCache;

    @Override
    public LocalizationResourceBundle getResourceBundle( final SiteEntity site, final Locale locale )
    {
        final ResourceKey defaultLocalizationResourceKey = site.getDefaultLocalizationResource();
        if ( defaultLocalizationResourceKey == null )
        {
            return null;
        }

        return createResourceBundle( locale, defaultLocalizationResourceKey );
    }

    private Properties loadBundle( final ResourceKey defaultLocalizationResourceKey, final String bundleExtension )
    {
        final String defaultLocalizationResourceName = defaultLocalizationResourceKey.toString();
        final int pos = defaultLocalizationResourceName.lastIndexOf( '.' );

        String bundleResourceKey = defaultLocalizationResourceName;
        if ( pos > 0 )
        {
            bundleResourceKey = defaultLocalizationResourceName.substring( 0, pos );
        }

        bundleResourceKey = bundleResourceKey + bundleExtension + ".properties";

        return getOrCreateProperties( ResourceKey.from( bundleResourceKey ) );
    }

    private Properties getOrCreateProperties( final ResourceKey resourceKey )
    {

        Properties properties = getFromCache( resourceKey );

        if ( properties == null )
        {
            properties = loadPropertiesFromFile( resourceKey );
        }

        return properties;
    }

    private synchronized Properties loadPropertiesFromFile( final ResourceKey resourceKey )
    {
        Properties properties = getFromCache( resourceKey );

        if ( properties != null )
        {
            return properties;
        }

        properties = new Properties();

        final ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );
        if ( resourceFile != null )
        {
            try
            {
                properties.load( resourceFile.getDataAsInputStream() );

            }
            catch ( final IOException e )
            {
                throw new LocalizationResourceException( "Not able to load resource: " + resourceFile.getName(), e );
            }
        }

        putInCache( resourceKey, properties );
        return properties;
    }

    private void putInCache( final ResourceKey resourceKey, final Properties properties )
    {
        this.propertiesCache.put( new LocalizationPropertiesCacheEntry( resourceKey, properties ) );
    }

    private Properties getFromCache( final ResourceKey resourceKey )
    {
        final LocalizationPropertiesCacheEntry entry = this.propertiesCache.get( resourceKey );
        return entry != null ? entry.getProperties() : null;
    }

    private LocalizationResourceBundle createResourceBundle( final Locale locale, final ResourceKey defaultLocalizationResourceKey )
    {
        Properties props = new Properties();

        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        props.putAll( loadBundle( defaultLocalizationResourceKey, "" ) );

        if ( StringUtils.isNotEmpty( lang ) )
        {
            lang = lang.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang ) );
        }

        if ( StringUtils.isNotEmpty( country ) )
        {
            country = country.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang + "_" + country ) );
        }

        if ( StringUtils.isNotEmpty( variant ) )
        {
            variant = variant.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang + "_" + country + "_" + variant ) );
        }

        return new LocalizationResourceBundle( props );
    }

    @Autowired
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Autowired
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheFacade = cacheManager.getLocalizationCache();
    }

    @Value("${cms.cache.localization.checkInterval}")
    public void setCheckInterval( final long checkInterval )
    {
        this.checkInterval = checkInterval;
    }

    @Override
    public void afterPropertiesSet()
    {
        this.propertiesCache = new LocalizationPropertiesCache( this.cacheFacade, this.resourceService, this.checkInterval );
    }
}
