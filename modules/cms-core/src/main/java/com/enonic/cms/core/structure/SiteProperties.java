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

    public String getSiteURL()
    {
        return properties.getProperty( SitePropertyNames.SITE_URL );
    }

    public String getProperty( String propertyKey )
    {
        return properties.getProperty( propertyKey );
    }

    public Properties getProperties()
    {
        return properties;
    }

    public Integer getPageCacheTimeToLive()
    {
        return getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE );
    }

    private Integer getPropertyAsInteger( final String key )
    {
        String svalue = properties.getProperty( key );

        if ( svalue != null && !StringUtils.isNumeric( svalue ) )
        {
            throw new NumberFormatException( "Invalid value of property " + key + " = " + svalue + " in site-" + siteKey + ".properties" );
        }

        return svalue == null ? null : new Integer( svalue );
    }

    private Boolean getPropertyAsBoolean( final String key )
    {
        String svalue = properties.getProperty( key );

        return svalue == null ? Boolean.FALSE : Boolean.valueOf( svalue );
    }
}
