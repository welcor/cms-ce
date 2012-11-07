/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.SiteEntity;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

public class LocalizationServiceImplTest
{

    private LocalizationServiceImpl localizationService;

    private LocalizationResourceBundleService resourceBundleServiceMock;

    private final SiteKey siteKey = new SiteKey( 0 );

    public static List<String> supportedPhrases = Lists.newArrayList();

    private static final String SITE_LOCALIZATION_RESOURCE_KEY = "test";

    public static final String LOCALIZED_ADDON = "Localized";

    @Before
    public void setUp()
    {
        localizationService = new LocalizationServiceImpl();

        resourceBundleServiceMock = createMock( LocalizationResourceBundleService.class );

        localizationService.setLocalizationResourceBundleService( resourceBundleServiceMock );
    }

    @Test
    public void getLocalizedPhrase()
    {
        createResourceBundleExpectance( getNorwegianTestResourceBundle() );

        setUpSupportedPhrases();

        for ( String phrase : supportedPhrases )
        {
            String localizedPhrase = localizationService.getLocalizedPhrase( createSite(), phrase, new Locale( "no" ) );

            assertEquals( phrase + LOCALIZED_ADDON + "_no", localizedPhrase );
        }
    }

    @After
    public void tearDown()
    {
        verify( resourceBundleServiceMock );
    }

    private void createResourceBundleExpectance( LocalizationResourceBundle resouceBundle )
    {
        expect( resourceBundleServiceMock.getResourceBundle( isA( SiteEntity.class ), isA( Locale.class ) ) ).andReturn(
            resouceBundle ).anyTimes();
        replay( resourceBundleServiceMock );
    }

    private void setUpSupportedPhrases()
    {
        supportedPhrases.add( "test" );
        supportedPhrases.add( "ost" );
        supportedPhrases.add( "fisk" );
    }

    private SiteEntity createSite()
    {
        final SiteEntity site = new SiteEntity();
        site.setKey( siteKey.toInt() );
        site.setDefaultLocalizationResource( ResourceKey.from( SITE_LOCALIZATION_RESOURCE_KEY ) );

        return site;
    }

    private LocalizationResourceBundle getNorwegianTestResourceBundle()
    {
        final Properties properties = new Properties();
        properties.put( "test", "testLocalized_no" );
        properties.put( "ost", "ostLocalized_no" );
        properties.put( "fisk", "fiskLocalized_no" );

        return new LocalizationResourceBundle( properties );
    }
}
