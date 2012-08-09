package com.enonic.cms.core.content.category;


import java.util.SortedMap;

import com.enonic.cms.core.content.ContentACLSynchronizer;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

class SynchronizeContentACLProcessor
{
    private ContentACLSynchronizer contentACLSynchronizer;

    private SortedMap<ContentKey, ContentEntity> contentToSynchronize;

    SynchronizeContentACLProcessor( ContentACLSynchronizer contentACLSynchronizer )
    {
        this.contentACLSynchronizer = contentACLSynchronizer;
    }

    void setContentToSynchronize( SortedMap<ContentKey, ContentEntity> contentToSynchronize )
    {
        this.contentToSynchronize = contentToSynchronize;
    }

    void process( final SynchronizeContentACLCommand command )
    {
        for ( ContentEntity content : contentToSynchronize.values() )
        {
            contentACLSynchronizer.synchronize( content, command.getContentACL() );
        }
    }
}
