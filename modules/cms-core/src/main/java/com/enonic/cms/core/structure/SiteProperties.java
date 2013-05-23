/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class SiteProperties
{
    private final SiteKey siteKey;

    private final Properties properties;

    public SiteProperties( final SiteKey siteKey, final Properties properties )
    {
        this.siteKey = siteKey;
        this.properties = properties;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getSiteURL()
    {
        return properties.getProperty( SitePropertyNames.SITE_URL );
    }

    public String getProperty( final String propertyKey )
    {
        return StringUtils.trimToNull( properties.getProperty( propertyKey ) );
    }

    public Properties getProperties()
    {
        return properties;
    }

    public int getPageCacheTimeToLive()
    {
        return getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE );
    }

    public boolean getPageCacheEnabled()
    {
        return getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE );
    }

    public boolean getAuthenticationLoggingEnabled()
    {
        return getPropertyAsBoolean( SitePropertyNames.LOGGING_AUTHENTICATION );
    }

    public Integer getPropertyAsInteger( final String key )
    {
        String svalue = StringUtils.trimToNull( properties.getProperty( key ) );

        if ( svalue != null && !StringUtils.isNumeric( svalue ) )
        {
            throw new NumberFormatException( "Invalid value of property " + key + " = " + svalue + " in site-" + siteKey + ".properties" );
        }

        return svalue == null ? null : new Integer( svalue );
    }

    public Boolean getPropertyAsBoolean( final String key )
    {
        String svalue = properties.getProperty( key );

        return svalue == null ? Boolean.FALSE : Boolean.valueOf( svalue );
    }
}
