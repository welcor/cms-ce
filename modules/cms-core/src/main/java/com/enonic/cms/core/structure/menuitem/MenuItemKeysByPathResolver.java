package com.enonic.cms.core.structure.menuitem;


import java.util.Collection;

public class MenuItemKeysByPathResolver
{
    private MenuItemEntity menuItem;

    private boolean isFolder = false;

    /**
     * @param menuItem menu-item to resolve relative paths from
     */
    public MenuItemKeysByPathResolver( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    /**
     * <p>reads page key by menu path or all keys in folder </p>
     * <p>path may be absolute or relative format</p>
     * <p/>
     * Examples: <br/>
     * <p/>
     * <code>/</code> - all keys in root folder<br/>
     * <code>./</code> - all keys in current folder<br/>
     * <code>fldr</code> - key of fldr folder in current folder<br/>
     * <code>./fldr</code> - key of fldr folder in current folder<br/>
     * <code>../welcome/fldr</code> - more relative path<br/>
     * <code>../././welcome/./fldr/../fldr</code> - complex path<br/>
     * <p/>
     * if parent folder or relative folder does not exist function returns empty string
     *
     * @param path path to page
     * @return comma separated keys of items in folder or empty
     */
    public String getPageKeyByPath( String path )
    {
        final String SEPARATOR = "/";

        MenuItemEntity currentItemEntity = null;
        Collection<MenuItemEntity> itemEntityList;

        isFolder = path.endsWith( SEPARATOR );
        String[] parts = path.split( SEPARATOR ); // split works strange. "////" will return ZERO parts !

        if ( parts.length == 0 || "".equals( parts[0] ) )
        { //  absolute path in format /root/folder
            itemEntityList = menuItem.getSite().getTopMenuItems();
        }
        else
        { // relative path in format ./relative/path or just relative/path
            // get fresh copy of current menu item from hibernate cache
            currentItemEntity = menuItem;
            itemEntityList = currentItemEntity.getChildren();
        }

        searching:
        for ( int num = 0; num < parts.length; num++ )
        {
            String part = parts[num];

            if ( ".".equals( part ) || "".equals( part ) )
            {
                // nothing to do with . and empty parts
            }

            else if ( "..".equals( part ) )
            {
                // check if system is trying go up to /
                if ( currentItemEntity == null ) // already root
                {
                    isFolder = false; // also do not show content of root folder
                    break; // searching
                }

                // go up
                currentItemEntity = currentItemEntity.getParent();

                // read items entity list
                if ( currentItemEntity == null )
                { // read contents of root folder
                    itemEntityList = menuItem.getSite().getTopMenuItems();
                }
                else
                { // just enter folder
                    itemEntityList = currentItemEntity.getChildren();
                }
            }

            else

            {
                // something other than . or .. here
                for ( MenuItemEntity itemEntity : itemEntityList )
                {
                    if ( part.equalsIgnoreCase( itemEntity.getName() ) )
                    {
                        if ( num == parts.length - 1 )
                        { // found
                            if ( isFolder )
                            {
                                itemEntityList = itemEntity.getChildren();
                            }
                            else
                            {
                                currentItemEntity = itemEntity;
                            }

                            // go build result string
                            break searching;
                        }
                        else
                        {
                            currentItemEntity = itemEntity;
                            itemEntityList = currentItemEntity.getChildren();
                            continue searching;
                        }
                    }
                }

                // did not find matching name in current folder
                currentItemEntity = null;
                break;
            }
        }

        String result = "";

        if ( isFolder )
        {
            String separator = "";
            for ( MenuItemEntity mi : itemEntityList )
            {
                result = result + separator + mi.getKey().toInt();
                separator = ",";
            }
        }
        else if ( currentItemEntity != null )
        {
            result = "" + currentItemEntity.getKey().toInt();
        }

        return result;
    }

}
