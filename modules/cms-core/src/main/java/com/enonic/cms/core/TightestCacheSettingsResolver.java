/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertiesService;
import com.enonic.cms.core.structure.SitePropertyNames;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

@Component
public class TightestCacheSettingsResolver
{
    @Autowired
    private SitePropertiesService sitePropertiesService;

    public CacheSettings resolveTightestCacheSettingsForPage( MenuItemEntity menuItem, Regions regions, PageTemplateEntity pageTemplate )
    {
        if ( menuItem == null )
        {
            throw new IllegalArgumentException( "Given menuItem cannot be null" );
        }

        final SiteKey siteKey = menuItem.getSite().getKey();
        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( siteKey );
        boolean pageCacheEnabledForSite = siteProperties.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE );
        if ( !pageCacheEnabledForSite )
        {
            return new CacheSettings( false, CacheSettings.TYPE_DEFAULT, 0 );
        }
        int defaultSecondsToLiveForSite = siteProperties.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE );

        CacheSettings menuItemCacheSettings = menuItem.getCacheSettings( defaultSecondsToLiveForSite, pageTemplate );
        if ( menuItemCacheSettings.isDisabled() )
        {
            return menuItemCacheSettings;
        }

        CacheSettings settingWithLeastTime = menuItemCacheSettings;

        // Compare with the portlets cache settings
        final List<PortletEntity> portlets = regions.getPortlets();

        for ( PortletEntity portlet : portlets )
        {
            CacheSettings current = portlet.getCacheSettings( defaultSecondsToLiveForSite );

            if ( current.isTighterThan( settingWithLeastTime ) )
            {
                settingWithLeastTime = current;
            }
        }

        return settingWithLeastTime;
    }
}
