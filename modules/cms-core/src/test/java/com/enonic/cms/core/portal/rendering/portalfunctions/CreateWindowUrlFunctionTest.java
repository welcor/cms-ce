/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.rendering.portalfunctions;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteKey;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.core.structure.portlet.PortletKey;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;

import static junit.framework.Assert.assertEquals;

public class CreateWindowUrlFunctionTest
{
    private MenuItemDao menuItemDao = Mockito.mock( MenuItemDao.class );

    private PortletDao portletDao = Mockito.mock( PortletDao.class );

    private SiteKey siteKey0 = new SiteKey( 0 );

    private SiteEntity site0 = createSite( siteKey0.toInt() );

    private MenuItemKey menuItemKey123 = new MenuItemKey( 123 );

    private MenuItemKey menuItemKey321 = new MenuItemKey( 321 );

    private PortletKey portletKey1 = new PortletKey( 1 );

    @Before
    public void before()
    {
    }

    @Test
    public void when_given_window_key_refers_current_page_then_reference_to_window_must_not_be_included()
    {
        // setup
        MenuItemEntity menuItem123 = createMenuItem( menuItemKey123, "desk", site0 );
        MenuItemEntity menuItem321 = createMenuItem( menuItemKey321, "other", site0 );
        PortletEntity portlet = createPortlet( portletKey1.toInt(), "lastebrukere i tbml - ajax" );

        Mockito.when( menuItemDao.findByKey( menuItemKey123 ) ).thenReturn( menuItem123 );
        Mockito.when( menuItemDao.findByKey( menuItemKey321 ) ).thenReturn( menuItem321 );
        Mockito.when( portletDao.findByKey( portletKey1.toInt() ) ).thenReturn( portlet );

        PortalFunctionsContext context = new PortalFunctionsContext();
        context.setSite( site0 );
        context.setPortalInstanceKey( PortalInstanceKey.createWindow( new WindowKey( menuItemKey123, portletKey1 ) ) );
        context.setOriginalSitePath( new SitePath( siteKey0, "desk/_window/oversikt nokkeltall - ajax" ) );

        CreateWindowUrlFunction function = new CreateWindowUrlFunction( menuItemDao, portletDao, context );
        function.useWindowKey( new WindowKey( menuItemKey123, portletKey1 ) );

        // exercise
        SitePath result = function.createWindowUrl();

        // verify
        assertEquals( "/desk/_window/lastebrukere i tbml - ajax", result.getLocalPath().toString() );
    }

    private SiteEntity createSite( int key )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( key );
        return site;
    }

    private MenuItemEntity createMenuItem( MenuItemKey key, String name, SiteEntity site )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( key );
        menuItem.setName( name );
        menuItem.setSite( site );
        return menuItem;
    }

    private PortletEntity createPortlet( int key, String name )
    {
        PortletEntity portlet = new PortletEntity();
        portlet.setKey( key );
        portlet.setName( name );
        return portlet;
    }
}
