/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Properties;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.SiteEntity;

public class LocalizationTestUtils
{
    private static final String BASE_RESOURCE_CLASSPATH = "classpath:com/enonic/cms/core/localization/";

    public static Properties create_Default_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases.properties" );
    }

    public static Properties create_NO_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_no.properties" );
    }

    public static Properties create_EN_US_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_en-us.properties" );
    }

    public static Properties getPropertiesFromFile( String path )
        throws Exception
    {
        final Properties properties = new Properties();

        final ResourceLoader resourceLoader = new FileSystemResourceLoader();
        final Resource resource = resourceLoader.getResource( path );

        properties.load( resource.getInputStream() );
        return properties;
    }

    public static LocalizationResourceBundle create_US_NO_DEFAULT_resourceBundle()
        throws Exception
    {
        final Properties properties = new Properties();

        properties.putAll( create_Default_Properties() );
        properties.putAll( create_NO_Properties() );
        properties.putAll( create_EN_US_Properties() );

        return new LocalizationResourceBundle( properties );
    }

    public static SiteEntity createSite( final String defaultLocalizationResource )
    {
        final SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        site.setDefaultLocalizationResource( ResourceKey.from( defaultLocalizationResource ) );
        return site;
    }
}