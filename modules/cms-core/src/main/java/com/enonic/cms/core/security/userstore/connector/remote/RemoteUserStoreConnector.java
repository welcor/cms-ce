/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.springframework.util.Assert;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.util.GenericConcurrencyLock;

import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.ext.userstore.RemotePrincipal;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUserStore;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.ReadOnlyUserFieldValidator;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserImpl;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreConnectorPolicyBrokenException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.connector.AbstractBaseUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.AuthenticationChain;
import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.core.time.TimeService;

public class RemoteUserStoreConnector
    extends AbstractBaseUserStoreConnector
{
    private static GenericConcurrencyLock<String> concurrencyLock = GenericConcurrencyLock.create();

    private RemoteUserStore remoteUserStorePlugin;

    private TimeService timeService;

    private UserStoreConnectorConfig connectorConfig;

    private UserStoreConfig userStoreConfig;

    public RemoteUserStoreConnector( final UserStoreKey userStoreKey, final String userStoreName, final String connectorName )
    {
        super( userStoreKey, userStoreName, connectorName );
    }

    public boolean canCreateUser()
    {
        return connectorConfig.canCreateUser();
    }

    public boolean canUpdateUser()
    {
        return connectorConfig.canUpdateUser();
    }

    public boolean canUpdateUserPassword()
    {
        return connectorConfig.canUpdateUserPassword();
    }

    public boolean canDeleteUser()
    {
        return connectorConfig.canDeleteUser();
    }

    public boolean canCreateGroup()
    {
        return connectorConfig.canCreateGroup();
    }

    public boolean canReadGroup()
    {
        return connectorConfig.canReadGroup();
    }

    public boolean canUpdateGroup()
    {
        return connectorConfig.canUpdateGroup();
    }

    public boolean canDeleteGroup()
    {
        return connectorConfig.canDeleteGroup();
    }

    public UserKey storeNewUser( final StoreNewUserCommand command )
    {
        if ( !connectorConfig.canCreateUser() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to create user without 'create' policy" );
        }

        Assert.isTrue( command.getUserStoreKey().equals( userStoreKey ) );

        ensureValidUserName( command );

        RemoteUser remoteUser = new RemoteUser( command.getUsername() );
        remoteUser.setEmail( command.getEmail() );

        final UserFields remoteUserFields = command.getUserFields().getRemoteFields( userStoreConfig );
        remoteUser.getUserFields().addAll( remoteUserFields.getAll() );

        final boolean success = remoteUserStorePlugin.addPrincipal( remoteUser );
        if ( !success )
        {
            throw new UserAlreadyExistsException( userStoreName, command.getUsername() );
        }
        remoteUserStorePlugin.changePassword( command.getUsername(), command.getPassword() );

        remoteUser = getRemoteUser( command.getUsername() );
        command.setSyncValue( remoteUser.getSync() );

        if ( connectorConfig.groupsStoredRemote() )
        {
            addMembershipsRemote( remoteUser, command.getMemberships() );
        }

        return storeNewUserLocally( command, new DisplayNameResolver( getUserStore().getConfig() ) );
    }

    protected boolean isUsernameUnique( final String userName )
    {
        final UserEntity localUser = getLocalUserWithUsername( userName );

        final RemoteUser remoteUser = getRemoteUser( userName );

        return localUser == null && remoteUser == null;
    }

    public void updateUser( final UpdateUserCommand command )
    {
        final UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        if ( userToUpdate == null )
        {
            throw new UserNotFoundException( command.getSpecification() );
        }

        final RemoteUser remoteUser = getRemoteUser( userToUpdate.getName() );

        if ( remoteUser == null )
        {
            throw new RuntimeException(
                "User not found in remote userstore '" + userStoreName + "' from specification: " + command.getSpecification().toString() );
        }

        if ( !connectorConfig.canUpdateUser() && commandContainsChangedRemoteFields( command, remoteUser ) )
        {
            // Trying to update remote fields:
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to update user without 'update' policy" );
        }

        new ReadOnlyUserFieldValidator( getUserStore().getConfig() ).validate(
            command.getUserFields().getChangedUserFields( remoteUser.getUserFields().getConfiguredFieldsOnly( userStoreConfig ),
                                                          command.isUpdateStrategy() ) );
        new UserPolicyValidator( connectorConfig, getUserStore() ).validateFieldsForUpdate( command, remoteUser );

        if ( connectorConfig.canUpdateUser() )
        {
            updateUserModifiableValues( command, remoteUser );

            final boolean success = remoteUserStorePlugin.updatePrincipal( remoteUser );
            if ( !success )
            {
                throw new RuntimeException( "User does not exists: " + command.getSpecification().getName() );
            }
        }

        if ( connectorConfig.canUpdateGroup() && connectorConfig.groupsStoredRemote() && command.syncMemberships() )
        {
            updateMembershipsRemote( userToUpdate, remoteUser, command.getMemberships() );
        }

        resetRemoteFieldsInCommand( command, remoteUser );
        updateUserLocally( command );
    }

    private boolean commandContainsChangedRemoteFields( final UpdateUserCommand command, final RemoteUser remoteUser )
    {
        final UserFields remoteFieldsInCommand =
            command.getUserFields().getConfiguredFieldsOnly( userStoreConfig ).getRemoteFields( userStoreConfig ).emptiesToNull();
        final UserFields configuredFieldsOnlyInRemoteUser = remoteUser.getUserFields().getConfiguredFieldsOnly( userStoreConfig );
        return !remoteFieldsInCommand.existingFieldsEquals( configuredFieldsOnlyInRemoteUser );
    }

    private void resetRemoteFieldsInCommand( final UpdateUserCommand command, final RemoteUser remoteUser )
    {
        final UserFields commandUserFields = command.getUserFields();

        // remove remote fields from update command
        commandUserFields.retain( userStoreConfig.getLocalOnlyUserFieldTypes() );

        // get remote fields values from remote user
        final UserFields remoteFields = remoteUser.getUserFields().getConfiguredFieldsOnly( userStoreConfig );
        remoteFields.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );

        // merge remote fields with local fields from command
        commandUserFields.addAll( remoteFields.getAll() );

        command.setUserFields( commandUserFields );
    }

    private void updateUserModifiableValues( final UpdateUserCommand command, final RemoteUser remoteUser )
    {
        final String email = command.getEmail();

        if ( email != null || command.isUpdateStrategy() )
        {
            remoteUser.setEmail( email );
        }

        final UserFields givenRemoteUserFields = command.getUserFields().getRemoteFields( userStoreConfig );
        final UserFields remoteUserFields = remoteUser.getUserFields();
        if ( command.isUpdateStrategy() )
        {
            remoteUserFields.clear();
        }

        // Remove address field on "target" when address field is set : JVS, ok but why?
        if ( givenRemoteUserFields.hasField( UserFieldType.ADDRESS ) )
        {
            remoteUserFields.remove( UserFieldType.ADDRESS );
        }

        remoteUserFields.addAll( givenRemoteUserFields.getAll() );
    }

    public void deleteUser( final DeleteUserCommand command )
    {
        if ( !connectorConfig.canDeleteUser() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to delete user without 'delete' policy" );
        }

        final UserEntity userToDelete = userDao.findSingleBySpecification( command.getSpecification() );

        remoteUserStorePlugin.removePrincipal( new RemoteUser( userToDelete.getName() ) );

        deleteUserLocally( command );
    }

    public GroupKey storeNewGroup( final StoreNewGroupCommand command )
    {
        if ( !connectorConfig.canCreateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to create group without 'create' policy" );
        }

        Assert.isTrue( command.getUserStoreKey().equals( userStoreKey ) );

        if ( connectorConfig.groupsStoredRemote() )
        {
            RemoteGroup remoteGroup = new RemoteGroup( command.getName() );
            final boolean success = remoteUserStorePlugin.addPrincipal( remoteGroup );
            if ( !success )
            {
                throw new GroupAlreadyExistsException( userStoreName, command.getName() );
            }
            remoteGroup = getRemoteGroup( command.getName() );
            command.setSyncValue( remoteGroup.getSync() );
            final List<GroupKey> members = command.getMembers();
            if ( members != null && members.size() > 0 )
            {
                addMembersRemote( remoteGroup, members );
            }
        }
        return storeNewGroupLocally( command );
    }

    public void updateGroup( final UpdateGroupCommand command )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to update group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            final GroupEntity groupToUpdate = groupDao.findByKey( command.getGroupKey() );
            Preconditions.checkNotNull( command.getName(), "missing name in command" );
            Preconditions.checkNotNull( groupToUpdate, "group does not exist: " + command.getGroupKey() );
            if ( !command.getName().equals( groupToUpdate.getName() ) )
            {
                throw new IllegalArgumentException(
                    "Changing names of a groups in remote user stores is not supported: " + groupToUpdate.getQualifiedName().toString() );
            }

            final RemoteGroup remoteGroup = getRemoteGroup( command.getName() );
            if ( command.getMembers() != null )
            {
                updateMembersRemote( groupToUpdate, remoteGroup, command.getMembers() );
            }
        }
        updateGroupLocally( command );
    }

    private List<RemotePrincipal> toPrincipalList( final RemotePrincipal... principals )
    {
        final ArrayList<RemotePrincipal> list = new ArrayList<RemotePrincipal>();
        list.addAll( Arrays.asList( principals ) );
        return list;
    }

    public void addMembershipToGroup( final GroupEntity groupToAdd, final GroupEntity groupToAddTo )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add membership to group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            if ( groupToAdd.isOfType( GroupType.USER, false ) )
            {
                addMembersToRemoteGroup( groupToAddTo.getName(), toPrincipalList( new RemoteUser( groupToAdd.getUser().getName() ) ) );
            }
            else
            {
                addMembersToRemoteGroup( groupToAddTo.getName(), toPrincipalList( new RemoteGroup( groupToAdd.getName() ) ) );
            }
        }
        addMembershipToGroupLocally( groupToAdd, groupToAddTo );
    }

    public void removeMembershipFromGroup( final GroupEntity groupToRemove, final GroupEntity groupToRemoveFrom )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to remove membership from group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            if ( groupToRemove.isOfType( GroupType.USER, false ) )
            {
                removeMembersFromRemoteGroup( groupToRemoveFrom.getName(),
                                              toPrincipalList( new RemoteUser( groupToRemove.getUser().getName() ) ) );
            }
            else
            {
                removeMembersFromRemoteGroup( groupToRemoveFrom.getName(), toPrincipalList( new RemoteGroup( groupToRemove.getName() ) ) );
            }
        }
        removeMembershipFromGroupLocally( groupToRemove, groupToRemoveFrom );
    }

    public void deleteGroup( final DeleteGroupCommand command )
    {
        if ( !connectorConfig.canDeleteGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to delete group without 'delete' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            final GroupEntity groupToDelete = groupDao.findSingleBySpecification( command.getSpecification() );
            remoteUserStorePlugin.removePrincipal( new RemoteGroup( groupToDelete.getName() ) );
        }
        deleteGroupLocally( command );
    }

    private boolean verifyPassword( final String uid, final String password, final AuthenticationChain authChain )
    {
        if ( authChain.authenticate( this.userStoreKey, uid, password ) )
        {
            return true;
        }

        return this.remoteUserStorePlugin.authenticate( uid, password );
    }

    public String authenticateUser( final String uid, final String password, final AuthenticationChain authChain )
    {
        if ( !verifyPassword( uid, password, authChain ) )
        {
            throw new InvalidCredentialsException( uid );
        }

        return getRemoteUser( uid ).getSync();
    }

    public void changePassword( final String uid, final String newPassword )
    {
        if ( !connectorConfig.canUpdateUserPassword() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to change password without 'updatePassword' policy" );
        }

        final boolean success = remoteUserStorePlugin.changePassword( uid, newPassword );
        if ( !success )
        {
            throw new RuntimeException( "Changed password failed" );
        }
    }

    public synchronized void synchronizeUsers( final SynchronizeStatus status, final List<RemoteUser> remoteUsers,
                                               final boolean syncMemberships, final MemberCache memberCache )
    {
        doSynchronizeUsers( status, remoteUsers, true, syncMemberships, memberCache );
    }

    public synchronized void synchronizeUserMemberships( final SynchronizeStatus status, final RemoteUser remoteUser,
                                                         final MemberCache memberCache )
    {
        final List<RemoteUser> remoteUsers = new ArrayList<RemoteUser>( 1 );
        remoteUsers.add( remoteUser );
        doSynchronizeUsers( status, remoteUsers, false, true, memberCache );
    }

    private void doSynchronizeUsers( final SynchronizeStatus status, final List<RemoteUser> remoteUsers, final boolean syncUser,
                                     final boolean syncMemberships, final MemberCache memberCache )
    {
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );

        final boolean doSyncMemberships = connectorConfig.groupsStoredRemote() && syncMemberships;

        final UsersSynchronizer synchronizer = new UsersSynchronizer( status, userStore, syncUser, doSyncMemberships );
        synchronizer.setUserStorer( userStorerFactory.create( userStore.getKey() ) );
        synchronizer.setGroupStorer( groupStorerFactory.create( userStore.getKey() ) );
        synchronizer.setGroupDao( groupDao );
        synchronizer.setUserDao( userDao );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setTimeService( timeService );
        synchronizer.setConnectorConfig( connectorConfig );
        synchronizer.setStatusCollector( status );

        synchronizer.synchronizeUsers( remoteUsers, memberCache );
    }

    public void synchronizeUser( final String uid )
    {
        final Lock locker = concurrencyLock.getLock( uid );

        try
        {
            locker.lock();

            final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
            final boolean syncMemberships = connectorConfig.groupsStoredRemote();

            final UserSynchronizer synchronizer = new UserSynchronizer( userStore, syncMemberships );
            synchronizer.setUserStorer( userStorerFactory.create( userStore.getKey() ) );
            synchronizer.setGroupStorer( groupStorerFactory.create( userStore.getKey() ) );
            synchronizer.setGroupDao( groupDao );
            synchronizer.setUserDao( userDao );
            synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
            synchronizer.setTimeService( timeService );
            synchronizer.setConnectorConfig( connectorConfig );

            synchronizer.synchronizeUser( uid );
        }
        finally
        {
            locker.unlock();
        }
    }

    public synchronized void synchronizeGroup( final GroupEntity group, final boolean syncMemberships, final boolean syncMembers )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize group without 'read' policy" );
        }

        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        final GroupSynchronizer synchronizer = new GroupSynchronizer( userStore, syncMemberships, syncMembers );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setUserDao( userDao );
        synchronizer.setGroupDao( groupDao );

        synchronizer.synchronize( group, new MemberCache() );
    }

    public synchronized void synchronizeGroups( final SynchronizeStatus status, final List<RemoteGroup> remoteGroups,
                                                final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize groups without 'read' policy" );
        }

        doSynchronizeGroups( status, remoteGroups, true, syncMemberships, syncMembers, memberCache );
    }

    public synchronized void synchronizeGroupMemberships( final SynchronizeStatus status, final RemoteGroup remoteGroup,
                                                          final MemberCache memberCache )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize groups without 'read' policy" );
        }

        final List<RemoteGroup> remoteGroups = new ArrayList<RemoteGroup>( 1 );
        remoteGroups.add( remoteGroup );
        doSynchronizeGroups( status, remoteGroups, false, true, false, memberCache );
    }

    private void doSynchronizeGroups( final SynchronizeStatus status, final List<RemoteGroup> remoteGroups, final boolean syncGroup,
                                      final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache )
    {
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        final GroupsSynchronizer synchronizer = new GroupsSynchronizer( status, userStore, syncGroup, syncMemberships, syncMembers );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setUserDao( userDao );
        synchronizer.setGroupDao( groupDao );
        synchronizer.setGroupStorer( groupStorerFactory.create( userStoreKey ) );

        synchronizer.synchronize( remoteGroups, memberCache );
    }

    private void addMembershipsRemote( final RemoteUser remoteUser, final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = getMembershipsToAddRemote( requestedMembershipKeys );

        final boolean hasMembershipsChanges = membershipsToAdd.size() > 0;
        if ( hasMembershipsChanges && !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add/remove a user's memberships without group 'update' policy" );
        }
        for ( final GroupEntity membershipToAdd : membershipsToAdd )
        {
            addMembersToRemoteGroup( membershipToAdd.getName(), toPrincipalList( remoteUser ) );
        }
    }

    private void addMembersRemote( final RemoteGroup remoteGroup, final Collection<GroupKey> requestedMemberKeys )
    {
        final List<RemotePrincipal> members = getMembersToAddRemote( requestedMemberKeys );
        remoteUserStorePlugin.addMembers( remoteGroup, members );
    }

    private void updateMembershipsRemote( final UserEntity userToUpdate, final RemoteUser remoteUser,
                                          final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToRemove = getMembershipsToRemoveRemote( userToUpdate, requestedMembershipKeys );
        final Set<GroupEntity> membershipsToAdd = getMembershipsToAddRemote( userToUpdate, requestedMembershipKeys );

        final boolean hasMembershipsChanges = membershipsToRemove.size() > 0 || membershipsToAdd.size() > 0;
        if ( hasMembershipsChanges && !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add/remove a user's memberships without group 'update' policy" );
        }
        for ( final GroupEntity membershipToAdd : membershipsToAdd )
        {
            addMembersToRemoteGroup( membershipToAdd.getName(), toPrincipalList( remoteUser ) );
        }
        for ( final GroupEntity membershipToRemove : membershipsToRemove )
        {
            removeMembersFromRemoteGroup( membershipToRemove.getName(), toPrincipalList( remoteUser ) );
        }
    }

    private void updateMembersRemote( final GroupEntity groupToUpdate, final RemoteGroup remoteGroup,
                                      final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToRemove = getMembersToRemoveRemote( groupToUpdate, requestedMembers );
        remoteUserStorePlugin.removeMembers( remoteGroup, membersToRemove );

        final List<RemotePrincipal> membersToAdd = getMembersToAddRemote( groupToUpdate, requestedMembers );
        remoteUserStorePlugin.addMembers( remoteGroup, membersToAdd );
    }

    private void addMembersToRemoteGroup( final String groupName, final List<RemotePrincipal> members )
    {
        final RemoteGroup remoteGroup = getRemoteGroup( groupName );
        remoteUserStorePlugin.addMembers( remoteGroup, members );
    }

    private void removeMembersFromRemoteGroup( final String groupName, final List<RemotePrincipal> members )
    {
        final RemoteGroup remoteGroup = getRemoteGroup( groupName );
        remoteUserStorePlugin.removeMembers( remoteGroup, members );
    }

    private Set<GroupEntity> getMembershipsToAddRemote( final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = new HashSet<GroupEntity>();
        for ( final GroupKey membershipKey : requestedMembershipKeys )
        {
            final GroupEntity membership = groupDao.findByKey( membershipKey );

            if ( membership.getType() == GroupType.USERSTORE_GROUP )
            {
                verifyCorrectUserstore( membership );
                membershipsToAdd.add( membership );
            }
        }
        return membershipsToAdd;
    }

    private Set<GroupEntity> getMembershipsToAddRemote( final UserEntity userToUpdate, final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = new HashSet<GroupEntity>();
        final Set<GroupEntity> currentMemberships = userToUpdate.getDirectMemberships();

        for ( final GroupKey requestedMembershipKey : requestedMembershipKeys )
        {
            final GroupEntity requestedMembership = groupDao.findByKey( requestedMembershipKey );
            if ( requestedMembership.getType() == GroupType.USERSTORE_GROUP && !currentMemberships.contains( requestedMembership ) )
            {
                verifyCorrectUserstore( requestedMembership );
                membershipsToAdd.add( requestedMembership );
            }
        }
        return membershipsToAdd;
    }

    private Set<GroupEntity> getMembershipsToRemoveRemote( final UserEntity userToUpdate,
                                                           final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToRemove = new HashSet<GroupEntity>();
        final Set<GroupEntity> currentMemberships = userToUpdate.getDirectMemberships();
        for ( final GroupEntity currentMembership : currentMemberships )
        {
            if ( currentMembership.getType() == GroupType.USERSTORE_GROUP &&
                !requestedMembershipKeys.contains( currentMembership.getGroupKey() ) )
            {
                verifyCorrectUserstore( currentMembership );
                membershipsToRemove.add( currentMembership );
            }
        }
        return membershipsToRemove;
    }

    private List<RemotePrincipal> getMembersToAddRemote( final Collection<GroupKey> requestedMemberKeys )
    {
        final List<RemotePrincipal> membersToAdd = new ArrayList<RemotePrincipal>();

        for ( final GroupKey requestedMemberKey : requestedMemberKeys )
        {
            final GroupEntity requestedMember = groupDao.findByKey( requestedMemberKey );
            if ( requestedMember.getType() == GroupType.USERSTORE_GROUP )
            {
                verifyCorrectUserstore( requestedMember );
                membersToAdd.add( new RemoteGroup( requestedMember.getName() ) );
            }
            if ( requestedMember.getType() == GroupType.USER )
            {
                verifyCorrectUserstore( requestedMember );
                final UserEntity user = requestedMember.getUser();
                verifyCorrectUserstore( user );
                membersToAdd.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToAdd;
    }

    private List<RemotePrincipal> getMembersToAddRemote( final GroupEntity groupToUpdate, final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToAdd = new ArrayList<RemotePrincipal>();
        final Set<GroupEntity> currentMembers = groupToUpdate.getMembers( false );

        for ( final GroupEntity requestedMember : requestedMembers )
        {
            if ( requestedMember.getType() == GroupType.USERSTORE_GROUP && !currentMembers.contains( requestedMember ) )
            {
                verifyCorrectUserstore( requestedMember );
                membersToAdd.add( new RemoteGroup( requestedMember.getName() ) );
            }
            if ( requestedMember.getType() == GroupType.USER && !currentMembers.contains( requestedMember ) )
            {
                verifyCorrectUserstore( requestedMember );
                final UserEntity user = requestedMember.getUser();
                verifyCorrectUserstore( user );
                membersToAdd.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToAdd;
    }

    private List<RemotePrincipal> getMembersToRemoveRemote( final GroupEntity groupToUpdate,
                                                            final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToRemove = new ArrayList<RemotePrincipal>();
        final Set<GroupEntity> currentMembers = groupToUpdate.getMembers( false );

        for ( final GroupEntity currentMember : currentMembers )
        {
            if ( currentMember.getType() == GroupType.USERSTORE_GROUP && !requestedMembers.contains( currentMember ) )
            {
                verifyCorrectUserstore( currentMember );
                membersToRemove.add( new RemoteGroup( currentMember.getName() ) );
            }
            if ( currentMember.getType() == GroupType.USER && !requestedMembers.contains( currentMember ) )
            {
                verifyCorrectUserstore( currentMember );
                final UserEntity user = currentMember.getUser();
                verifyCorrectUserstore( user );
                membersToRemove.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToRemove;
    }

    private RemoteGroup getRemoteGroup( final String groupName )
    {
        final RemoteGroup remoteGroup = remoteUserStorePlugin.getGroup( groupName );
        if ( remoteGroup == null )
        {
            throw new IllegalArgumentException( "Group does not exists in remote user store '" + userStoreName + "': " + groupName );
        }
        return remoteGroup;
    }

    private void verifyCorrectUserstore( final UserEntity user )
    {
        if ( !userStoreKey.equals( user.getUserStoreKey() ) )
        {
            throw new IllegalArgumentException(
                "Illegal userstore. Cannot add user " + user.getQualifiedName() + " to userstore " + userStoreName );
        }
    }

    private void verifyCorrectUserstore( final GroupEntity group )
    {
        if ( !userStoreKey.equals( group.getUserStoreKey() ) )
        {
            throw new IllegalArgumentException(
                "Illegal userstore. Cannot add group " + group.getQualifiedName() + " to userstore " + userStoreName );
        }
    }

    private RemoteUser getRemoteUser( String uid )
    {
        return remoteUserStorePlugin.getUser( uid );
    }

    public User getUserByEntity( final UserEntity userEntity )
    {
        return UserImpl.createFrom( userEntity );
    }

    public List<RemoteUser> getAllUsers()
    {
        return remoteUserStorePlugin.getAllUsers();
    }

    public List<RemoteGroup> getAllGroups()
    {
        return remoteUserStorePlugin.getAllGroups();
    }

    public void setRemoteUserStorePlugin( final RemoteUserStore value )
    {
        remoteUserStorePlugin = value;
    }

    public void setTimeService( final TimeService value )
    {
        timeService = value;
    }

    public void setConnectorConfig( final UserStoreConnectorConfig value )
    {
        connectorConfig = value;
    }

    public void setUserStoreConfig( final UserStoreConfig value )
    {
        userStoreConfig = value;
    }
}
