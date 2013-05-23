/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.CacheObjectSettings;
import com.enonic.cms.core.CachedObject;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.rendering.PageCacheKey;
import com.enonic.cms.core.portal.rendering.WindowCacheKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class PageCache
{
    private static final String TYPE_PAGE = "P";

    private static final String TYPE_OBJECT = "O";

    private final SiteKey siteKey;

    private final CacheFacade cacheFacade;

    private boolean enabled = false;

    private Integer defaultTimeToLive;

    public PageCache( final SiteKey siteKey, CacheFacade cacheFacade )
    {
        Preconditions.checkNotNull( siteKey, "siteKey cannot be null" );
        Preconditions.checkNotNull( cacheFacade, "cacheFacade cannot be null" );
        this.siteKey = siteKey;
        this.cacheFacade = cacheFacade;
    }

    public void setEnabled( boolean value )
    {
        this.enabled = value;
    }

    public void setDefaultTimeToLive( Integer value )
    {
        this.defaultTimeToLive = value;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public int getDefaultTimeToLive()
    {

        if ( defaultTimeToLive != null )
        {
            return defaultTimeToLive;
        }
        return cacheFacade.getTimeToLive();
    }

    private void doCacheObject( String group, Object key, CachedObject obj, int secondsToLive )
    {
        cacheFacade.put( group, key.toString(), obj, secondsToLive );
    }

    private CachedObject doGetCachedObject( String group, Object key )
    {
        final CachedObject cachedObject = (CachedObject) cacheFacade.get( group, key.toString() );
        if ( cachedObject != null && cachedObject.isExpired() )
        {
            cacheFacade.remove( siteKey.toString(), key.toString() );
            return null;
        }
        return cachedObject;
    }


    public CachedObject cachePage( PageCacheKey key, Object page, CacheObjectSettings settings )
    {
        if ( !enabled )
        {
            return new CachedObject( page, false );
        }

        int secondsToLive = resolveSecondsToLive( settings );
        CachedObject cachedObject = new CachedObject( page );
        cachedObject.setExpirationTime( new DateTime().plusSeconds( secondsToLive ) );
        String group = resolveGroupStringForPage( siteKey, key.getMenuItemKey() );
        doCacheObject( group, key, cachedObject, secondsToLive );
        return cachedObject;
    }

    public CachedObject cachePortletWindow( WindowCacheKey key, Object object, CacheObjectSettings settings )
    {
        if ( !enabled )
        {
            return new CachedObject( object, false );
        }

        int secondsToLive = resolveSecondsToLive( settings );
        CachedObject cachedObject = new CachedObject( object );
        cachedObject.setExpirationTime( new DateTime().plusSeconds( secondsToLive ) );
        String group = resolveGroupStringForObject( siteKey, key.getMenuItemKey() );
        doCacheObject( group, key, cachedObject, secondsToLive );
        return cachedObject;
    }

    public CachedObject getCachedPage( PageCacheKey key )
    {
        if ( !enabled )
        {
            return null;
        }

        final String group = resolveGroupStringForPage( siteKey, key.getMenuItemKey() );
        return doGetCachedObject( group, key );
    }

    public CachedObject getCachedPortletWindow( WindowCacheKey key )
    {
        if ( !enabled )
        {
            return null;
        }

        final String group = resolveGroupStringForObject( siteKey, key.getMenuItemKey() );
        return doGetCachedObject( group, key );
    }

    public void removeEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( siteKey + "-" );
    }

    public void removePageEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( siteKey + "-" + TYPE_PAGE + "-" );
    }

    public void removePortletWindowEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( siteKey + "-" + TYPE_OBJECT + "-" );
    }

    public void removeEntriesByMenuItem( final MenuItemKey menuItemKey )
    {
        if ( !enabled )
        {
            return;
        }

        String groupForPage = resolveGroupStringForPage( siteKey, menuItemKey );
        cacheFacade.removeGroup( groupForPage );

        String groupForObjects = resolveGroupStringForObject( siteKey, menuItemKey );
        cacheFacade.removeGroup( groupForObjects );
    }

    private String resolveGroupStringForPage( final SiteKey siteKey, final MenuItemKey menuItemKey )
    {
        final StringBuilder s = new StringBuilder();
        s.append( siteKey.toString() ).append( "-" ).append( TYPE_PAGE ).append( "-" ).append( menuItemKey );
        return s.toString();
    }

    private String resolveGroupStringForObject( final SiteKey siteKey, final MenuItemKey menuItemKey )
    {
        final StringBuilder s = new StringBuilder();
        s.append( siteKey.toString() ).append( "-" ).append( TYPE_OBJECT ).append( "-" ).append( menuItemKey );
        return s.toString();
    }

    private int resolveSecondsToLive( final CacheObjectSettings settings )
    {
        if ( settings.useDefaultSettings() )
        {
            return getDefaultTimeToLive();
        }
        else
        {
            // specified or "live forever"
            return settings.getSecondsToLive();
        }
    }

}
