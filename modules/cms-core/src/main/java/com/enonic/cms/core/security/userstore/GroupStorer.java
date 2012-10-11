package com.enonic.cms.core.security.userstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.util.Assert;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryAccessDao;
import com.enonic.cms.store.dao.ContentAccessDao;
import com.enonic.cms.store.dao.DefaultSiteAccessDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemAccessDao;
import com.enonic.cms.store.dao.UserDao;

public class GroupStorer
{
    private GroupDao groupDao;

    private UserDao userDao;

    private MenuItemAccessDao menuItemAccessDao;

    private CategoryAccessDao categoryAccessDao;

    private ContentAccessDao contentAccessDao;

    private DefaultSiteAccessDao defaultSiteAccessDao;

    private TimeService timeService;

    private boolean resurrectDeletedGroups;

    private boolean groupsStoredLocally;

    private UserStoreEntity userStore;

    public GroupKey storeNewGroup( StoreNewGroupCommand command )
    {
        if ( userStore != null )
        {

            Preconditions.checkArgument( command.getUserStoreKey().equals( userStore.getKey() ),
                                         "This UserStorer only supports creating groups for user store " + userStore.getKey() + ", was: " +
                                             command.getUserStoreKey() );
        }
        else
        {
            Preconditions.checkArgument( command.getUserStoreKey() == null,
                                         "This UserStorer only supports creating groups for global user stores" );
        }

        checkIfGroupAlreadyExistsUndeleted( command );

        if ( groupAlreadyExistsDeleted( command ) )
        {
            if ( userStore != null && resurrectDeletedGroups )
            {
                return resurrectDeletedGroup( command );
            }
            else if ( !groupsStoredLocally )
            {
                makeExistingDeletedGroupsHaveNonRepeatableSyncValues( command );
            }
        }

        UserEntity user = null;
        if ( command.getUserKey() != null )
        {
            user = userDao.findByKey( command.getUserKey().toString() );
        }

        final GroupEntity newGroup = new GroupEntity();
        newGroup.setRestricted( command.isRestriced() );
        newGroup.setDeleted( false );
        newGroup.setDescription( command.getDescription() );
        newGroup.setName( command.getName() );
        final String syncValue = command.getSyncValue();
        newGroup.setSyncValue( syncValue == null ? "NA" : syncValue );
        if ( user != null )
        {
            newGroup.setUser( user );
        }
        newGroup.setUserStore( userStore );
        newGroup.setType( command.getType() );

        if ( command.getMembers() != null )
        {
            for ( GroupKey memberKey : command.getMembers() )
            {
                GroupEntity member = groupDao.find( memberKey.toString() );

                if ( newGroup.isInUserStore() && !newGroup.getUserStore().equals( member.getUserStore() ) )
                {
                    throw new IllegalArgumentException(
                        member.getQualifiedName() + " cannot be member of group " + newGroup.getQualifiedName() +
                            ". Group and member must be located in same user store." );
                }
                member.addMembership( newGroup );
            }
        }
        groupDao.storeNew( newGroup );
        return newGroup.getGroupKey();
    }

    public void addMembershipToGroup( GroupEntity groupToAdd, GroupEntity groupToAddTo )
    {
        groupToAdd.addMembership( groupToAddTo );
    }

    public void removeMembershipFromGroup( GroupEntity groupToRemove, GroupEntity groupToRemoveFrom )
    {
        if ( groupToRemove.hasMembership( groupToRemoveFrom ) )
        {
            groupToRemove.removeMembership( groupToRemoveFrom );
        }
    }

    public void updateGroup( UpdateGroupCommand command )
    {
        GroupEntity group = groupDao.findByKey( command.getGroupKey() );

        Preconditions.checkNotNull( group, "group does not exist: " + command.getGroupKey() );

        group.setName( command.getName() );
        group.setDescription( command.getDescription() );
        if ( group.isBuiltIn() )
        {
            // Force restricted enrollment for built-in groups - always!
            group.setRestricted( true );
        }
        else
        {
            if ( command.isRestricted() != null )
            {
                group.setRestricted( command.isRestricted() ? 1 : 0 );
            }
        }

        if ( command.getMembersAsKeys() != null )
        {
            syncMembers( group, command.getMembersAsKeys() );
        }
    }

    private void syncMembers( final GroupEntity group, final Collection<GroupKey> expectedMembers )
    {
        Preconditions.checkNotNull( group );
        Preconditions.checkNotNull( expectedMembers );

        removeMembershipsNotExistingInCollection( group, expectedMembers );
        addMembershipsToGroup( group, expectedMembers );
    }

    private void removeMembershipsNotExistingInCollection( final GroupEntity group, final Collection<GroupKey> expectedMembers )
    {
        final List<GroupEntity> membersToRemove = new ArrayList<GroupEntity>();
        for ( GroupEntity existingMember : group.getMembers( false ) )
        {
            if ( !expectedMembers.contains( existingMember.getGroupKey() ) )
            {
                membersToRemove.add( existingMember );
            }
        }
        for ( GroupEntity memberToRemove : membersToRemove )
        {
            memberToRemove.removeMembership( group );
        }
    }

    private void addMembershipsToGroup( final GroupEntity group, final Collection<GroupKey> expectedMembers )
    {
        for ( GroupKey expectedMemberGroupKey : expectedMembers )
        {
            final GroupEntity expectedMember = groupDao.findByKey( expectedMemberGroupKey );

            if ( expectedMember != null && !expectedMember.hasMembership( group ) )
            {
                if ( group.isInUserStore() && !group.getUserStore().equals( expectedMember.getUserStore() ) )
                {
                    throw new IllegalArgumentException(
                        expectedMember.getQualifiedName() + " cannot be member of group " + group.getQualifiedName() +
                            ". Group and member must be located in same user store." );
                }
                expectedMember.addMembership( group );
            }
        }
    }

    public void deleteGroup( final DeleteGroupCommand command )
    {
        final GroupEntity groupToDelete = groupDao.findSingleBySpecification( command.getSpecification() );

        Assert.notNull( groupToDelete, "No group matching specification: " + command.getSpecification() );

        groupToDelete.setDeleted( true );

        final GroupKey groupKey = groupToDelete.getGroupKey();
        defaultSiteAccessDao.deleteByGroupKey( groupKey );
        menuItemAccessDao.deleteByGroupKey( groupKey );
        contentAccessDao.deleteByGroupKey( groupKey );
        categoryAccessDao.deleteByGroupKey( groupKey );
    }

    private boolean groupAlreadyExistsDeleted( final StoreNewGroupCommand command )
    {
        final GroupSpecification spec = new GroupSpecification();
        spec.setDeletedState( GroupSpecification.DeletedState.DELETED );
        if ( userStore != null )
        {
            spec.setUserStoreKey( userStore.getKey() );
            if ( groupsStoredLocally )
            {
                spec.setName( command.getName() );
            }
            else
            {
                spec.setSyncValue( command.getSyncValue() );
            }
        }
        else
        {
            spec.setName( command.getName() );
            spec.setType( GroupType.GLOBAL_GROUP );
        }

        final List<GroupEntity> groups = groupDao.findBySpecification( spec );
        return !groups.isEmpty();
    }

    private GroupKey resurrectDeletedGroup( final StoreNewGroupCommand command )
    {
        Preconditions.checkArgument( !groupsStoredLocally, "Only resurrection of groups in remote userStores is expected" );

        final GroupSpecification existingGroupToResurrectSpec = new GroupSpecification();
        existingGroupToResurrectSpec.setUserStoreKey( userStore.getKey() );
        existingGroupToResurrectSpec.setDeletedState( GroupSpecification.DeletedState.DELETED );
        existingGroupToResurrectSpec.setSyncValue( command.getSyncValue() );

        final List<GroupEntity> groups = groupDao.findBySpecification( existingGroupToResurrectSpec );

        final GroupEntity groupToResurrect = groups.get( 0 );
        groupToResurrect.setDeleted( false );
        groupToResurrect.setName( command.getName() );

        if ( command.getMembers() != null )
        {
            syncMembers( groupToResurrect, command.getMembers() );
        }

        return groupToResurrect.getGroupKey();
    }

    private void makeExistingDeletedGroupsHaveNonRepeatableSyncValues( final StoreNewGroupCommand command )
    {
        Preconditions.checkArgument( !groupsStoredLocally,
                                     "No use in making sync values for groups stored in local userStores non-repeatable." );

        final GroupSpecification specification = new GroupSpecification();
        specification.setUserStoreKey( userStore.getKey() );
        specification.setSyncValue( command.getSyncValue() );
        specification.setDeletedState( GroupSpecification.DeletedState.DELETED );
        final List<GroupEntity> groups = groupDao.findBySpecification( specification );

        final NonRepeatableSyncValueResolver nonRepeatableSyncValueResolver = new NonRepeatableSyncValueResolver( timeService );
        for ( GroupEntity group : groups )
        {
            group.setSyncValue( nonRepeatableSyncValueResolver.resolve( group.getSyncValue() ) );
        }
    }

    private void checkIfGroupAlreadyExistsUndeleted( final StoreNewGroupCommand command )
        throws UserAlreadyExistsException
    {
        final GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        if ( userStore == null )
        {
            groupSpec.setType( GroupType.GLOBAL_GROUP );
            groupSpec.setName( command.getName() );
        }
        else
        {
            groupSpec.setUserStoreKey( command.getUserStoreKey() );
            if ( groupsStoredLocally )
            {
                groupSpec.setName( command.getName() );
            }
            else if ( command.getSyncValue() != null )
            {
                groupSpec.setSyncValue( command.getSyncValue() );
            }

            if ( command.getType() != null )
            {
                groupSpec.setType( command.getType() );
            }
        }

        final List<GroupEntity> groups = groupDao.findBySpecification( groupSpec );
        if ( !groups.isEmpty() )
        {
            if ( userStore == null )
            {
                throw new GroupAlreadyExistsException( command.getName() );
            }
            else
            {
                throw new GroupAlreadyExistsException( userStore.getName(), command.getName() );
            }
        }
    }

    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
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

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setUserStore( UserStoreEntity userStore )
    {
        this.userStore = userStore;
    }

    public void setResurrectDeletedGroups( boolean resurrectDeletedGroups )
    {
        this.resurrectDeletedGroups = resurrectDeletedGroups;
    }

    public void setGroupsStoredLocally( boolean groupsStoredLocally )
    {
        this.groupsStoredLocally = groupsStoredLocally;
    }
}
