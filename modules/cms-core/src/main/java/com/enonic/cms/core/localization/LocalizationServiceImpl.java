/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.structure.SiteEntity;

@Component
public final class LocalizationServiceImpl
    implements LocalizationService
{
    private LocalizationResourceBundleService localizationResourceBundleService;

    private static final String NO_TRANSLATION_FOUND_VALUE = "NOT TRANSLATED";

    @Override
    public String getLocalizedPhrase( final SiteEntity site, final String phrase, final Locale locale )
    {
        return getLocalizedPhrase( site, phrase, null, locale );
    }

    @Override
    public String getLocalizedPhrase( final SiteEntity site, final String phrase, final Object[] arguments, final Locale locale )
    {
        if ( noLocalizationResourceDefinedForSite( site ) )
        {
            return createNotTranslated( phrase );
        }

        if ( locale == null )
        {
            return createNotTranslated( phrase );
        }

        final LocalizationResourceBundle localizationResourceBundle = getResourceBundleForLocale( site, locale );
        if ( localizationResourceBundle == null )
        {
            return createNotTranslated( phrase );
        }

        final String localizedPhrase = getLocalizedPhrase( phrase, arguments, localizationResourceBundle );
        return StringUtils.isNotEmpty( localizedPhrase ) ? localizedPhrase : createNotTranslated( phrase );
    }

    private boolean noLocalizationResourceDefinedForSite( final SiteEntity site )
    {
        return site.getDefaultLocalizationResource() == null;
    }

    private String createNotTranslated( final String phrase )
    {
        return NO_TRANSLATION_FOUND_VALUE + ": " + phrase;
    }

    private LocalizationResourceBundle getResourceBundleForLocale( final SiteEntity site, final Locale locale )
    {
        return localizationResourceBundleService.getResourceBundle( site, locale );
    }

    private String getLocalizedPhrase( final String phrase, final Object[] arguments, final LocalizationResourceBundle resourceBundle )
    {
        if ( arguments == null )
        {
            return resourceBundle.getLocalizedPhrase( phrase );
        }

        return resourceBundle.getLocalizedPhrase( phrase, arguments );
    }

    @Autowired
    public void setLocalizationResourceBundleService( final LocalizationResourceBundleService localizationResourceBundleService )
    {
        this.localizationResourceBundleService = localizationResourceBundleService;
    }
}

