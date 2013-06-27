/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import org.springframework.stereotype.Repository;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;

@Repository("menuItemAccessDao")
public final class MenuItemAccessEntityDao
    extends AbstractBaseEntityDao<MenuItemAccessEntity>
    implements MenuItemAccessDao
{
    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "MenuItemAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }
}
