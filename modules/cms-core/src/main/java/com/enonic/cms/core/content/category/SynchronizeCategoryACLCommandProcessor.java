package com.enonic.cms.core.content.category;


import java.util.SortedMap;

import com.enonic.cms.store.dao.GroupDao;

class SynchronizeCategoryACLCommandProcessor
{
    private UpdateCategoryAccessChecker updateCategoryAccessChecker;

    private GroupDao groupDao;

    private SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate;

    SynchronizeCategoryACLCommandProcessor( GroupDao groupDao, UpdateCategoryAccessChecker updateCategoryAccessChecker )
    {
        this.groupDao = groupDao;
        this.updateCategoryAccessChecker = updateCategoryAccessChecker;
    }

    void setCategoriesToUpdate( SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate )
    {
        this.categoriesToUpdate = categoriesToUpdate;
    }

    void process( final SynchronizeCategoryACLCommand command )
    {
        checkUpdateCategoriesAccess();
        synchronize( command );
    }

    private void synchronize( final SynchronizeCategoryACLCommand command )
    {
        final CategoryACLSynchronizer synchronizer = new CategoryACLSynchronizer( groupDao );
        for ( CategoryEntity category : categoriesToUpdate.values() )
        {
            synchronizer.synchronize( command.getCategoryACL(), category );
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
