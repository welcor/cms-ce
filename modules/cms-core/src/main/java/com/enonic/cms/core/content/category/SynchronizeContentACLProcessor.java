package com.enonic.cms.core.content.category;


import java.util.Map;

import com.enonic.cms.core.content.ContentACLSynchronizer;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.IndexTransactionService;

class SynchronizeContentACLProcessor
{
    private ContentACLSynchronizer contentACLSynchronizer;

    private Map<ContentKey, ContentEntity> contentToSynchronize;

    private final IndexTransactionService indexTransactionService;

    SynchronizeContentACLProcessor( final ContentACLSynchronizer contentACLSynchronizer,
                                    final IndexTransactionService indexTransactionService )
    {
        this.contentACLSynchronizer = contentACLSynchronizer;
        this.indexTransactionService = indexTransactionService;
    }

    void setContentToSynchronize( Map<ContentKey, ContentEntity> contentToSynchronize )
    {
        this.contentToSynchronize = contentToSynchronize;
    }

    void process( final SynchronizeContentACLCommand command )
    {
        for ( ContentEntity content : contentToSynchronize.values() )
        {
            contentACLSynchronizer.synchronize( content, command.getContentACL() );
            indexTransactionService.registerUpdate( content.getKey(), true );
        }
    }
}
