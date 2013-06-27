/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import org.springframework.util.Assert;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.user.remote.RemoteGroup;

public class GroupSynchronizer
    extends AbstractBaseGroupSynchronizer
{
    public GroupSynchronizer( final UserStoreEntity userStore, final boolean syncMemberships, final boolean syncMembers )
    {
        super( userStore, true, syncMemberships, syncMembers );
    }

    public void synchronize( final GroupEntity localGroup, final MemberCache memberCache )
    {
        Assert.notNull( localGroup );

        final RemoteGroup remoteGroup = remoteUserStorePlugin.getGroup( localGroup.getName() );
        if ( remoteGroup == null )
        {
            deleteGroup( localGroup );
        }
        else if ( !remoteGroup.getSync().equals( localGroup.getSyncValue() ) )
        {
            // No matching sync value - group no longer in userstore , we delete it
            deleteGroup( localGroup );
        }
        else
        {
            resurrectGroup( localGroup );

            if ( syncMembers )
            {
                syncGroupMembers( localGroup, remoteGroup, memberCache );
            }
            if ( syncMemberships )
            {
                syncGroupMemberships( localGroup, remoteGroup, memberCache );
            }
        }
    }

    private void deleteGroup( final GroupEntity localGroup )
    {
        if ( !localGroup.isDeleted() )
        {
            localGroup.setDeleted( 1 );
        }
    }
}
