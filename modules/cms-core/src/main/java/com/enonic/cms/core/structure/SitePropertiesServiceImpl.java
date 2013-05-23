/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.store.dao.SiteDao;

@Component("sitePropertiesService")
public class SitePropertiesServiceImpl
    implements SitePropertiesService, InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SitePropertiesService.class );

    private Properties defaultProperties;

    private final Map<SiteKey, Properties> sitePropertiesMap = new ConcurrentHashMap<SiteKey, Properties>();

    private File homeDir;

    private ResourceLoader resourceLoader = new FileSystemResourceLoader();

    private String characterEncoding;

    @Autowired
    private PageCacheService pageCacheService;

    @Autowired
    private SiteServiceImpl siteService;

    @Autowired
    private SiteDao siteDao;

    public void afterPropertiesSet()
        throws Exception
    {
        Resource resource = resourceLoader.getResource( "classpath:com/enonic/cms/business/render/site-default.properties" );
        try
        {
            defaultProperties = new Properties();
            defaultProperties.load( resource.getInputStream() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load site-default.properties", e );
        }

        for ( final SiteEntity currSite : siteDao.findAll() )
        {
            loadSiteProperties( currSite.getKey() );
        }
    }

    public SiteProperties getSiteProperties( SiteKey siteKey )
    {
        return new SiteProperties( doGetSiteProperties( siteKey ) );
    }

    public String getProperty( String key, SiteKey siteKey )
    {
        Properties props = doGetSiteProperties( siteKey );
        if ( props == null )
        {
            throw new IllegalArgumentException( "No properties for site " + siteKey );
        }

        return StringUtils.trimToNull( props.getProperty( key ) );
    }

    public Integer getPropertyAsInteger( String key, SiteKey siteKey )
    {
        String svalue = getProperty( key, siteKey );

        if ( svalue != null && !StringUtils.isNumeric( svalue ) )
        {
            throw new NumberFormatException( "Invalid value of property " + key + " = " + svalue + " in site-" + siteKey + ".properties" );
        }

        return svalue == null ? null : new Integer( svalue );
    }

    public Boolean getPropertyAsBoolean( String key, SiteKey siteKey )
    {
        String svalue = getProperty( key, siteKey );

        return svalue == null ? Boolean.FALSE : Boolean.valueOf( svalue );
    }

    /**
     * Loads properties from the disk if properties are not loaded
     * <p/>
     * thread safe
     *
     * @param siteKey
     * @return
     */
    private Properties doGetSiteProperties( final SiteKey siteKey )
    {
        Properties props;
        synchronized ( sitePropertiesMap )
        {
            props = sitePropertiesMap.get( siteKey );
            if ( props == null )
            {
                loadSiteProperties( siteKey );
            }
        }
        return props;
    }

    /**
     * removes site properties from internal map. New properties will be loaded on demand.
     *
     * @param siteKey site key
     */
    public void reloadSiteProperties( final SiteKey siteKey )
    {
        synchronized ( sitePropertiesMap )
        {
            loadSiteProperties( siteKey );

            pageCacheService.reloadPageCacheConfig( siteKey );
            siteService.updateAuthenticationLoggingEnabled( siteKey, null );
        }
    }

    private void loadSiteProperties( final SiteKey siteKey )
    {
        final Properties siteProperties = new Properties( defaultProperties );
        siteProperties.setProperty( "sitekey", String.valueOf( siteKey ) );

        final String relativePathToCmsHome = "/config/site-" + siteKey + ".properties";
        try
        {
            String resourcePath = this.homeDir.toURI().toURL() + relativePathToCmsHome;
            Resource resource = resourceLoader.getResource( resourcePath );
            boolean useCustomProperties = resource.exists();
            if ( useCustomProperties )
            {
                InputStream stream = resource.getInputStream();
                siteProperties.load( stream );
                siteProperties.setProperty( "customSiteProperties", "true" );
                stream.close();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load site properties file: " + relativePathToCmsHome, e );
        }

        siteProperties.setProperty( SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, this.characterEncoding );
        sitePropertiesMap.put( siteKey, siteProperties );

        LOG.info( "Loaded properties for site #{}", siteKey );
    }

    @Value("${cms.home}")
    public void setHomeDir( File homeDir )
    {
        this.homeDir = homeDir;
    }

    @Value("${cms.url.characterEncoding}")
    public void setCharacterEncoding( final String encoding )
    {
        this.characterEncoding = encoding;
    }

    public void setResourceLoader( ResourceLoader resourceLoader )
    {
        this.resourceLoader = resourceLoader;
    }
}
