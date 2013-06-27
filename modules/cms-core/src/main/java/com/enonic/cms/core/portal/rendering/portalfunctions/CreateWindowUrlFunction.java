/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.rendering.portalfunctions;


import com.enonic.cms.core.Path;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.core.structure.portlet.PortletKey;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;

public class CreateWindowUrlFunction
{
    private MenuItemDao menuItemDao;

    private PortletDao portletDao;

    private PortalFunctionsContext context;

    private boolean useCurrentLocation = false;

    private WindowKey windowKey;

    private String outputFormat;

    CreateWindowUrlFunction( MenuItemDao menuItemDao, PortletDao portletDao, PortalFunctionsContext context )
    {
        this.menuItemDao = menuItemDao;
        this.portletDao = portletDao;
        this.context = context;
    }

    CreateWindowUrlFunction useCurrentLocation()
    {
        this.useCurrentLocation = true;
        this.windowKey = context.getPortalInstanceKey().getWindowKey();
        return this;
    }

    CreateWindowUrlFunction useWindowKey( WindowKey windowKey )
    {
        this.windowKey = windowKey;
        return this;
    }

    CreateWindowUrlFunction outputFormat( String outputFormat )
    {
        this.outputFormat = outputFormat;
        return this;
    }

    SitePath createWindowUrl()
    {
        final MenuItemKey menuItemKey = windowKey.getMenuItemKey();

        final MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            throw new PortalFunctionException( "Menuitem does not exist: " + menuItemKey );
        }
        final boolean menuItemIsInAnotherSite = !menuItem.getSite().equals( context.getSite() );
        if ( menuItemIsInAnotherSite )
        {
            throw new PortalFunctionException( "Menuitem exist in another site: " + menuItemKey );
        }

        final PortletKey portletKey = windowKey.getPortletKey();
        final PortletEntity portlet = portletDao.findByKey( portletKey.toInt() );
        if ( portlet == null )
        {
            throw new PortalFunctionException( "Portlet does not exist: " + menuItemKey );
        }

        final Path localPath = buildPathToWindow( menuItem, portlet );

        return new SitePath( context.getSite().getKey(), localPath );
    }

    private Path buildPathToWindow( MenuItemEntity menuItem, PortletEntity portlet )
    {
        Path localPath;
        boolean givenWindowKeyReferCurrentPage = context.getPortalInstanceKey().getMenuItemKey().equals( windowKey.getMenuItemKey() );
        if ( useCurrentLocation || givenWindowKeyReferCurrentPage )
        {
            localPath = resolvePathToPageOnly( context.getOriginalSitePath().getLocalPath() );
        }
        else
        {
            localPath = menuItem.getPath();
        }

        String portletPathStr = "_window/" + portlet.getName().toLowerCase();
        if ( outputFormat != null )
        {
            portletPathStr = portletPathStr + "." + outputFormat;
        }
        localPath = localPath.appendPath( new Path( portletPathStr ) );
        return localPath;
    }

    private Path resolvePathToPageOnly( final Path path )
    {
        final SitePath sitePathToPageWithPotentialWindowReference = new SitePath( context.getSite().getKey(), path );
        if ( sitePathToPageWithPotentialWindowReference.hasReferenceToWindow() )
        {
            return sitePathToPageWithPotentialWindowReference.removeWindowReference().getLocalPath();
        }
        return path;
    }
}
