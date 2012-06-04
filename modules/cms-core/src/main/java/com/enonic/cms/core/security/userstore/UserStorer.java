package com.enonic.cms.core.security.userstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupFactory;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.store.dao.CategoryAccessDao;
import com.enonic.cms.store.dao.ContentAccessDao;
import com.enonic.cms.store.dao.DefaultSiteAccessDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemAccessDao;
import com.enonic.cms.store.dao.RememberedLoginDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

public class UserStorer
{
    private GroupDao groupDao;

    private UserDao userDao;

    private TimeService timeService;

    private MenuItemAccessDao menuItemAccessDao;

    private CategoryAccessDao categoryAccessDao;

    private ContentAccessDao contentAccessDao;

    private DefaultSiteAccessDao defaultSiteAccessDao;

    private RememberedLoginDao rememberedLoginDao;

    private SiteDao siteDao;

    private boolean resurrectDeletedUsers;

    private UserStoreEntity userStore;

    public UserKey storeNewUser( StoreNewUserCommand command, DisplayNameResolver displayNameResolver )
    {
        checkIfUserAlreadyExistsUndeleted( command );

        if ( userAlreadyExistsDeleted( command ) )
        {
            if ( resurrectDeletedUsers )
            {
                return resurrectDeletedUser( command );
            }
            else if ( userStore.isRemote() )
            {
                makeExistingDeletedUsersHaveNonRepeatableSyncValues( command );
            }
        }

        String displayName = command.getDisplayName();
        if ( command.getDisplayName() == null || !displayNameManuallyEdited( displayNameResolver, command ) )
        {
            displayName =
                displayNameResolver.resolveDisplayName( command.getUsername(), command.getDisplayName(), command.getUserFields() );
        }

        final UserEntity newUser = new UserEntity();
        newUser.setDeleted( 0 );
        newUser.setUserStore( userStore );
        newUser.setName( command.getUsername() );
        newUser.setSyncValue( command.getSyncValue() == null ? "NA" : command.getSyncValue() );
        newUser.setEmail( command.getEmail() );
        newUser.setType( command.getType() );
        newUser.setTimestamp( timeService.getNowAsDateTime() );
        newUser.setDisplayName( displayName );
        newUser.encodePassword( command.getPassword() );
        newUser.setUserFields( command.getUserFields() );

        userDao.storeNew( newUser );

        if ( command.getType() == UserType.ANONYMOUS )
        {
            final GroupEntity anonymousUserGroup = groupDao.findSingleByGroupType( GroupType.ANONYMOUS );
            anonymousUserGroup.setUser( newUser );
            newUser.setUserGroup( anonymousUserGroup );
        }
        else if ( command.getType() == UserType.ADMINISTRATOR )
        {
            final GroupEntity eaUserGroup = groupDao.findSingleByGroupType( GroupType.ENTERPRISE_ADMINS );
            eaUserGroup.setUser( newUser );
            newUser.setUserGroup( eaUserGroup );
        }
        else
        {
            final GroupEntity newUserGroup = GroupFactory.createUserGroup( newUser );
            groupDao.storeNew( newUserGroup );
            newUser.setUserGroup( newUserGroup );

            if ( command.getMemberships() != null )
            {
                addMemberships( newUser, command.getMemberships() );
            }
        }

        return newUser.getKey();
    }

    public void updateUser( final UpdateUserCommand command )
    {
        UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        if ( userToUpdate == null )
        {
            throw new UserNotFoundException( command.getSpecification() );
        }

        boolean modified = updateUserModifyableValues( command, userToUpdate );
        if ( modified )
        {
            userToUpdate.setTimestamp( timeService.getNowAsDateTime() );
        }

        if ( command.syncMemberships() )
        {
            syncMemberships( userToUpdate, command.getMemberships() );
        }
    }

    public void changePassword( final String uid, final String newPassword )
    {
        final UserEntity user = userDao.findByUserStoreKeyAndUsername( userStore.getKey(), uid );
        user.encodePassword( newPassword );
    }

    public void deleteUser( final UserSpecification userSpec )
    {
        final UserEntity userToDelete = userDao.findSingleBySpecification( userSpec );

        if ( userToDelete == null )
        {
            return;
        }

        Preconditions.checkArgument( !userToDelete.isBuiltIn(), "Cannot delete a built-in user" );

        userToDelete.setDeleted( true );
        userToDelete.setTimestamp( timeService.getNowAsDateTime() );

        rememberedLoginDao.removeUsage( userToDelete.getKey() );
        siteDao.removeUsage( userToDelete );

        final GroupEntity userGroup = userToDelete.getUserGroup();
        if ( userGroup != null )
        {
            userGroup.setDeleted( true );
            final GroupKey groupKey = userGroup.getGroupKey();
            defaultSiteAccessDao.deleteByGroupKey( groupKey );
            menuItemAccessDao.deleteByGroupKey( groupKey );
            contentAccessDao.deleteByGroupKey( groupKey );
            categoryAccessDao.deleteByGroupKey( groupKey );
        }
    }

    private UserKey resurrectDeletedUser( final StoreNewUserCommand command )
    {
        Preconditions.checkArgument( userStore.isRemote(), "Only resurrection of users in remote userStores is expected" );

        final UserSpecification existingUserToResurrectSpec = new UserSpecification();
        existingUserToResurrectSpec.setUserStoreKey( userStore.getKey() );
        existingUserToResurrectSpec.setDeletedState( UserSpecification.DeletedState.DELETED );
        existingUserToResurrectSpec.setSyncValue( command.getSyncValue() );

        final List<UserEntity> users = userDao.findBySpecification( existingUserToResurrectSpec );

        final UserEntity userToResurrect = geUserWithLatestTimestamp( users );
        userToResurrect.setDeleted( false );
        userToResurrect.setDisplayName( command.getDisplayName() );
        userToResurrect.setEmail( command.getEmail() );
        userToResurrect.setUserFields( command.getUserFields() );
        userToResurrect.setTimestamp( timeService.getNowAsDateTime() );
        final GroupEntity userGroup = userToResurrect.getUserGroup();
        userGroup.setDeleted( false );

        syncMemberships( userToResurrect, command.getMemberships() );

        return userToResurrect.getKey();
    }

    private void syncMemberships( final UserEntity user, final Collection<GroupKey> expectedMemberships )
    {
        Preconditions.checkNotNull( user );
        Preconditions.checkNotNull( expectedMemberships );

        addMemberships( user, expectedMemberships );
        removeMembershipsNotExistingInCollection( user, expectedMemberships );
    }

    private void addMemberships( final UserEntity user, final Iterable<GroupKey> memberships )
    {
        Preconditions.checkNotNull( user );
        Preconditions.checkNotNull( memberships );

        final GroupEntity userGroup = user.getUserGroup();

        for ( GroupKey groupKey : memberships )
        {
            final GroupEntity groupToBeMemberOf = groupDao.find( groupKey.toString() );
            if ( groupToBeMemberOf == null )
            {
                continue;
            }

            final boolean membershipAlreadyExist = userGroup.isMemberOf( groupToBeMemberOf, false );
            if ( membershipAlreadyExist )
            {
                continue;
            }

            if ( groupToBeMemberOf.isInUserStore() && !groupToBeMemberOf.getUserStore().equals( userStore ) )
            {
                throw new IllegalArgumentException(
                    userGroup.getUser().getQualifiedName() + " cannot be member of group " + groupToBeMemberOf.getQualifiedName() +
                        ". The user is not located in the same userStore as the group." );
            }
            userGroup.addMembership( groupToBeMemberOf );
        }
    }

    private void removeMembershipsNotExistingInCollection( final UserEntity user, final Collection<GroupKey> expectedMemberships )
    {
        Preconditions.checkNotNull( user );
        Preconditions.checkNotNull( expectedMemberships );

        final GroupEntity userGroup = user.getUserGroup();
        final List<GroupEntity> groupsToRemove = new ArrayList<GroupEntity>();

        for ( GroupEntity existingMembership : userGroup.getMemberships( false ) )
        {
            final boolean removeThisGroup = !expectedMemberships.contains( existingMembership.getGroupKey() );

            if ( removeThisGroup )
            {
                groupsToRemove.add( existingMembership );
            }
        }

        for ( GroupEntity groupToRemove : groupsToRemove )
        {
            userGroup.removeMembership( groupToRemove );
        }
    }

    private boolean updateUserModifyableValues( UpdateUserCommand command, UserEntity userToUpdate )
    {
        final boolean isUpdateStrategy = command.isUpdateStrategy();
        boolean modified = false;

        final String displayName = command.getDisplayName();
        if ( displayName != null || isUpdateStrategy )
        {
            modified = !equals( userToUpdate.getDisplayName(), displayName );
            userToUpdate.setDisplayName( displayName );
        }

        final String email = command.getEmail();
        if ( email != null || isUpdateStrategy )
        {
            modified = modified || !equals( userToUpdate.getEmail(), email );
            userToUpdate.setEmail( email );
        }

        UserFields userFields = command.getUserFields();
        if ( userFields == null && isUpdateStrategy )
        {
            userFields = new UserFields();
        }

        if ( userFields != null )
        {
            boolean userInfoModified;

            if ( isUpdateStrategy )
            {
                userInfoModified = userToUpdate.setUserFields( userFields );
            }
            else
            {
                userInfoModified = userToUpdate.overwriteUserFields( userFields );
            }

            modified = modified || userInfoModified;
        }
        return modified;
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

    private boolean displayNameManuallyEdited( final DisplayNameResolver displayNameResolver, StoreNewUserCommand command )
    {
        final String displayNameGeneratedFromExistingUser =
            displayNameResolver.resolveDisplayName( command.getUsername(), command.getDisplayName(), command.getUserFields() );

        return !displayNameGeneratedFromExistingUser.equals( command.getDisplayName() );
    }

    private void checkIfUserAlreadyExistsUndeleted( final StoreNewUserCommand command )
        throws UserAlreadyExistsException
    {
        final UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( command.getUserStoreKey() );
        userSpec.setDeletedState( UserSpecification.DeletedState.NOT_DELETED );
        if ( userStore.isLocal() )
        {
            userSpec.setName( command.getUsername() );
        }
        else
        {
            userSpec.setSyncValue( command.getSyncValue() );
        }
        final List<UserEntity> users = userDao.findBySpecification( userSpec );
        if ( !users.isEmpty() )
        {
            throw new UserAlreadyExistsException( userStore.getName(), command.getUsername() );
        }
    }

    private boolean userAlreadyExistsDeleted( final StoreNewUserCommand command )
    {
        final UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( command.getUserStoreKey() );
        userSpec.setDeletedState( UserSpecification.DeletedState.DELETED );
        if ( userStore.isLocal() )
        {
            userSpec.setName( command.getUsername() );
        }
        else
        {
            userSpec.setSyncValue( command.getSyncValue() );
        }

        final List<UserEntity> users = userDao.findBySpecification( userSpec );
        return !users.isEmpty();
    }

    private void makeExistingDeletedUsersHaveNonRepeatableSyncValues( final StoreNewUserCommand command )
    {
        Preconditions.checkArgument( userStore.isRemote(),
                                     "No use in making sync values for users stored in local userStores non-repeatable." );

        final UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( userStore.getKey() );
        userSpec.setSyncValue( command.getSyncValue() );
        userSpec.setDeletedState( UserSpecification.DeletedState.DELETED );
        final List<UserEntity> users = userDao.findBySpecification( userSpec );

        final NonRepeatableSyncValueResolver nonRepeatableSyncValueResolver = new NonRepeatableSyncValueResolver( timeService );
        for ( UserEntity user : users )
        {
            user.setSyncValue( nonRepeatableSyncValueResolver.resolve( user.getSync() ) );
        }
    }

    private UserEntity geUserWithLatestTimestamp( final Iterable<UserEntity> it )
    {
        UserEntity latestUpdatedUser = null;
        for ( UserEntity user : it )
        {
            if ( latestUpdatedUser == null )
            {
                latestUpdatedUser = user;
            }
            else if ( user.getTimestamp().isAfter( latestUpdatedUser.getTimestamp() ) )
            {
                latestUpdatedUser = user;
            }
        }
        return latestUpdatedUser;
    }

    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setMenuItemAccessDao( MenuItemAccessDao menuItemAccessDao )
    {
        this.menuItemAccessDao = menuItemAccessDao;
    }

    public void setCategoryAccessDao( CategoryAccessDao categoryAccessDao )
    {
        this.categoryAccessDao = categoryAccessDao;
    }

    public void setContentAccessDao( ContentAccessDao contentAccessDao )
    {
        this.contentAccessDao = contentAccessDao;
    }

    public void setDefaultSiteAccessDao( DefaultSiteAccessDao defaultSiteAccessDao )
    {
        this.defaultSiteAccessDao = defaultSiteAccessDao;
    }

    public void setRememberedLoginDao( RememberedLoginDao rememberedLoginDao )
    {
        this.rememberedLoginDao = rememberedLoginDao;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setUserStore( UserStoreEntity userStore )
    {
        this.userStore = userStore;
    }

    public void setResurrectDeletedUsers( boolean resurrectDeletedUsers )
    {
        this.resurrectDeletedUsers = resurrectDeletedUsers;
    }
}
