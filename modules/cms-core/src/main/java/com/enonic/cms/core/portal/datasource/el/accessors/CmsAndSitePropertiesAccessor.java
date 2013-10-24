/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import java.util.Map;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteProperties;

public final class CmsAndSitePropertiesAccessor
    implements Accessor<CmsAndSitePropertiesAccessor>
{
    private final Map<Object, Object> rootProperties;

    private final Map<Object, Object> siteProperties;

    private final String path;

    public CmsAndSitePropertiesAccessor( final Properties rootProperties, final SiteProperties siteProperties )
    {
        this.rootProperties = rootProperties;
        this.siteProperties = siteProperties != null ? siteProperties.getProperties() : null;
        this.path = null;
    }

    public CmsAndSitePropertiesAccessor( final Map<Object, Object> rootProperties, final Map<Object, Object> siteProperties,
                                         final String path )
    {
        this.rootProperties = rootProperties;
        this.siteProperties = siteProperties;
        this.path = path;
    }

    public CmsAndSitePropertiesAccessor getValue( final String name )
    {
        final String path = this.path == null ? name : this.path + "." + name;

        return new CmsAndSitePropertiesAccessor( rootProperties, siteProperties, path );
    }

    @Override
    public String toString()
    {
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
