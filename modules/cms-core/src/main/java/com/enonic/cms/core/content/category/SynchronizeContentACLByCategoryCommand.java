/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.List;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.content.ContentACL;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.store.dao.ContentDao;

class SynchronizeContentACLByCategoryCommand
{
    public boolean executeInOneTransaction = false;

    private static final int BATCH_SIZE = 500;

    private CategoryKey category;

    private ContentACL contentACL;

    public void contentAccessControl( ContentACL value )
    {
        this.contentACL = value;
    }

    public void category( CategoryKey value )
    {
        category = value;
    }

    void executeInBatches( final CategoryService categoryService, final ContentDao contentDao )
    {
        final List<ContentKey> contentToUpdate = contentDao.findContentKeysByCategory( category );
        final BatchedList<ContentKey> batchedList = new BatchedList<ContentKey>( contentToUpdate, BATCH_SIZE );
        while ( batchedList.hasMoreBatches() )
        {
            final SynchronizeContentACLCommand command = new SynchronizeContentACLCommand();
            command.contentToUpdate( batchedList.getNextBatch() );
            command.contentAccessControl( contentACL );
            if ( executeInOneTransaction )
            {
                categoryService.synchronizeContentACL_withoutRequiresNewPropagation_for_test_only( command );
            }
            else
            {
                categoryService.synchronizeContentACL( command );
            }
        }
    }
}
