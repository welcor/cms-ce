/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.structure.SiteEntity;

import static org.junit.Assert.*;

public class LocalizationResourceBundleServiceImplTest
{
    private LocalizationResourceBundleServiceImpl resourceBundleService;

    private CacheFacade propertiesCache;

    @Before
    public void setUp()
    {
        this.propertiesCache = Mockito.mock( CacheFacade.class );
        final CacheManager cacheManager = Mockito.mock( CacheManager.class );
        Mockito.when( cacheManager.getLocalizationCache() ).thenReturn( this.propertiesCache );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );

        this.resourceBundleService = new LocalizationResourceBundleServiceImpl();
        this.resourceBundleService.setCacheManager( cacheManager );
        this.resourceBundleService.setResourceService( resourceService );
        this.resourceBundleService.afterPropertiesSet();
    }

    @Test
    public void testCache()
    {
        setUpFetchFromCache( new Properties() );

        final SiteEntity site = LocalizationTestUtils.createSite( "phrases.properties" );
        final Locale locale = new Locale( "no" );

        final LocalizationResourceBundle resourceBundle = this.resourceBundleService.getResourceBundle( site, locale );
        assertNotNull( "Should fetch empty properties from cache and create ResourceBundle", resourceBundle );
    }

    @Test
    public void testNoCache()
    {
        setUpFetchFromCache( null );

        final SiteEntity site = LocalizationTestUtils.createSite( "phrases.properties" );
        final Locale locale = new Locale( "no" );

        final LocalizationResourceBundle resourceBundle = this.resourceBundleService.getResourceBundle( site, locale );
        assertNotNull( "Should fetch empty properties from cache and create ResourceBundle", resourceBundle );
    }

    private void setUpFetchFromCache( final Properties properties )
    {
        final LocalizationPropertiesCacheEntry entry = new LocalizationPropertiesCacheEntry( ResourceKey.from( "" ), properties );
        Mockito.when( this.propertiesCache.get( Mockito.anyString(), Mockito.anyString() ) ).thenReturn( entry );
    }
}
