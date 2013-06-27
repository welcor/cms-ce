/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.content.ContentAccessControl;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.dao.ContentDao;

/**
 *
 */
public class ModifyCategoryACLCommand
{
    public static boolean executeInOneTransaction = false;

    private static final int BATCH_SIZE = 500;

    private UserKey updater;

    private List<CategoryKey> categoriesToUpdate = new ArrayList<CategoryKey>();

    private List<CategoryAccessControl> toBeAdded = new ArrayList<CategoryAccessControl>();

    private List<CategoryAccessControl> toBeModified = new ArrayList<CategoryAccessControl>();

    private List<GroupKey> toBeRemoved = new ArrayList<GroupKey>();

    private boolean includeContent;

    public void setUpdater( UserKey updater )
    {
        this.updater = updater;
    }

    public UserKey getUpdater()
    {
        return updater;
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

    public void addToBeAdded( CategoryAccessControl value )
    {
        this.toBeAdded.add( value );
    }

    public void addToBeModified( CategoryAccessControl value )
    {
        this.toBeModified.add( value );
    }

    public void addToBeRemoved( GroupKey value )
    {
        this.toBeRemoved.add( value );
    }

    public List<CategoryAccessControl> getToBeAdded()
    {
        return toBeAdded;
    }

    public List<CategoryAccessControl> getToBeModified()
    {
        return toBeModified;
    }

    public List<GroupKey> getToBeRemoved()
    {
        return toBeRemoved;
    }

    public void executeInBatches( final CategoryService categoryService, final ContentDao contentDao )
    {
        final BatchedList<CategoryKey> batchedList = new BatchedList<CategoryKey>( categoriesToUpdate, BATCH_SIZE );
        while ( batchedList.hasMoreBatches() )
        {
            final List<CategoryKey> nextBatch = batchedList.getNextBatch();

            final ModifyCategoryACLCommand command = new ModifyCategoryACLCommand();
            command.updater = this.updater;
            command.toBeAdded = this.toBeAdded;
            command.toBeModified = this.toBeModified;
            command.toBeRemoved = this.toBeRemoved;
            command.categoriesToUpdate = nextBatch;
            if ( executeInOneTransaction )
            {
                categoryService.modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( command );
            }
            else
            {
                categoryService.modifyCategoryACL( command );
            }

            if ( isIncludeContent() )
            {
                for ( CategoryKey category : command.categoriesToUpdate )
                {
                    final ModifyContentACLByCategoryCommand modifyContentACLByCategoryCommand = new ModifyContentACLByCategoryCommand();
                    modifyContentACLByCategoryCommand.executeInOneTransaction = executeInOneTransaction;
                    modifyContentACLByCategoryCommand.category( category );

                    for ( CategoryAccessControl carToBeAdded : command.getToBeAdded() )
                    {
                        modifyContentACLByCategoryCommand.addToBeAdded( ContentAccessControl.create( carToBeAdded ) );
                    }
                    for ( CategoryAccessControl carToBeModified : command.getToBeModified() )
                    {
                        modifyContentACLByCategoryCommand.addToBeModified( ContentAccessControl.create( carToBeModified ) );
                    }
                    for ( GroupKey toBeRemoved : command.getToBeRemoved() )
                    {
                        modifyContentACLByCategoryCommand.addToBeRemoved( toBeRemoved );
                    }

                    modifyContentACLByCategoryCommand.executeInBatches( categoryService, contentDao );
                }
            }
        }
    }
}
