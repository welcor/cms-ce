/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;

import static org.junit.Assert.*;

public class GroupMembershipDiffResolverTest
{
    private GroupEntity userGroup;

    @Before
    public void setUp()
    {
        userGroup = createGroup( "userGroup1", "group for user 1" );
    }

    @Test
    public void testResolveGroupsToJoin()
    {
        // setup
        userGroup.addMembership( createGroup( "group100" ) );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups =
            Sets.newHashSet( new GroupKey( "group100" ), new GroupKey( "group700" ), new GroupKey( "group888" ) );
        Set<GroupKey> groupsToJoin = resolver.resolveGroupsToJoin( requestedGroups );

        // verify
        Set<GroupKey> expectedGroupsToJoin = new HashSet<GroupKey>();
        expectedGroupsToJoin.add( new GroupKey( "group700" ) );
        expectedGroupsToJoin.add( new GroupKey( "group888" ) );

        assertEquals( expectedGroupsToJoin, groupsToJoin );
    }

    @Test
    public void testResolveGroupsToLeave()
    {
        // setup
        userGroup.addMembership( createGroup( "group100" ) );
        userGroup.addMembership( createGroup( "group700" ) );
        userGroup.addMembership( createGroup( "group888" ) );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups = Sets.newHashSet( new GroupKey( "group100" ) );
        Set<GroupKey> groupsToLeave = resolver.resolveGroupsToLeave( requestedGroups );

        // verify
        Set<GroupKey> expectedGroupsToLeave = Sets.newHashSet( new GroupKey( "group700" ), new GroupKey( "group888" ) );
        assertEquals( expectedGroupsToLeave, groupsToLeave );
    }

    @Test
    public void testResolveGroupsDiff()
    {
        // setup
        userGroup.addMembership( createGroup( "group100" ) );
        userGroup.addMembership( createGroup( "group700" ) );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups = Sets.newHashSet( new GroupKey( "group700" ), new GroupKey( "group800" ), new GroupKey( "group888" ),
                                                         new GroupKey( "group999" ) );
        Set<GroupKey> groupsToJoin = resolver.resolveGroupsToJoin( requestedGroups );
        Set<GroupKey> groupsToLeave = resolver.resolveGroupsToLeave( requestedGroups );

        // verify
        Set<GroupKey> expectedGroupsToJoin =
            Sets.newHashSet( new GroupKey( "group800" ), new GroupKey( "group888" ), new GroupKey( "group999" ) );
        Set<GroupKey> expectedGroupsToLeave = Sets.newHashSet( new GroupKey( "group100" ) );

        assertEquals( expectedGroupsToJoin, groupsToJoin );
        assertEquals( expectedGroupsToLeave, groupsToLeave );
    }

    @Test
    public void testResolveGroupsDiffWithEmptyValues()
    {
        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups = new HashSet<GroupKey>();
        Set<GroupKey> groupsToJoin = resolver.resolveGroupsToJoin( requestedGroups );
        Set<GroupKey> groupsToLeave = resolver.resolveGroupsToLeave( requestedGroups );

        // verify
        assertEquals( new HashSet<GroupKey>(), groupsToJoin );
        assertEquals( new HashSet<GroupKey>(), groupsToLeave );
    }

    @Test
    public void resolveGroupsToJoin_returns_empty_set_when_existing_memberships_are_the_same_as_requested_memberships()
    {
        // setup
        GroupEntity existingMembership1 = createGroup( "EX1", "Existing membership 1" );
        GroupEntity existingMembership2 = createGroup( "EX2", "Existing membership 2" );
        userGroup.addMembership( existingMembership1 );
        userGroup.addMembership( existingMembership2 );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups = Sets.newHashSet( existingMembership1.getGroupKey(), existingMembership2.getGroupKey() );

        // verify
        assertEquals( "groupsToJoin", new HashSet<GroupKey>(), resolver.resolveGroupsToJoin( requestedGroups ) );
    }

    @Test
    public void resolveGroupsToLeave_returns_empty_set_when_existing_memberships_are_the_same_as_requested_memberships()
    {
        // setup
        GroupEntity existingMembership1 = createGroup( "EX1", "Existing membership 1" );
        GroupEntity existingMembership2 = createGroup( "EX2", "Existing membership 2" );
        userGroup.addMembership( existingMembership1 );
        userGroup.addMembership( existingMembership2 );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );
        Set<GroupKey> requestedGroups = Sets.newHashSet( existingMembership1.getGroupKey(), existingMembership2.getGroupKey() );
        // verify
        assertEquals( "groupsToLeave", new HashSet<GroupKey>(), resolver.resolveGroupsToLeave( requestedGroups ) );
    }

    @Test
    public void resolveGroupsToLeave_returns_empty_set_when_existing_memberships_are_the_same_as_requested_memberships_and_existing_membership_have_other_membership()
    {
        // setup
        GroupEntity existingMembership1 = createGroup( "EX1", "Existing membership 1" );
        GroupEntity existingMembership2 = createGroup( "EX2", "Existing membership 2" );
        GroupEntity indirectlyExistingMembership1 = createGroup( "IEX2", "Indirectly existing membership 1" );
        existingMembership1.addMembership( indirectlyExistingMembership1 );
        userGroup.addMembership( existingMembership1 );
        userGroup.addMembership( existingMembership2 );

        Set<GroupKey> requestedGroups = Sets.newHashSet( existingMembership1.getGroupKey(), existingMembership2.getGroupKey() );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );

        // verify
        assertEquals( "groupsToLeave", new HashSet<GroupKey>(), resolver.resolveGroupsToLeave( requestedGroups ) );
    }

    @Test
    public void resolveGroupsToJoin_returns_empty_set_when_existing_memberships_are_the_same_as_requested_memberships_and_existing_membership_have_other_membership()
    {
        // setup
        GroupEntity existingMembership1 = createGroup( "EX1", "Existing membership 1" );
        GroupEntity existingMembership2 = createGroup( "EX2", "Existing membership 2" );
        GroupEntity indirectlyExistingMembership1 = createGroup( "IEX2", "Indirectly existing membership 1" );
        existingMembership1.addMembership( indirectlyExistingMembership1 );
        userGroup.addMembership( existingMembership1 );
        userGroup.addMembership( existingMembership2 );

        Set<GroupKey> requestedGroups = Sets.newHashSet( existingMembership1.getGroupKey(), existingMembership2.getGroupKey() );

        // exercise
        GroupMembershipDiffResolver resolver = new GroupMembershipDiffResolver( userGroup );

        // verify
        assertEquals( "groupsToJoin", new HashSet<GroupKey>(), resolver.resolveGroupsToJoin( requestedGroups ) );
    }

    private GroupEntity createGroup( String key, String name )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( key ) );
        group.setDeleted( false );
        group.setName( name );
        return group;
    }

    private GroupEntity createGroup( String key )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( key ) );
        group.setDeleted( false );
        group.setName( key );
        return group;
    }
}
