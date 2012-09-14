package com.enonic.cms.core.content.category;


import java.util.SortedMap;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.store.dao.GroupDao;

class ModifyCategoryACLCommandProcessor
{
    private UpdateCategoryAccessChecker updateCategoryAccessChecker;

    private GroupDao groupDao;

    private SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate;

    ModifyCategoryACLCommandProcessor( final GroupDao groupDao, final UpdateCategoryAccessChecker updateCategoryAccessChecker )
    {
        this.groupDao = groupDao;
        this.updateCategoryAccessChecker = updateCategoryAccessChecker;
    }

    void setCategoriesToUpdate( SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate )
    {
        this.categoriesToUpdate = categoriesToUpdate;
    }

    void process( final ModifyCategoryACLCommand command )
    {
        checkUpdateCategoriesAccess();

        for ( CategoryEntity category : categoriesToUpdate.values() )
        {
            category.removeAcessRights( command.getToBeRemoved() );
            processThoseToBeAdded( command, category );
            processThoseToModified( command, category );
        }
    }

    private void processThoseToModified( final ModifyCategoryACLCommand command, final CategoryEntity category )
    {
        // modify (and add if not already existing)
        for ( CategoryAccessControl categoryAccessControl : command.getToBeModified() )
        {
            GroupEntity group = groupDao.findByKey( categoryAccessControl.getGroupKey() );
            if ( category.hasAccessForGroup( categoryAccessControl.getGroupKey() ) )
            {
                CategoryAccessEntity access = category.getCategoryAccess( categoryAccessControl.getGroupKey() );
                access.setAccess( categoryAccessControl );
            }
            else
            {
                category.addAccessRight( CategoryAccessEntity.create( category.getKey(), group, categoryAccessControl ) );
            }
        }
    }

    private void processThoseToBeAdded( final ModifyCategoryACLCommand command, final CategoryEntity category )
    {
        // add (and modify if already existing)
        for ( CategoryAccessControl categoryAccessControl : command.getToBeAdded() )
        {
            GroupEntity group = groupDao.findByKey( categoryAccessControl.getGroupKey() );
            if ( !category.hasAccessForGroup( categoryAccessControl.getGroupKey() ) )
            {
                category.addAccessRight( CategoryAccessEntity.create( category.getKey(), group, categoryAccessControl ) );
            }
            else
            {
                CategoryAccessEntity access = category.getCategoryAccess( categoryAccessControl.getGroupKey() );
                access.setAccess( categoryAccessControl );
            }
        }
    }

    private void checkUpdateCategoriesAccess()
        throws CreateCategoryAccessException
    {
        for ( CategoryEntity categoryToUpdate : categoriesToUpdate.values() )
        {
            updateCategoryAccessChecker.checkAccessToUpdateCategory( categoryToUpdate );
        }
    }
}
