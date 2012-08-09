package com.enonic.cms.core.content.category;


import java.util.SortedMap;

import com.enonic.cms.core.content.ContentAccessControl;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.GroupDao;

class ModifyContentACLCommandProcessor
{
    private GroupDao groupDao;

    private SortedMap<ContentKey, ContentEntity> contentToSynchronize;

    ModifyContentACLCommandProcessor( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    void setContentToSynchronize( final SortedMap<ContentKey, ContentEntity> contentToSynchronize )
    {
        this.contentToSynchronize = contentToSynchronize;
    }

    void process( final ModifyContentACLCommand command )
    {
        for ( final ContentEntity content : contentToSynchronize.values() )
        {
            remove( command.getToBeRemoved(), content );
            modify( command.getToBeModified(), content );
            add( command.getToBeAdded(), content );
        }
    }

    private void remove( final Iterable<GroupKey> toBeRemoved, final ContentEntity content )
    {
        for ( GroupKey groupKey : toBeRemoved )
        {
            content.removeContentAccessRightByGroup( groupKey );
        }
    }

    private void modify( final Iterable<ContentAccessControl> it, final ContentEntity content )
    {
        for ( ContentAccessControl cac : it )
        {
            overwriteOrAddNew( content, cac );
        }
    }

    private void add( final Iterable<ContentAccessControl> it, final ContentEntity content )
    {
        for ( ContentAccessControl cac : it )
        {
            overwriteOrAddNew( content, cac );
        }
    }

    private void overwriteOrAddNew( final ContentEntity content, final ContentAccessControl cac )
    {
        ContentAccessEntity access = content.getContentAccessRight( cac.getGroup() );
        if ( access != null )
        {
            access.overwriteRightsFrom( cac );
        }
        else
        {
            access = new ContentAccessEntity();
            final GroupEntity group = groupDao.findByKey( cac.getGroup() );
            if ( group == null )
            {
                throw new IllegalArgumentException( "Group suddenly no longer exists: " + cac.getGroup() );
            }
            access.setGroup( group );
            access.setReadAccess( cac.isRead() );
            access.setUpdateAccess( cac.isUpdate() );
            access.setDeleteAccess( cac.isDelete() );
            content.addContentAccessRight( access );
        }
    }

}
