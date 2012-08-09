package com.enonic.cms.core.content.category;

import java.util.Map;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.GroupDao;

class CategoryAccessStorer
{
    private GroupDao groupDao;

    private final GroupEntity administrator;

    CategoryAccessStorer( GroupDao groupDao )
    {
        this.groupDao = groupDao;
        this.administrator = groupDao.findBuiltInAdministrator();
    }

    void applyAccessRightsFromParent( final CategoryEntity parentCategory, final CategoryEntity category )
    {
        Map<GroupKey, CategoryAccessEntity> accessRights = parentCategory.getAccessRights();
        for ( GroupKey group : accessRights.keySet() )
        {
            CategoryAccessEntity parentAccessRight = accessRights.get( group );
            CategoryAccessEntity accessRight =
                CategoryAccessEntity.create( category.getKey(), parentAccessRight.getGroup(), parentAccessRight.toAccessRights() );
            accessRight.setKey( new CategoryAccessKey( category.getKey(), group ) );
            accessRight.setGroup( parentAccessRight.getGroup() );
            accessRight.setAdminAccess( parentAccessRight.isAdminAccess() );
            accessRight.setAdminBrowseAccess( parentAccessRight.isAdminBrowseAccess() );
            accessRight.setCreateAccess( parentAccessRight.isCreateAccess() );
            accessRight.setPublishAccess( parentAccessRight.isPublishAccess() );
            accessRight.setReadAccess( parentAccessRight.isReadAccess() );
            category.addAccessRight( accessRight );
        }

        ensureAccessRightForAdministratorGroup( category );
    }

    void applyGivenAccessRights( final CategoryACL categoryACL, final CategoryEntity category )
    {
        for ( CategoryAccessControl aRight : categoryACL )
        {
            GroupEntity group = groupDao.findByKey( aRight.getGroupKey() );
            CategoryAccessEntity accessRight = CategoryAccessEntity.create( category.getKey(), group, aRight );
            category.addAccessRight( accessRight );
        }

        ensureAccessRightForAdministratorGroup( category );
    }

    void ensureAccessRightForAdministratorGroup( final CategoryEntity category )
    {
        if ( category.getAccessRights() == null || category.getAccessRights().isEmpty() )
        {
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
            accessRight.setGroup( administrator );
            accessRight.setAll( true );
            category.addAccessRight( accessRight );
        }
        else if ( category.getAccessRights().size() > 0 )
        {
            boolean isAdministratorAccessRightsExist = false;
            for ( CategoryAccessEntity categoryAccess : category.getAccessRights().values() )
            {
                if ( categoryAccess.getKey().getGroupKey().equals( administrator.getGroupKey() ) )
                {
                    categoryAccess.setAll( true );
                    isAdministratorAccessRightsExist = true;
                }
            }
            if ( !isAdministratorAccessRightsExist )
            {
                CategoryAccessEntity accessRight = new CategoryAccessEntity();
                accessRight.setGroup( administrator );
                accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
                accessRight.setAll( true );
                category.addAccessRight( accessRight );
            }
        }
    }

}
