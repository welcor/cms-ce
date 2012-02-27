/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;


public class MenuItemKeyUserType
    extends AbstractIntegerBasedUserType<MenuItemKey>
{
    public MenuItemKeyUserType()
    {
        super( MenuItemKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public MenuItemKey get( int value )
    {
        return new MenuItemKey( value );
    }

    public Integer getIntegerValue( MenuItemKey value )
    {
        return value.toInt();
    }
}