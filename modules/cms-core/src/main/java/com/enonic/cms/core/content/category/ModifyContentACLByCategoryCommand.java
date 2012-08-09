package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.content.ContentAccessControl;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.ContentDao;

class ModifyContentACLByCategoryCommand
{
    public boolean executeInOneTransaction = false;

    private static final int BATCH_SIZE = 500;

    private CategoryKey category;

    private List<ContentAccessControl> toBeAdded = new ArrayList<ContentAccessControl>();

    private List<ContentAccessControl> toBeModified = new ArrayList<ContentAccessControl>();

    private List<GroupKey> toBeRemoved = new ArrayList<GroupKey>();

    public void category( CategoryKey value )
    {
        category = value;
    }

    public void addToBeAdded( ContentAccessControl value )
    {
        toBeAdded.add( value );
    }

    public void addToBeModified( ContentAccessControl value )
    {
        toBeModified.add( value );
    }

    public void addToBeRemoved( final GroupKey value )
    {
        toBeRemoved.add( value );
    }

    void executeInBatches( final CategoryService categoryService, final ContentDao contentDao )
    {
        final List<ContentKey> contentToUpdate = contentDao.findContentKeysByCategory( category );

        final BatchedList<ContentKey> batchedList = new BatchedList<ContentKey>( contentToUpdate, BATCH_SIZE );
        while ( batchedList.hasMoreBatches() )
        {
            final ModifyContentACLCommand command = new ModifyContentACLCommand();
            command.contentToUpdate( batchedList.getNextBatch() );
            command.addToBeRemoved( this.toBeRemoved );
            command.addToBeAdded( this.toBeAdded );
            command.addToBeModified( this.toBeModified );

            if ( executeInOneTransaction )
            {
                categoryService.modifyContentACL( command );
            }
            else
            {
                categoryService.modifyContentACLInNewTX( command );
            }
        }
    }
}
