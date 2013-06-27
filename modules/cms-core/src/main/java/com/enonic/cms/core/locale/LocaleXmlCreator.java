/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.locale;

import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Preconditions;

public class LocaleXmlCreator
{

    public Document createLocalesDocument( final Locale[] locales )
    {
        Preconditions.checkNotNull( locales, "locales cannot be null" );

        Element localesEl = new Element( "locales" );
        for ( Locale locale : locales )
        {
            localesEl.addContent( doCreateLocaleElement( locale, null ) );
        }
        return new Document( localesEl );
    }

    public Document createLocalesDocument( final Locale locale )
    {
        Preconditions.checkNotNull( locale, "locale cannot be null" );

        Element localesEl = new Element( "locales" );
        localesEl.addContent( doCreateLocaleElement( locale, null ) );
        return new Document( localesEl );
    }

    public Document createLocaleDocument( final Locale locale, Locale inLocale )
    {
        Preconditions.checkNotNull( locale, "locale cannot be null" );

        Element localesEl = new Element( "locales" );
        localesEl.addContent( doCreateLocaleElement( locale, inLocale ) );
        return new Document( localesEl );
    }

    private String getISO3Country( final Locale locale )
    {
        try
        {
            return locale.getISO3Country();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private String getISO3Language( final Locale locale )
    {
        try
        {
            return locale.getISO3Language();
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private Element doCreateLocaleElement( final Locale locale, final Locale inLocale )
    {
        Element localeEl = new Element( "locale" );
        localeEl.addContent( new Element( "name" ).setText( locale.toString() ) );
        localeEl.addContent( new Element( "country" ).setText( asEmptyIfNull( locale.getCountry() ) ) );
        localeEl.addContent( new Element( "display-country" ).setText( asEmptyIfNull( locale.getDisplayCountry() ) ) );
        localeEl.addContent( new Element( "display-language" ).setText( asEmptyIfNull( locale.getDisplayLanguage() ) ) );
        localeEl.addContent( new Element( "display-name" ).setText( asEmptyIfNull( locale.getDisplayName() ) ) );
        localeEl.addContent( new Element( "display-variant" ).setText( asEmptyIfNull( locale.getDisplayVariant() ) ) );
        localeEl.addContent( new Element( "iso3country" ).setText( asEmptyIfNull( getISO3Country( locale ) ) ) );
        localeEl.addContent( new Element( "iso3language" ).setText( asEmptyIfNull( getISO3Language( locale ) ) ) );
        localeEl.addContent( new Element( "language" ).setText( asEmptyIfNull( locale.getLanguage() ) ) );
        localeEl.addContent( new Element( "variant" ).setText( asEmptyIfNull( locale.getVariant() ) ) );

        if ( inLocale != null )
        {
            localeEl.addContent( new Element( "display-country-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayCountry( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-language-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayLanguage( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-name-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayName( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-variant-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayVariant( inLocale ) ) ) );
        }
        return localeEl;
    }

    private String asEmptyIfNull( final String value )
    {
        return value == null ? "" : value;
    }

}
