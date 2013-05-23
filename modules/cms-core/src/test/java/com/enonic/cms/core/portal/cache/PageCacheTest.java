/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.standard.StandardCacheManager;

import com.enonic.cms.core.CacheObjectSettings;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.cluster.NopClusterEventPublisher;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.portal.rendering.PageCacheKey;
import com.enonic.cms.core.portal.rendering.RenderedPageResult;
import com.enonic.cms.core.portal.rendering.RenderedWindowResult;
import com.enonic.cms.core.portal.rendering.WindowCacheKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

import static org.junit.Assert.*;


public class PageCacheTest
{
    private SiteKey siteKey_1 = new SiteKey( 1 );

    private SiteKey siteKey_2 = new SiteKey( 2 );

    private PageCache pageCache_site_1;

    private PageCache pageCache_site_2;

    private CacheFacade cacheFacade;

    private CacheObjectSettings settings;

    @Before
    public void before()
    {
        int maxEntries = 100;
        int timeToLiveSeconds = 1000;

        final ConfigProperties props = new ConfigProperties();
        props.setProperty( "cms.cache.page.memoryCapacity", String.valueOf( maxEntries ) );
        props.setProperty( "cms.cache.page.timeToLive", String.valueOf( timeToLiveSeconds ) );

        final StandardCacheManager cacheManager = new StandardCacheManager();
        cacheManager.setProperties( props );
        cacheManager.setClusterEventPublisher( new NopClusterEventPublisher() );
        cacheManager.afterPropertiesSet();

        cacheFacade = cacheManager.getPageCache();

        pageCache_site_1 = new PageCache( siteKey_1, cacheFacade );
        pageCache_site_1.setEnabled( true );
        pageCache_site_1.setDefaultTimeToLive( timeToLiveSeconds );

        pageCache_site_2 = new PageCache( siteKey_2, cacheFacade );
        pageCache_site_2.setEnabled( true );
        pageCache_site_1.setDefaultTimeToLive( timeToLiveSeconds );

        settings = new CacheObjectSettings( "default", timeToLiveSeconds );
    }

    @Test
    public void testCachePage()
    {
        pageCache_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePage( createPKey( "ABC", "2", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );

        assertEquals( 3, cacheFacade.getCount() );
    }

    @Test
    public void testCacheContentObject()
    {
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        assertEquals( 3, cacheFacade.getCount() );
    }

    @Test
    public void testRemoveEntriesBySite()
    {

        pageCache_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        pageCache_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCache_site_1.removeEntriesBySite();
        assertEquals( 2, cacheFacade.getCount() );

        pageCache_site_2.removeEntriesBySite();
        assertEquals( 0, cacheFacade.getCount() );

    }

    @Test
    public void testRemoveContentObjectEntriesBySite()
    {

        pageCache_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        pageCache_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCache_site_1.removePortletWindowEntriesBySite();
        // expect on page entry on site 1 and the two entries on site 2
        assertEquals( 3, cacheFacade.getCount() );

        pageCache_site_2.removePortletWindowEntriesBySite();
        assertEquals( 2, cacheFacade.getCount() );

    }

    @Test
    public void testRemovePageEntriesBySite()
    {
        pageCache_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        pageCache_site_2.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_2.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        assertEquals( 5, cacheFacade.getCount() );

        pageCache_site_1.removePageEntriesBySite();
        // expect two object entries on site 1 and the two entries on site 2
        assertEquals( 4, cacheFacade.getCount() );

        pageCache_site_2.removePageEntriesBySite();
        assertEquals( 3, cacheFacade.getCount() );

    }

    @Test
    public void testRemoveEntriesByMenuItem()
    {

        pageCache_site_1.cachePage( createPKey( "ABC", "1", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "1", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        pageCache_site_1.cachePage( createPKey( "ABC", "2", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_1.cachePortletWindow( createCOKey( "ABC", "2", "102", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        pageCache_site_2.cachePage( createPKey( "ABC", "11", "q", "a", new Locale( "no" ) ), new RenderedPageResult(), settings );
        pageCache_site_2.cachePortletWindow( createCOKey( "ABC", "11", "101", "q", "p", "a", new Locale( "no" ) ),
                                             new RenderedWindowResult(), settings );

        assertEquals( 7, cacheFacade.getCount() );

        pageCache_site_1.removeEntriesByMenuItem( new MenuItemKey( 1 ) );
        assertEquals( 4, cacheFacade.getCount() );

        pageCache_site_2.removeEntriesByMenuItem( new MenuItemKey( 11 ) );
        assertEquals( 2, cacheFacade.getCount() );

    }

    private PageCacheKey createPKey( String userKey, String menuItemKey, String queryString, String deviceClass, Locale resolvedLocale )
    {
        PageCacheKey key = new PageCacheKey();
        key.setQueryString( queryString );
        key.setDeviceClass( deviceClass );
        key.setLocale( resolvedLocale );
        key.setUserKey( userKey );
        key.setMenuItemKey( new MenuItemKey( menuItemKey ) );
        return key;
    }

    private WindowCacheKey createCOKey( String userKey, String menuItemKey, String contentObjectKey, String queryString,
                                        String paramsString, String deviceClass, Locale resolvedLocale )
    {
        return new WindowCacheKey( userKey, new MenuItemKey( menuItemKey ), Integer.valueOf( contentObjectKey ), queryString, paramsString,
                                   deviceClass, resolvedLocale );
    }
}
