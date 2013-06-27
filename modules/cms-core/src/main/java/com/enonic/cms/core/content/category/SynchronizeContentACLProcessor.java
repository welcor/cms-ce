/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import com.enonic.cms.core.content.ContentACLSynchronizer;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentMap;
import com.enonic.cms.core.search.IndexTransactionService;

class SynchronizeContentACLProcessor
{
    private ContentACLSynchronizer contentACLSynchronizer;

    private ContentMap contentToSynchronize;

    private final IndexTransactionService indexTransactionService;

    SynchronizeContentACLProcessor( final ContentACLSynchronizer contentACLSynchronizer,
                                    final IndexTransactionService indexTransactionService )
    {
        this.contentACLSynchronizer = contentACLSynchronizer;
        this.indexTransactionService = indexTransactionService;
    }

    void setContentToSynchronize( ContentMap contentToSynchronize )
    {
        this.contentToSynchronize = contentToSynchronize;
    }

    void process( final SynchronizeContentACLCommand command )
    {
        for ( ContentEntity content : contentToSynchronize )
        {
            contentACLSynchronizer.synchronize( content, command.getContentACL() );
            indexTransactionService.registerUpdate( content.getKey(), true );
        }
    }
}
