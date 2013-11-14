/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

class Section
    implements Comparable<Section>
{
    private SiteKey siteKey;

    private MenuItemKey menuItemKey;

    private String menuItemName;

    private String menuItemPath;

    private String siteName;

    private List<ContentEntity> contentList = new ArrayList<ContentEntity>();

    public Section( SiteKey siteKey, MenuItemKey menuItemKey, String menuItemName, String menuItemPathAsString, String siteName )
    {
        this.menuItemKey = menuItemKey;
        this.siteKey = siteKey;
        this.menuItemName = menuItemName;
        this.menuItemPath = menuItemPathAsString;
        this.siteName = siteName;
    }

    public int compareTo( com.enonic.cms.core.structure.Section o )
    {

        // return menuItemName.compareToIgnoreCase( o.getMenuItemName() );

        String s1path = this.siteName + this.menuItemPath;
        String s2path = o.getSiteName() + o.getMenuItemPath();

        return s1path.compareToIgnoreCase( s2path );

    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public String getMenuItemName()
    {
        return menuItemName;
    }

    public String getMenuItemPath()
    {
        return menuItemPath;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        com.enonic.cms.core.structure.Section s = (com.enonic.cms.core.structure.Section) o;

        if ( this.siteKey.equals( s.getSiteKey() ) && this.menuItemPath.equalsIgnoreCase( s.getMenuItemPath() ) )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    public int hashCode()
    {
        return menuItemName.hashCode() + siteKey.hashCode();
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName( final String siteName )
    {
        this.siteName = siteName;
    }

    public void addContent( final ContentEntity content )
    {
        contentList.add( content );
    }

    public List<ContentEntity> getContentList()
    {
        return contentList;
    }
}
