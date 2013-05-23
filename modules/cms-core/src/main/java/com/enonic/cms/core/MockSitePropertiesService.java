/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesListener;
import com.enonic.cms.core.structure.SitePropertiesService;

public class MockSitePropertiesService
    implements SitePropertiesService
{

    private Map<SiteKey, SiteProperties> sitePropertiesMapBySiteKey = new HashMap<SiteKey, SiteProperties>();

    public void setProperty( SiteKey siteKey, String key, String value )
    {
        Properties props = getSiteProperties( siteKey ).getProperties();
        props.setProperty( key, value );
    }

    @Override
    public void registerSitePropertiesListener( final SitePropertiesListener listener )
    {
        // nothing
    }

    @Override
    public void reloadSiteProperties( final SiteKey siteKey )
    {
        // nothing
    }

    public SiteProperties getSiteProperties( SiteKey siteKey )
    {
        SiteProperties props = sitePropertiesMapBySiteKey.get( siteKey );
        if ( props == null )
        {
            props = new SiteProperties( siteKey, new Properties() );
            sitePropertiesMapBySiteKey.put( siteKey, props );
        }

        return props;
    }
}
