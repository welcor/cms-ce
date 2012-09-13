package com.enonic.cms.core.content.category;


import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.search.IndexTransactionService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.store.dao.GroupDao;

class ModifyCategoryACLCommandProcessor
{
    private UpdateCategoryAccessChecker updateCategoryAccessChecker;

    private GroupDao groupDao;

    private SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate;

    private final IndexTransactionService indexTransactionService;


    ModifyCategoryACLCommandProcessor( final GroupDao groupDao, final UpdateCategoryAccessChecker updateCategoryAccessChecker,
                                       final IndexTransactionService indexTransactionService )
    {
        this.groupDao = groupDao;
        this.updateCategoryAccessChecker = updateCategoryAccessChecker;
        this.indexTransactionService = indexTransactionService;
    }

    void setCategoriesToUpdate( SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate )
    {
        this.categoriesToUpdate = categoriesToUpdate;
    }

    void process( final ModifyCategoryACLCommand command )
    {
        checkUpdateCategoriesAccess();
        modify( command );
    }

    private void modify( final ModifyCategoryACLCommand command )
    {
        indexTransactionService.startTransaction();

        for ( CategoryEntity category : categoriesToUpdate.values() )
        {
            category.removeAcessRights( command.getToBeRemoved() );
            add( command, category );
            modify( command, category );

            for ( ContentEntity content : category.getContents() )
            {
                indexTransactionService.registerUpdate( content.getKey(), true );
            }
        }
    }

    private void modify( final ModifyCategoryACLCommand command, final CategoryEntity category )
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

    private void add( final ModifyCategoryACLCommand command, final CategoryEntity category )
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
