/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocalizationResourceBundleUtilsTest
{
    @Test
    public void testParseLocaleString()
    {
        Locale locale = LocalizationResourceBundleUtils.parseLocaleString( "no" );

        assertEquals( "no", locale.getLanguage() );
        assertEquals( "", locale.getCountry() );
    }

    @Test
    public void testParseLocaleStringWithCountry()
    {
        Locale locale = LocalizationResourceBundleUtils.parseLocaleString( "no-US" );

        assertEquals( "no", locale.getLanguage() );
        assertEquals( "US", locale.getCountry() );
    }

    @Test(expected = LocaleParsingException.class)
    public void testParseInvalidLocaleString()
    {
        LocalizationResourceBundleUtils.parseLocaleString( "_US" );
    }

    @Test
    public void testParseLocaleStringWithContryAndVariant()
    {
        Locale locale = LocalizationResourceBundleUtils.parseLocaleString( "no_NO_NY" );

        assertEquals( "Should contain language", "no", locale.getLanguage() );
        assertEquals( "Should contain country", "NO", locale.getCountry() );
        assertEquals( "Should contain variant", "NY", locale.getVariant() );
    }
}
