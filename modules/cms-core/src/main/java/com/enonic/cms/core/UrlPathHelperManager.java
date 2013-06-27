/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesListener;
import com.enonic.cms.core.structure.SitePropertiesService;
import com.enonic.cms.core.structure.SitePropertyNames;

@Component
public class UrlPathHelperManager
    implements SitePropertiesListener
{
    private SitePropertiesService sitePropertiesService;

    private final Map<SiteKey, UrlPathHelper> urlPathHelperMapBySiteKey = new HashMap<SiteKey, UrlPathHelper>();


    @Override
    public void sitePropertiesLoaded( final SiteProperties siteProperties )
    {
        // nothing
    }

    @Override
    public void sitePropertiesReloaded( final SiteProperties siteProperties )
    {
        synchronized ( urlPathHelperMapBySiteKey )
        {
            urlPathHelperMapBySiteKey.remove( siteProperties.getSiteKey() );
            createAndRegisterUrlPathHelper( siteProperties.getSiteKey() );
        }
    }

    public synchronized UrlPathHelper getUrlPathHelper( SiteKey siteKey )
    {
        UrlPathHelper urlPathHelper;
        synchronized ( urlPathHelperMapBySiteKey )
        {
            urlPathHelper = urlPathHelperMapBySiteKey.get( siteKey );
            if ( urlPathHelper == null )
            {
                urlPathHelper = createAndRegisterUrlPathHelper( siteKey );
            }
        }
        return urlPathHelper;
    }

    private UrlPathHelper createAndRegisterUrlPathHelper( final SiteKey siteKey )
    {
        final UrlPathHelper urlPathHelper;
        urlPathHelper = createUrlPathHelper( siteKey );
        urlPathHelperMapBySiteKey.put( siteKey, urlPathHelper );
        return urlPathHelper;
    }

    private UrlPathHelper createUrlPathHelper( SiteKey siteKey )
    {
        String defaultEncoding =
            sitePropertiesService.getSiteProperties( siteKey ).getProperty( SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING );

        SiteUrlPathHelper urlPathHelper = new SiteUrlPathHelper();
        urlPathHelper.setUrlDecode( true );
        urlPathHelper.setDefaultEncoding( defaultEncoding );
        return urlPathHelper;
    }

    @Autowired
    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

}
