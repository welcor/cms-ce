/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.Collection;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.userstore.GroupStorer;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;

public class GroupsSynchronizer
    extends AbstractBaseGroupSynchronizer
{
    private GroupStorer groupStorer;

    public GroupsSynchronizer( final SynchronizeStatus status, final UserStoreEntity userStore, boolean syncGroup, boolean syncMemberships,
                               boolean syncMembers )
    {
        super( userStore, syncGroup, syncMemberships, syncMembers );
        setStatusCollector( status );
    }

    public void synchronize( final Collection<RemoteGroup> remoteGroupsToSync, final MemberCache memberCache )
    {
        for ( final RemoteGroup remoteGroup : remoteGroupsToSync )
        {
            createUpdateOrResurrectLocalGroup( remoteGroup, memberCache );
        }
    }

    private void createUpdateOrResurrectLocalGroup( final RemoteGroup remoteGroup, final MemberCache memberCache )
    {
        GroupEntity localGroup = findGroupBySyncValue( remoteGroup.getSync() );

        if ( syncGroup )
        {
            if ( localGroup == null )
            {
                localGroup = createGroup( remoteGroup );
                status.groupCreated();
            }
            else
            {
                final boolean resurrected = resurrectGroup( localGroup );
                status.groupUpdated( resurrected );
            }
        }
        if ( syncMemberships )
        {
            syncGroupMemberships( localGroup, remoteGroup, memberCache );
        }
    }

    private GroupEntity findGroupBySyncValue( String syncValue )
    {
        final GroupSpecification spec = new GroupSpecification();
        spec.setUserStoreKey( userStore.getKey() );
        spec.setSyncValue( syncValue );
        spec.setType( GroupType.USERSTORE_GROUP );

        return groupDao.findSingleBySpecification( spec );
    }

    private GroupEntity createGroup( final RemoteGroup remoteGroup )
    {
        final StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setName( remoteGroup.getId() );
        storeNewGroupCommand.setSyncValue( remoteGroup.getSync() );
        storeNewGroupCommand.setRestriced( true );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setUserStoreKey( userStore.getKey() );
        GroupKey groupKey = groupStorer.storeNewGroup( storeNewGroupCommand );
        return groupDao.findByKey( groupKey );
    }

    public void setGroupStorer( GroupStorer groupStorer )
    {
        this.groupStorer = groupStorer;
    }
}
