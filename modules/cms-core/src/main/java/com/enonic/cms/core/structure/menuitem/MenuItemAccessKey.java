/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.security.group.GroupKey;

public class MenuItemAccessKey
    implements Serializable
{
    private MenuItemKey menuItemKey;

    private GroupKey groupKey;

    public MenuItemAccessKey()
    {
    }

    public MenuItemAccessKey( MenuItemKey menuItemKey, GroupKey groupKey )
    {
        this.menuItemKey = menuItemKey;
        this.groupKey = groupKey;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemAccessKey ) )
        {
            return false;
        }

        MenuItemAccessKey that = (MenuItemAccessKey) o;

        if ( menuItemKey.toInt() != that.getMenuItemKey().toInt() )
        {
            return false;
        }
        if ( !groupKey.equals( that.getGroupKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 453, 335 ).append( menuItemKey.toInt() ).append( groupKey ).toHashCode();
    }
}
