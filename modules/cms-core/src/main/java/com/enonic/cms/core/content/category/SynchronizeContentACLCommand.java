/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.List;

import com.enonic.cms.core.content.ContentACL;
import com.enonic.cms.core.content.ContentKey;

class SynchronizeContentACLCommand
{
    private List<ContentKey> contentToUpdate;

    private ContentACL contentACL;

    List<ContentKey> getContentToUpdate()
    {
        return contentToUpdate;
    }

    void contentToUpdate( List<ContentKey> contentToUpdate )
    {
        this.contentToUpdate = contentToUpdate;
    }

    void contentAccessControl( ContentACL value )
    {
        this.contentACL = value;
    }

    ContentACL getContentACL()
    {
        return contentACL;
    }
}
