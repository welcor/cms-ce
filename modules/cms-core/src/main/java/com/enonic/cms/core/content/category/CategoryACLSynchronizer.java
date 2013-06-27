/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.GroupDao;

class CategoryACLSynchronizer
{
    private final GroupDao groupDao;

    CategoryACLSynchronizer( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    void synchronize( final CategoryACL bluePrintACL, final CategoryEntity category )
    {
        remove( bluePrintACL, category );
        updateExisting( bluePrintACL, category );
        addMissing( bluePrintACL, category );
    }

    private void addMissing( final CategoryACL bluePrintACL, final CategoryEntity category )
    {
        for ( CategoryAccessControl car : bluePrintACL )
        {
            if ( !category.hasAccessForGroup( car.getGroupKey() ) )
            {
                final GroupEntity group = groupDao.findByKey( car.getGroupKey() );
                category.addAccessRight( CategoryAccessEntity.create( category.getKey(), group, car ) );
            }
        }
    }

    private void updateExisting( final CategoryACL bluePrintACL, final CategoryEntity category )
    {
        for ( CategoryAccessEntity categoryAccess : category.getAccessRights().values() )
        {
            final CategoryAccessControl acBluePrint = bluePrintACL.get( categoryAccess.getKey().getGroupKey() );
            if ( acBluePrint != null )
            {
                categoryAccess.setAccess( acBluePrint );
            }
        }
    }

    private void remove( final CategoryACL bluePrintACL, final CategoryEntity category )
    {
        final List<GroupKey> accesesToRemove = new ArrayList<GroupKey>();
        for ( CategoryAccessEntity categoryAccess : category.getAccessRights().values() )
        {
            if ( !bluePrintACL.hasAccessForGroup( categoryAccess.getKey().getGroupKey() ) )
            {
                accesesToRemove.add( categoryAccess.getKey().getGroupKey() );
            }
        }
        category.removeAcessRights( accesesToRemove );
    }
}
