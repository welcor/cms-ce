/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

/**
 * A class that calculates the difference between a set of group keys for requested memberships and a set of group keys for existing
 * membership provided add and remove commands that can be used to synchronize the memberships with the UserStoreService.
 */
public class GroupMembershipDiffResolver
{
    private GroupEntity groupToUpdate;

    private Set<GroupKey> existingMemberships;

    public GroupMembershipDiffResolver( GroupEntity groupToUpdate )
    {
        this.groupToUpdate = groupToUpdate;
        setExistingMemberships();
    }

    private void setExistingMemberships()
    {
        if ( existingMemberships == null )
        {
            existingMemberships = new HashSet<GroupKey>();
        }

        for ( GroupEntity existingMembership : groupToUpdate.getMemberships( true ) )
        {
            existingMemberships.add( existingMembership.getGroupKey() );
        }
    }

    public Set<GroupKey> resolveGroupsToJoin( Set<GroupKey> requestedMemberships )
    {
        final Set<GroupKey> groupsToAddTo = new HashSet<GroupKey>( requestedMemberships );
        groupsToAddTo.removeAll( existingMemberships );

        return groupsToAddTo;
    }

    public Set<GroupKey> resolveGroupsToLeave( Set<GroupKey> requestedMemberships )
    {
        final Set<GroupKey> groupsToRemoveFrom = new HashSet<GroupKey>( existingMemberships );
        groupsToRemoveFrom.removeAll( requestedMemberships );

        return groupsToRemoveFrom;
    }
}
