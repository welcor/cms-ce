/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import java.util.Map;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteProperties;

public final class CmsAndSitePropertiesAccessor
    implements Accessor<String>
{
    private String path;

    private final Map<Object, Object> rootProperties;

    private Map<Object, Object> siteProperties;

    public CmsAndSitePropertiesAccessor( final Properties rootProperties, final SiteProperties siteProperties )
    {
        this.rootProperties = rootProperties;
        this.siteProperties = siteProperties != null ? siteProperties.getProperties() : null;
    }

    public String getValue( final String name )
    {
        this.path = this.path == null ? name : this.path + "." + name;

        Object value = null;

        if ( siteProperties != null )
        {
            value = siteProperties.get( this.path );
        }
        if ( value == null )
        {
            value = rootProperties.get( this.path );
        }

        return value != null ? value.toString() : null;
    }

}
