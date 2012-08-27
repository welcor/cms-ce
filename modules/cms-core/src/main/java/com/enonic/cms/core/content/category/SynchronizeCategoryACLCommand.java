package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.content.ContentACL;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.dao.ContentDao;

/**
 * Sets given ACL on all given categories and optionally contents within each category.
 */
public class SynchronizeCategoryACLCommand
{
    public static boolean executeInOneTransaction = false;

    private static final int BATCH_SIZE = 500;

    private UserKey updater;

    private List<CategoryKey> categoriesToUpdate = new ArrayList<CategoryKey>();

    private CategoryACL categoryACL = new CategoryACL();

    private boolean includeContent;

    public void setUpdater( UserKey updater )
    {
        this.updater = updater;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public void setCategory( CategoryKey category )
    {
        this.categoriesToUpdate.clear();
        this.categoriesToUpdate.add( category );
    }

    public void addCategory( CategoryKey category )
    {
        this.categoriesToUpdate.add( category );
    }

    public List<CategoryKey> getCategoriesToUpdate()
    {
        return categoriesToUpdate;
    }

    public void includeContent()
    {
        this.includeContent = true;
    }

    public boolean isIncludeContent()
    {
        return includeContent;
    }

    public CategoryACL getCategoryACL()
    {
        return categoryACL;
    }

    public void addAccessControlList( Iterable<CategoryAccessControl> it )
    {
        for ( CategoryAccessControl cac : it )
        {
            categoryACL.add( cac );
        }
    }

    public void executeInBatches( final CategoryService categoryService, final ContentDao contentDao )
    {
        final BatchedList<CategoryKey> batchedList = new BatchedList<CategoryKey>( categoriesToUpdate, BATCH_SIZE );
        while ( batchedList.hasMoreBatches() )
        {
            final SynchronizeCategoryACLCommand command = new SynchronizeCategoryACLCommand();
            command.updater = this.updater;
            command.categoryACL = this.categoryACL;
            command.categoriesToUpdate = batchedList.getNextBatch();
            if ( executeInOneTransaction )
            {
                categoryService.synchronizeCategoryACL_withoutRequiresNewPropagation_for_test_only( command );
            }
            else
            {
                categoryService.synchronizeCategoryACL( command );
            }

            if ( isIncludeContent() )
            {
                for ( CategoryKey category : command.categoriesToUpdate )
                {
                    final SynchronizeContentACLByCategoryCommand synchronizeContentACLByCategoryCommand =
                        new SynchronizeContentACLByCategoryCommand();
                    synchronizeContentACLByCategoryCommand.executeInOneTransaction = executeInOneTransaction;

                    final ContentACL contentACL = ContentACL.create( this.categoryACL );
                    synchronizeContentACLByCategoryCommand.contentAccessControl( contentACL );
                    synchronizeContentACLByCategoryCommand.category( category );
                    synchronizeContentACLByCategoryCommand.executeInBatches( categoryService, contentDao );
                }
            }
        }
    }
}
