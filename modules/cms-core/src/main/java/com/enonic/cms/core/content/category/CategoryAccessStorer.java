package com.enonic.cms.core.content.category;

import java.util.List;
import java.util.Map;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.GroupDao;

public class CategoryAccessStorer
{
    private GroupDao groupDao;

    private final GroupEntity administrator;

    public CategoryAccessStorer( GroupDao groupDao )
    {
        this.groupDao = groupDao;
        this.administrator = groupDao.findBuiltInAdministrator();
    }

    void applyAccessRightsFromParent( CategoryEntity parentCategory, CategoryEntity category )
    {
        Map<GroupKey, CategoryAccessEntity> accessRights = parentCategory.getAccessRights();
        for ( GroupKey group : accessRights.keySet() )
        {
            CategoryAccessEntity parentAccessRight = accessRights.get( group );
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
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

    void applyGivenAccessRights( List<CategoryAccessRights> categoryAccessRightsList, CategoryEntity category )
    {
        for ( CategoryAccessRights aRight : categoryAccessRightsList )
        {
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( category.getKey(), aRight.getGroupKey() ) );
            accessRight.setGroup( groupDao.findByKey( aRight.getGroupKey() ) );
            accessRight.setAdminAccess( aRight.isAdminAccess() );
            accessRight.setAdminBrowseAccess( aRight.isAdminBrowseAccess() );
            accessRight.setCreateAccess( aRight.isCreateAccess() );
            accessRight.setPublishAccess( aRight.isPublishAccess() );
            accessRight.setReadAccess( aRight.isReadAccess() );
            category.addAccessRight( accessRight );
        }

        ensureAccessRightForAdministratorGroup( category );
    }

    void ensureAccessRightForAdministratorGroup( CategoryEntity category )
    {
        if ( category.getAccessRights() == null || category.getAccessRights().isEmpty() )
        {
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
            accessRight.setGroup( administrator );
            setAllRightsToTrue( accessRight );
            category.addAccessRight( accessRight );
        }
        else if ( category.getAccessRights().size() > 0 )
        {
            boolean isAdministratorAccessRightsExist = false;
            for ( CategoryAccessEntity categoryAccess : category.getAccessRights().values() )
            {
                if ( categoryAccess.getKey().getGroupKey().equals( administrator.getGroupKey() ) )
                {
                    setAllRightsToTrue( categoryAccess );
                    isAdministratorAccessRightsExist = true;
                }
            }
            if ( !isAdministratorAccessRightsExist )
            {
                CategoryAccessEntity accessRight = new CategoryAccessEntity();
                accessRight.setGroup( administrator );
                accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
                setAllRightsToTrue( accessRight );
                category.addAccessRight( accessRight );
            }
        }
    }

    private void setAllRightsToTrue( CategoryAccessEntity accessRight )
    {
        accessRight.setAdminAccess( true );
        accessRight.setAdminBrowseAccess( true );
        accessRight.setCreateAccess( true );
        accessRight.setPublishAccess( true );
        accessRight.setReadAccess( true );
    }
}
