package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentAccessControl;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.group.GroupKey;

class ModifyContentACLCommand
{
    private List<ContentKey> contentToUpdate;

    private List<ContentAccessControl> toBeAdded = new ArrayList<ContentAccessControl>();

    private List<ContentAccessControl> toBeModified = new ArrayList<ContentAccessControl>();

    private List<GroupKey> toBeRemoved = new ArrayList<GroupKey>();

    List<ContentKey> getContentToUpdate()
    {
        return contentToUpdate;
    }

    void contentToUpdate( List<ContentKey> contentToUpdate )
    {
        this.contentToUpdate = contentToUpdate;
    }

    void addToBeAdded( Iterable<ContentAccessControl> value )
    {
        for ( ContentAccessControl cac : value )
        {
            toBeAdded.add( cac );
        }
    }

    void addToBeModified( Iterable<ContentAccessControl> value )
    {
        for ( ContentAccessControl cac : value )
        {
            toBeModified.add( cac );
        }
    }

    void addToBeRemoved( GroupKey value )
    {
        toBeRemoved.add( value );
    }

    void addToBeRemoved( Iterable<GroupKey> groupKeys )
    {
        for ( GroupKey groupKey : groupKeys )
        {
            addToBeRemoved( groupKey );
        }
    }

    public List<ContentAccessControl> getToBeAdded()
    {
        return toBeAdded;
    }

    public List<ContentAccessControl> getToBeModified()
    {
        return toBeModified;
    }

    public List<GroupKey> getToBeRemoved()
    {
        return toBeRemoved;
    }
}
