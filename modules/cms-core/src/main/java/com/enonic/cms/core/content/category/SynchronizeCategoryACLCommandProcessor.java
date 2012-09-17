package com.enonic.cms.core.content.category;


import java.util.List;
import java.util.SortedMap;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.IndexTransactionService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;

class SynchronizeCategoryACLCommandProcessor
{
    private UpdateCategoryAccessChecker updateCategoryAccessChecker;

    private GroupDao groupDao;

    private SortedMap<CategoryKey, CategoryEntity> categoriesToUpdate;

    private final IndexTransactionService indexTransactionService;

    private final ContentDao contentDao;

    SynchronizeCategoryACLCommandProcessor( final GroupDao groupDao, final UpdateCategoryAccessChecker updateCategoryAccessChecker,
                                            final IndexTransactionService indexTransactionService, final ContentDao contentDao )
    {
        this.groupDao = groupDao;
        this.updateCategoryAccessChecker = updateCategoryAccessChecker;
        this.indexTransactionService = indexTransactionService;
        this.contentDao = contentDao;
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

            indexTransactionService.registerUpdate( contentDao.findContentKeysByCategory( category.getKey() ), true );
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
