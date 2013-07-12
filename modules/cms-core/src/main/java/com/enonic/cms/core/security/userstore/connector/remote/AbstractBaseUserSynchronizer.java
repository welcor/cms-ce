/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.GroupStorer;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStorer;
import com.enonic.cms.api.plugin.userstore.UserStoreConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.api.plugin.userstore.RemoteUserStorePlugin;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.api.plugin.userstore.UserFields;
import com.enonic.cms.api.plugin.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.userstore.RemoteUser;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

public abstract class AbstractBaseUserSynchronizer
{
    protected UserStorer userStorer;

    private GroupStorer groupStorer;

    protected final UserStoreEntity userStore;

    protected final boolean syncMemberships;

    protected RemoteUserStorePlugin remoteUserStorePlugin;

    protected UserDao userDao;

    protected GroupDao groupDao;

    protected TimeService timeService;

    protected UserStoreConnectorConfig connectorConfig;

    protected UserStoreConfig userStoreConfig;

    protected SynchronizeStatus status = null;

    protected final boolean syncUser;

    protected final boolean createMissingGroupsLocallyForMemberships;

    protected AbstractBaseUserSynchronizer( final SynchronizeStatus synchronizeStatus, final UserStoreEntity userStore,
                                            final boolean syncUser, final boolean syncMemberships,
                                            final boolean createMissingGroupsLocallyForMemberships )
    {
        this.status = synchronizeStatus;
        this.userStore = userStore;
        this.syncMemberships = syncMemberships;
        this.userStoreConfig = userStore.getConfig();
        this.syncUser = syncUser;
        this.createMissingGroupsLocallyForMemberships = createMissingGroupsLocallyForMemberships;
    }

    protected UserStoreKey getUserStoreKey()
    {
        return userStore.getKey();
    }

    UserEntity createUser( final RemoteUser remoteUser, final MemberCache memberCache )
    {
        final UserFields userFields = remoteUser.getUserFields().getConfiguredFieldsOnly( userStoreConfig );
        userFields.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );

        final StoreNewUserCommand storeNewUserCommand = new StoreNewUserCommand();
        storeNewUserCommand.setUserStoreKey( userStore.getKey() );
        storeNewUserCommand.setUsername( remoteUser.getId() );
        storeNewUserCommand.setSyncValue( remoteUser.getSync() );
        storeNewUserCommand.setEmail( remoteUser.getEmail() );
        storeNewUserCommand.setType( UserType.NORMAL );
        storeNewUserCommand.setUserFields( userFields );
        if ( syncMemberships )
        {
            final List<RemoteGroup> remoteMemberships = remoteUserStorePlugin.getMemberships( remoteUser );
            for ( RemoteGroup remoteGroup : remoteMemberships )
            {
                GroupEntity groupToBeMemberOf = findLocalGroup( remoteGroup, memberCache );
                if ( groupToBeMemberOf == null && createMissingGroupsLocallyForMemberships )
                {
                    groupToBeMemberOf = createGroup( remoteGroup );
                }
                if ( groupToBeMemberOf != null )
                {
                    storeNewUserCommand.addMembership( groupToBeMemberOf.getGroupKey() );
                }
            }
        }

        final UserKey newUserKey = userStorer.storeNewUser( storeNewUserCommand, new DisplayNameResolver( userStoreConfig ) );
        return userDao.findByKey( newUserKey );
    }

    UserEntity findUserBySyncValue( String syncValue )
    {
        final UserSpecification spec = new UserSpecification();
        spec.setUserStoreKey( userStore.getKey() );
        spec.setSyncValue( syncValue );
        spec.setDeletedState( UserSpecification.DeletedState.ANY );

        return userDao.findSingleBySpecification( spec );
    }

    UserEntity findUserByName( String uid )
    {
        final UserSpecification spec = new UserSpecification();
        spec.setUserStoreKey( userStore.getKey() );
        spec.setName( uid );
        spec.setDeletedState( UserSpecification.DeletedState.ANY );

        return userDao.findSingleBySpecification( spec );
    }

    protected boolean updateAndResurrectUser( final UserEntity localUser, final RemoteUser remoteUser )
    {
        boolean resurrected = false;
        boolean modified = false;
        // force resurrection
        if ( localUser.isDeleted() )
        {
            resurrected = true;

            localUser.setDeleted( false );
            if ( localUser.getUserGroup() != null )
            {
                localUser.getUserGroup().setDeleted( false );
            }
            modified = true;
        }

        if ( updateUserModifiableProperties( localUser, remoteUser ) )
        {
            modified = true;
        }

        if ( modified )
        {
            localUser.setTimestamp( timeService.getNowAsDateTime() );
        }
        return resurrected;
    }

    protected String getNameToVerify( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String remoteName = remoteUser != null ? remoteUser.getId() : null;
        if ( StringUtils.isNotBlank( remoteName ) )
        {
            return remoteName;
        }
        final String localName = localUser != null ? localUser.getName() : null;
        if ( StringUtils.isNotBlank( localName ) )
        {
            return localName;
        }
        return null;
    }

    protected String getEmailToVerify( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String remoteEmail = remoteUser != null ? remoteUser.getEmail() : null;
        if ( StringUtils.isNotBlank( remoteEmail ) )
        {
            return remoteEmail;
        }
        final String localEmail = localUser != null ? localUser.getEmail() : null;
        if ( StringUtils.isNotBlank( localEmail ) )
        {
            return localEmail;
        }
        return null;
    }

    protected boolean nameAlreadyUsedByOtherUser( final String name, final UserEntity localUser )
    {
        if ( name == null )
        {
            return false;
        }
        final UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setName( name );
        userByEmailSpec.setUserStoreKey( getUserStoreKey() );
        userByEmailSpec.setDeletedStateNotDeleted();

        return isOtherThanMeFound( userByEmailSpec, localUser );
    }

    protected UserEntity getOtherUserWithSameEmail( final String email, final UserEntity localUser )
    {
        if ( email == null )
        {
            return null;
        }

        final UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setEmail( email );
        userByEmailSpec.setUserStoreKey( getUserStoreKey() );
        userByEmailSpec.setDeletedStateNotDeleted();

        return findOtherThanMe( userByEmailSpec, localUser );
    }

    protected boolean emailAlreadyUsedByOtherUser( final String email, final UserEntity localUser )
    {
        return getOtherUserWithSameEmail( email, localUser ) != null;
    }

    private boolean isOtherThanMeFound( final UserSpecification specification, final UserEntity me )
    {
        return findOtherThanMe( specification, me ) != null;
    }

    private UserEntity findOtherThanMe( final UserSpecification specification, final UserEntity me )
    {
        final List<UserEntity> users = userDao.findBySpecification( specification );

        if ( me != null )
        {
            final boolean oneEntityFoundAndItsMe = users.size() == 1 && me.equals( users.get( 0 ) );
            if ( oneEntityFoundAndItsMe )
            {
                return null;
            }

            return getOtherThanMe( users, me );
        }

        return users.isEmpty() ? null : users.get( 0 );
    }

    private UserEntity getOtherThanMe( List<UserEntity> users, UserEntity me )
    {
        for ( UserEntity user : users )
        {
            if ( !user.equals( me ) )
            {
                return user;
            }
        }

        return null;
    }

    protected void synchronizeOtherUserWithSameEmail( final String email, final UserEntity localUser )
    {
        UserEntity otherUserWithSameEmail = getOtherUserWithSameEmail( email, localUser );

        if ( otherUserWithSameEmail != null )
        {
            final RemoteUser remoteUser = remoteUserStorePlugin.getUser( otherUserWithSameEmail.getName() );

            if ( remoteUser == null )
            {
                deleteUser( otherUserWithSameEmail );
            }
        }
    }

    private boolean updateUserModifiableProperties( final UserEntity userToModify, final RemoteUser remoteUser )
    {
        final DisplayNameResolver displayNameResolver = new DisplayNameResolver( userStoreConfig );
        final boolean displayNameManuallyEdited = displayNameManuallyEdited( displayNameResolver, userToModify );

        boolean modified = false;

        if ( !equals( userToModify.getEmail(), remoteUser.getEmail() ) )
        {
            userToModify.setEmail( remoteUser.getEmail() );
            modified = true;
        }

        if ( !equals( userToModify.getName(), remoteUser.getId() ) )
        {
            userToModify.setName( remoteUser.getId() );
            modified = true;
        }

        final UserFields remoteUserFields = remoteUser.getUserFields().getConfiguredFieldsOnly( userStoreConfig );
        remoteUserFields.retain(
            userStoreConfig.getRemoteOnlyUserFieldTypes() ); // TODO: isnt not this unecessary since these are coming from removeUser?

        // must only clear and update fields that are remote, otherwise locally stored user fields are lost
        final UserFields userFieldsToModify = userToModify.getUserFields().getConfiguredFieldsOnly( userStoreConfig );
        userFieldsToModify.replaceAllRemoteFieldsOnly( remoteUserFields, userStoreConfig );
        final boolean modifiedUserFields = userToModify.setUserFields( userFieldsToModify );
        modified = modified || modifiedUserFields;

        if ( !displayNameManuallyEdited )
        {
            userToModify.setDisplayName( displayNameResolver.resolveDisplayName( userToModify.getName(), userToModify.getDisplayName(),
                                                                                 userToModify.getUserFields() ) );
        }

        return modified;
    }

    private boolean displayNameManuallyEdited( final DisplayNameResolver displayNameResolver, UserEntity user )
    {
        final String displayNameGeneratedFromExistingUser =
            displayNameResolver.resolveDisplayName( user.getName(), user.getDisplayName(), user.getUserFields() );

        final String existingDisplayName = user.getDisplayName();

        return !displayNameGeneratedFromExistingUser.equals( existingDisplayName );
    }

    protected void syncUserMemberships( final UserEntity localUser, final RemoteUser remoteUser, final MemberCache memberCache )
    {
        final List<RemoteGroup> remoteMemberships = remoteUserStorePlugin.getMemberships( remoteUser );

        removeLocalUserMembershipsNotExistingRemote( localUser, remoteMemberships );

        final GroupEntity userGroup = localUser.getUserGroup();

        for ( final RemoteGroup remoteMembership : remoteMemberships )
        {
            syncGroupMembershipOfTypeGroup( userGroup, remoteMembership, memberCache );
        }

    }

    private void syncGroupMembershipOfTypeGroup( final GroupEntity localGroup, final RemoteGroup remoteGroupMember,
                                                 final MemberCache memberCache )
    {
        final GroupEntity existingMember = findLocalGroup( remoteGroupMember, memberCache );

        if ( existingMember == null )
        {
            // skip creation - only supported in full sync
        }
        else
        {
            if ( localGroup.hasMembership( existingMember ) )
            {
                // all is fine
                if ( status != null )
                {
                    status.userMembershipVerified();
                }
            }
            else
            {
                localGroup.addMembership( existingMember );
                if ( status != null )
                {
                    status.userMembershipCreated();
                }
            }
        }
    }

    private GroupEntity findLocalGroup( final RemoteGroup remoteGroup, final MemberCache memberCache )
    {
        final GroupSpecification spec = createGroupSpecification( remoteGroup );
        GroupEntity existingMember = memberCache.getMemberOfTypeGroup( spec );
        if ( existingMember == null )
        {
            existingMember = groupDao.findSingleBySpecification( spec );
            if ( existingMember != null )
            {
                memberCache.addMemeberOfTypeGroup( existingMember );
            }
        }
        return existingMember;
    }

    private GroupSpecification createGroupSpecification( final RemoteGroup remoteGroup )
    {
        final GroupSpecification spec = new GroupSpecification();
        spec.setUserStoreKey( getUserStoreKey() );
        spec.setSyncValue( remoteGroup.getSync() );
        return spec;
    }

    protected void removeLocalUserMembershipsNotExistingRemote( final UserEntity localUser, final List<RemoteGroup> remoteMemberships )
    {
        // Gather remote users in a map for fast and easy access
        final Map<String, RemoteGroup> remoteMembershipsMap = new HashMap<String, RemoteGroup>();
        for ( final RemoteGroup remoteMembership : remoteMemberships )
        {
            remoteMembershipsMap.put( remoteMembership.getId() + "-" + remoteMembership.getSync(), remoteMembership );
        }

        final GroupEntity userGroup = localUser.getUserGroup();

        // Gather local memberships that does not exist remote
        final Set<GroupEntity> localMembershipsToRemove = new HashSet<GroupEntity>();
        for ( final GroupEntity localMembership : userGroup.getMemberships( false ) )
        {
            // We're not removing memberships in built-in or global groups
            if ( !localMembership.isBuiltIn() && !localMembership.isGlobal() )
            {
                final RemoteGroup remoteMembership =
                    remoteMembershipsMap.get( localMembership.getName() + "-" + localMembership.getSyncValue() );
                if ( remoteMembership == null )
                {
                    localMembershipsToRemove.add( localMembership );
                }
            }
        }

        // Remove local memberships that does not exist remote
        for ( final GroupEntity localMembershipToRemove : localMembershipsToRemove )
        {
            userGroup.removeMembership( localMembershipToRemove );
            if ( status != null )
            {
                status.userMembershipDeleted();
            }
        }
    }

    protected void deleteUser( final UserEntity localUser )
    {
        if ( !localUser.isDeleted() )
        {
            final UserSpecification userToDeleteSpec = new UserSpecification();
            userToDeleteSpec.setKey( localUser.getKey() );
            userStorer.deleteUser( userToDeleteSpec );
            if ( status != null )
            {
                status.userDeleted();
            }
        }
    }

    private boolean equals( Object a, Object b )
    {
        if ( a == null && b == null )
        {
            return true;
        }
        else if ( a == null || b == null )
        {
            return false;
        }
        return a.equals( b );
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

    public void setRemoteUserStorePlugin( final RemoteUserStorePlugin value )
    {
        this.remoteUserStorePlugin = value;
    }

    public void setTimeService( final TimeService value )
    {
        this.timeService = value;
    }

    public void setUserDao( final UserDao value )
    {
        this.userDao = value;
    }

    public void setGroupDao( final GroupDao value )
    {
        this.groupDao = value;
    }

    public void setConnectorConfig( final UserStoreConnectorConfig value )
    {
        this.connectorConfig = value;
    }

    public void setUserStorer( UserStorer value )
    {
        this.userStorer = value;
    }

    public void setGroupStorer( GroupStorer groupStorer )
    {
        this.groupStorer = groupStorer;
    }
}
