/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure.menuitem;


import java.util.Collection;

public class MenuItemKeysByPathResolver
{
    private static final MenuItemEntity MENUITEM_NOT_FOUND = new MenuItemEntity();

    private static final String PATH_SEPARATOR = "/";

    private static final String MENUITEM_SEPARATOR = ",";

    private MenuItemEntity menuItem;

    /**
     * @param menuItem menu-item to resolve relative paths from
     */
    public MenuItemKeysByPathResolver( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public String getPageKeyByPath( String path )
    {
        final MenuItemEntity menuItemEntityByPath = getMenuItemEntityByPath( path );

        if ( menuItemEntityByPath == MENUITEM_NOT_FOUND )
        {
            return "";
        }

        if ( menuItemEntityByPath == null ) // root menu item
        {
            return "";
        }

        return menuItemEntityByPath.getKey().toString();
    }

    public String getPageKeysByPath( String path )
    {
        final MenuItemEntity menuItemEntityByPath = getMenuItemEntityByPath( path );

        if ( menuItemEntityByPath == MENUITEM_NOT_FOUND )
        {
            return "";
        }

        final Collection<MenuItemEntity> itemEntityList = getMenuItemChildren( menuItemEntityByPath );

        String result = "";

        String separator = "";
        for ( final MenuItemEntity mi : itemEntityList )
        {
            result = result + separator + mi.getKey().toString();
            separator = MENUITEM_SEPARATOR;
        }

        return result;
    }

    /**
     * returns children for menu item.
     *
     * for root folder ( menuItemEntity == null) will return root children.
     *
     * @param menuItemEntity - menu item
     * @return children of menuItemEntity
     */
    private Collection<MenuItemEntity> getMenuItemChildren( final MenuItemEntity menuItemEntity )
    {
        return menuItemEntity == null ? menuItem.getSite().getTopMenuItems() : menuItemEntity.getChildren();
    }

    /**
     * <p>reads page key by menu path </p>
     * <p>path may be absolute or relative</p>
     * <p/>
     * Examples: <br/>
     * <p/>
     * <code>/</code> - MenuItemEntity of root folder<br/>
     * <code>./</code> - MenuItemEntity of current folder<br/>
     * <code>fldr</code> - MenuItemEntity of fldr folder in current folder<br/>
     * <code>./fldr</code> - MenuItemEntity of fldr folder in current folder<br/>
     * <code>../welcome/fldr</code> - more relative path<br/>
     * <code>../././welcome/./fldr/../fldr</code> - complex path<br/>
     * <p/>
     * if parent folder or relative folder does not exist function returns MENUITEM_NOT_FOUND constant
     *
     * @param path path to page
     * @return MenuItemEntity
     */
    protected MenuItemEntity getMenuItemEntityByPath( String path )
    {
        final String[] parts = path.split( PATH_SEPARATOR ); // split works strange. "////" will return ZERO parts !

        // currentItemEntity = null for absolute path (root folder) or current menuItem for relative
        MenuItemEntity currentItemEntity = parts.length == 0 || "".equals( parts[0] ) ? null : menuItem;

        searching:
        for ( int num = 0; num < parts.length; num++ )
        {
            final String part = parts[num];

            if ( "".equals( part ) || ".".equals( part ) )
            {
                // nothing to do with . and empty parts
            }
            else if ( "..".equals( part ) )
            {
                // check if system is trying go up from /
                if ( currentItemEntity == null ) // already root
                {
                    // do not go up to root - does not have sense
                    currentItemEntity = MENUITEM_NOT_FOUND;
                    break; // searching
                }

                // go up
                currentItemEntity = currentItemEntity.getParent();
            }
            else
            {
                // something other than . or .. here
                Collection<MenuItemEntity> itemEntityList = getMenuItemChildren( currentItemEntity );

                for ( MenuItemEntity itemEntity : itemEntityList )
                {
                    if ( part.equalsIgnoreCase( itemEntity.getName() ) )
                    {
                        currentItemEntity = itemEntity;

                        if ( num == parts.length - 1 )
                        {   // success! found and it is last in path
                            break searching;
                        }
                        else
                        {
                            // found and it is not last in path
                            continue searching;
                        }
                    }
                }

                // did not find matching name in current folder
                currentItemEntity = MENUITEM_NOT_FOUND;
                break; // searching
            }
        }

        return currentItemEntity;
    }

}
