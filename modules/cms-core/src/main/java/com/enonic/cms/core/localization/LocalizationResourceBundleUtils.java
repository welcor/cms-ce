/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LocalizationResourceBundleUtils
{
    // Pattern to parse locale string on format Languagcode[-country][anything]
    private static final String LOCALE_PATTERN = "^(\\w{2})(_(\\w{2}))?(_(\\w{2}))?$";

    private static Matcher match( String inputString, final String regexp )
    {
        Pattern pattern = Pattern.compile( regexp, Pattern.CASE_INSENSITIVE );
        if ( inputString == null )
        {
            inputString = "";
        }

        return pattern.matcher( inputString );
    }

    public static Locale parseLocaleString( String localeAsString )
    {
        localeAsString = localeAsString.replace( '-', '_' );

        final Matcher matcher = match( localeAsString, LOCALE_PATTERN );

        String language = "";
        String country = "";
        String variant = "";

        if ( matcher.matches() )
        {
            language = getLanguageFromMatcher( matcher );
            country = getCountryFromMatcher( matcher );
            variant = getVariantFromMatcher( matcher );
        }
        else
        {
            throw new LocaleParsingException( "Could not parse locale string: " + localeAsString + " to valid locale" );
        }

        return new Locale( language, country == null ? "" : country, variant == null ? "" : variant );
    }

    private static String getLanguageFromMatcher( final Matcher matcher )
    {
        return matcher.group( 1 );
    }

    private static String getCountryFromMatcher( final Matcher matcher )
    {
        return matcher.group( 3 );
    }

    private static String getVariantFromMatcher( final Matcher matcher )
    {
        return matcher.group( 5 );
    }
}
