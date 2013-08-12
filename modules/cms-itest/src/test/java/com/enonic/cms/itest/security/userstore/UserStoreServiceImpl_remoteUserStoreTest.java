/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.security.userstore;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.group.AddMembershipsCommand;
import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.ReadOnlyUserFieldPolicyException;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreConnectorPolicyBrokenException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJob;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class UserStoreServiceImpl_remoteUserStoreTest
    extends AbstractSpringTest
{
    @Autowired
    private UserStoreConnectorManager userStoreConnectorManager;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private SynchronizeUserStoreJobFactory synchronizeUserStoreJobFactory;

    @Autowired
    private MemUserDatabase userDatabase;

    @Autowired
    private DomainFixture fixture;

    private DomainFactory factory;

    @Before
    public void setUp()
        throws Exception
    {
        this.fixture.initSystemData();
        this.factory = this.fixture.getFactory();
        this.userDatabase.clear();

        UserStoreEntity userStore = factory.createUserStore( "myRemoteUserStore", "myRemoteUserStore", true );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.INITIALS, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "remote" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );

    }

    @Test
    public void synchronizeUsers_creates_two_users_when_remote_has_two_users()
        throws Exception
    {
        addUser( "arn" );
        addUser( "laverne" );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        SynchronizeUserStoreJob job = synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify job status
        SynchronizeStatus synchronizeStatus = job.getStatus();
        assertEquals( 2, synchronizeStatus.getRemoteUsersStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getResurrectedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getUpdatedCount() );
        assertEquals( 2, synchronizeStatus.getRemoteUsersStatus().getTotalCount() );
        assertEquals( 2, synchronizeStatus.getRemoteUsersStatus().getCurrentCount() );

        // verify created users
        assertEquals( 2, fixture.countUsersByType( UserType.NORMAL ) );

        UserEntity actualUserArn = fixture.findUserByName( "arn" );
        assertNotNull( actualUserArn );
        assertEquals( "First Last", actualUserArn.getDisplayName() );
        assertEquals( "arn", actualUserArn.getName() );
        assertEquals( "arn@test.com", actualUserArn.getEmail() );
        assertEquals( UserType.NORMAL, actualUserArn.getType() );
        assertEquals( false, actualUserArn.isDeleted() );
        assertEquals( "First", actualUserArn.getUserFields().getFirstName() );
        assertEquals( "Last", actualUserArn.getUserFields().getLastName() );

        UserEntity actualUserLaverne = fixture.findUserByName( "laverne" );
        assertNotNull( actualUserLaverne );
        assertEquals( "First Last", actualUserLaverne.getDisplayName() );
        assertEquals( "laverne", actualUserLaverne.getName() );
        assertEquals( "laverne@test.com", actualUserLaverne.getEmail() );
        assertEquals( UserType.NORMAL, actualUserLaverne.getType() );
        assertEquals( false, actualUserLaverne.isDeleted() );
        assertEquals( "First", actualUserLaverne.getUserFields().getFirstName() );
        assertEquals( "Last", actualUserLaverne.getUserFields().getLastName() );
    }

    @Test
    public void synchronizeUsers_given_only_local_user_fields()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHOTO, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.GENDER, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LOCALE, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: create user
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhoto( new byte[]{100} );
        userFields.setPhone( "12345678" );
        userFields.setGender( Gender.FEMALE );
        userFields.setBirthday( new DateMidnight( 1980, 1, 1 ).toDate() );
        userFields.setHomePage( "http://www.homepage.com" );
        userFields.setLocale( Locale.ENGLISH );
        command.setUserFields( userFields );
        userStoreService.storeNewUser( command );

        // setup: verify created user
        UserEntity user = fixture.findUserByName( "laverne" );
        assertEquals( "Laverne", user.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken", user.getUserFields().getLastName() );
        assertArrayEquals( new byte[]{100}, user.getUserFields().getPhoto() );
        assertEquals( "12345678", user.getUserFields().getPhone() );
        assertEquals( Gender.FEMALE, user.getUserFields().getGender() );
        assertEquals( new DateMidnight( 1980, 1, 1 ).toDate(), user.getUserFields().getBirthday() );
        assertEquals( "http://www.homepage.com", user.getUserFields().getHomePage() );
        assertEquals( Locale.ENGLISH, user.getUserFields().getLocale() );

        // exercise
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify
        user = fixture.findUserByName( "laverne" );
        assertEquals( "Laverne", user.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken", user.getUserFields().getLastName() );
        assertArrayEquals( new byte[]{100}, user.getUserFields().getPhoto() );
        assertEquals( "12345678", user.getUserFields().getPhone() );
        assertEquals( Gender.FEMALE, user.getUserFields().getGender() );
        assertEquals( new DateMidnight( 1980, 1, 1 ).toDate(), user.getUserFields().getBirthday() );
        assertEquals( "http://www.homepage.com", user.getUserFields().getHomePage() );
        assertEquals( Locale.ENGLISH, user.getUserFields().getLocale() );
    }

    @Test
    public void synchronizeUsers_membership_removed()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group1 = addGroup( "cleaners" );
        final RemoteGroup group2 = addGroup( "editors" );
        this.userDatabase.addMember( group1, user );
        this.userDatabase.addMember( group2, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: verify laverne has two memberships
        UserEntity laverne = fixture.findUserByName( "laverne" );
        List<GroupEntity> laverneMemberships = Lists.newArrayList( laverne.getUserGroup().getMemberships( false ) );
        assertEquals( 2, laverneMemberships.size() );
        assertEquals( "cleaners", laverneMemberships.get( 0 ).getName() );
        assertEquals( "editors", laverneMemberships.get( 1 ).getName() );

        // setup: remove lavernes membership in cleaners
        this.userDatabase.removeMember( group1, user );

        // exercise
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify: laverne no longer member of cleaners
        laverne = fixture.findUserByName( "laverne" );
        laverneMemberships = Lists.newArrayList( laverne.getUserGroup().getMemberships( false ) );
        assertEquals( 1, laverneMemberships.size() );
        assertEquals( "editors", laverneMemberships.get( 0 ).getName() );
    }

    @Test
    public void synchronizeUsers_given_one_user_member_of_one_remote_when_synchronizing_users_and_groups_then_both_user_group_membership_is_created()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        SynchronizeUserStoreJob job = synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // verify job status
        SynchronizeStatus synchronizeStatus = job.getStatus();
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getResurrectedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getUpdatedCount() );
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getCurrentCount() );

        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getUpdatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getResurrectedCount() );
        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getTotalCount() );
        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getTotalCount() );
        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getVerifiedCount() );
        assertEquals( 1, synchronizeStatus.getGroupMembershipsStatus().getCurrentCount() );
        assertEquals( 1, synchronizeStatus.getGroupMembershipsStatus().getTotalCount() );

        assertEquals( 1, synchronizeStatus.getUserMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getVerifiedCount() );
        assertEquals( 1, synchronizeStatus.getUserMembershipsStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getUserMembershipsStatus().getCurrentCount() );

        // verify created users and groups
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        UserEntity actualUserLaverne = fixture.findUserByName( "laverne" );
        assertNotNull( actualUserLaverne );
        List<GroupEntity> memberships = Lists.newArrayList( actualUserLaverne.getUserGroup().getMemberships( false ) );
        assertEquals( 1, memberships.size() );
        assertEquals( "editors", memberships.get( 0 ).getName() );
        assertEquals( actualUserLaverne, memberships.get( 0 ).getMembers( false ).iterator().next().getUser() );
        assertNotNull( fixture.findGroupByName( "editors" ) );
    }

    @Test
    public void synchronizeUsers_given_one_user_member_of_one_remote_when_synchronizing_users_only_then_only_user_is_created()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        SynchronizeUserStoreJob job = synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify job status
        SynchronizeStatus synchronizeStatus = job.getStatus();
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getResurrectedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getUpdatedCount() );
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getUpdatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getResurrectedCount() );
        assertEquals( -1, synchronizeStatus.getRemoteGroupsStatus().getTotalCount() );
        assertEquals( -1, synchronizeStatus.getRemoteGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getTotalCount() );
        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getDeletedCount() );
        assertEquals( -1, synchronizeStatus.getLocalGroupsStatus().getTotalCount() );
        assertEquals( -1, synchronizeStatus.getLocalGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getVerifiedCount() );
        assertEquals( -1, synchronizeStatus.getGroupMembershipsStatus().getCurrentCount() );
        assertEquals( -1, synchronizeStatus.getGroupMembershipsStatus().getTotalCount() );

        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getVerifiedCount() );
        assertEquals( 1, synchronizeStatus.getUserMembershipsStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getUserMembershipsStatus().getCurrentCount() );

        // verify created user but not group
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 0, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        UserEntity actualUserLaverne = fixture.findUserByName( "laverne" );
        assertNotNull( actualUserLaverne );
        List<GroupEntity> memberships = Lists.newArrayList( actualUserLaverne.getUserGroup().getMemberships( false ) );
        assertEquals( 0, memberships.size() );
        assertNull( fixture.findGroupByName( "editors" ) );
    }

    @Test
    public void synchronizeUsers_given_one_user_member_of_one_remote_when_synchronizing_groups_only_then_only_group_is_created()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        SynchronizeUserStoreJob job = synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );

        // verify job status
        SynchronizeStatus synchronizeStatus = job.getStatus();
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getResurrectedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getUpdatedCount() );
        assertEquals( -1, synchronizeStatus.getRemoteUsersStatus().getTotalCount() );
        assertEquals( -1, synchronizeStatus.getRemoteUsersStatus().getCurrentCount() );

        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getUpdatedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteGroupsStatus().getResurrectedCount() );
        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getTotalCount() );
        assertEquals( 1, synchronizeStatus.getRemoteGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalUsersStatus().getDeletedCount() );
        assertEquals( -1, synchronizeStatus.getLocalUsersStatus().getTotalCount() );
        assertEquals( -1, synchronizeStatus.getLocalUsersStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getTotalCount() );
        assertEquals( 0, synchronizeStatus.getLocalGroupsStatus().getCurrentCount() );

        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getGroupMembershipsStatus().getVerifiedCount() );
        assertEquals( 1, synchronizeStatus.getGroupMembershipsStatus().getCurrentCount() );
        assertEquals( 1, synchronizeStatus.getGroupMembershipsStatus().getTotalCount() );

        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getCreatedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getDeletedCount() );
        assertEquals( 0, synchronizeStatus.getUserMembershipsStatus().getVerifiedCount() );
        assertEquals( -1, synchronizeStatus.getUserMembershipsStatus().getTotalCount() );
        assertEquals( -1, synchronizeStatus.getUserMembershipsStatus().getCurrentCount() );

        // verify user is not created but group is
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        assertNull( fixture.findUserByName( "laverne" ) );
        assertNotNull( fixture.findGroupByName( "editors" ) );
    }

    @Test
    public void synchronizeUsers_when_remoteUserStore_contains_two_users_with_same_name()
        throws Exception
    {
        addUser( "laverne" );
        addUser( "LAVERNE" );
        addUser( "umshlaba" );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup synchronize job
        SynchronizeUserStoreJob job = synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify job status
        SynchronizeStatus synchronizeStatus = job.getStatus();
        assertEquals( 2, synchronizeStatus.getRemoteUsersStatus().getCreatedCount() );
        assertEquals( 1, synchronizeStatus.getRemoteUsersStatus().getSkippedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getResurrectedCount() );
        assertEquals( 0, synchronizeStatus.getRemoteUsersStatus().getUpdatedCount() );
        assertEquals( 3, synchronizeStatus.getRemoteUsersStatus().getTotalCount() );
        assertEquals( 3, synchronizeStatus.getRemoteUsersStatus().getCurrentCount() );

        // verify created users
        assertEquals( 2, fixture.countUsersByType( UserType.NORMAL ) );
        assertNotNull( fixture.findUserByName( "laverne" ) );
        assertNotNull( fixture.findUserByName( "umshlaba" ) );
    }

    @Test
    public void synchronizeUser_given_user_not_existing_remote_or_in_db_then_UserNotFoundException_is_thrown()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise & verify
        try
        {
            userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "nonexistinguser" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void synchronizeUser_given_user_exists_in_db_but_not_remote_then_user_is_deleted()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: get user stored in db
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        assertEquals( 1, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        assertEquals( 0, fixture.countDeletedUsersByType( UserType.NORMAL ) );

        // setup: rename the user, so the previous user no longer is stored remote
        this.userDatabase.removeUser( user );

        // exercise
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify user is deleted
        assertEquals( 0, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        assertEquals( 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );
    }

    @Test
    public void synchronizeUser_given_user_and_membership_exists_remote_but_not_in_db_then_user_group_and_membership_is_created()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 0, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // exercise
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        UserEntity laverne = fixture.findUserByName( "laverne" );
        assertNotNull( laverne );
        List<GroupEntity> memberships = Lists.newArrayList( laverne.getUserGroup().getMemberships( false ) );
        assertEquals( 1, memberships.size() );
        assertEquals( "editors", memberships.get( 0 ).getName() );
    }

    @Test
    public void synchronizeUser_given_user_and_membership_exists_remote_and_in_db_as_deleted_then_user_is_resurrected()
        throws Exception
    {
        RemoteUser user = addUser( "laverne" );
        RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );
        deleteUser( "laverne" );

        assertEquals( 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );

        user = addUser( "laverne" );
        group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        UserEntity laverne = fixture.findUserByName( "laverne" );
        assertNotNull( laverne );
        List<GroupEntity> memberships = Lists.newArrayList( laverne.getUserGroup().getMemberships( false ) );
        assertEquals( 1, memberships.size() );
        assertEquals( "editors", memberships.get( 0 ).getName() );
    }

    @Test
    public void synchronizeUser_given_change_in_the_displayName_based_fields_in_ldap_and_displayName_have_not_been_manually_editet_then_displayName_is_changed()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify DisplayName before change
        UserEntity laverne = fixture.findUserByName( "laverne" );
        assertEquals( "First Last", laverne.getDisplayName() );

        // change user's Firstname
        user.getUserFields().setFirstName( "Veronica" );
        this.userDatabase.updateUser( user );

        // exercise
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify: user's Firstname and DisplayName have changed
        laverne = fixture.findUserByName( "laverne" );
        assertEquals( "Veronica", laverne.getUserFields().getFirstName() );
        assertEquals( "Veronica Last", laverne.getDisplayName() );
    }

    @Test
    public void synchronizeUser_given_change_in_the_displayName_based_fields_in_ldap_and_displayName_have_been_manually_editet_then_displayName_is_not_changed()
        throws Exception
    {
        final RemoteUser user = addUser( "laverne" );
        final RemoteGroup group = addGroup( "editors" );
        this.userDatabase.addMember( group, user );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify DisplayName before change
        UserEntity laverne = fixture.findUserByName( "laverne" );
        assertEquals( "First Last", laverne.getDisplayName() );

        // setup: change the user's DisplayName
        UserSpecification userSpec = new UserSpecification();
        userSpec.setKey( laverne.getKey() );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userSpec );
        updateUserCommand.setupModifyStrategy();
        updateUserCommand.setDisplayName( "Changed DisplayName" );
        userStoreService.updateUser( updateUserCommand );

        // setup: verify user's DisplayName is changed
        laverne = fixture.findUserByName( "laverne" );
        assertEquals( "Changed DisplayName", laverne.getDisplayName() );

        // change user's Firstname
        user.getUserFields().setFirstName( "Veronica" );
        this.userDatabase.updateUser( user );

        // exercise
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify: user's DisplayName have not changed
        laverne = fixture.findUserByName( "laverne" );
        assertEquals( "Changed DisplayName", laverne.getDisplayName() );

        assertEquals( "Veronica", laverne.getUserFields().getFirstName() );
        assertEquals( "Last", laverne.getUserFields().getLastName() );
    }

    @Test
    public void synchronizeUser_when_remoteUserStore_contains_two_users_with_same_name()
        throws Exception
    {
        addUser( "laverne" );
        addUser( "LAVERNE" );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise again
        userStoreService.synchronizeUser( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(), "laverne" );

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
    }

    @Test
    public void storeNewUser_creating_one_new_user_in_empty_remote_userStore()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        command.setUserFields( userFields );
        userStoreService.storeNewUser( command );

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );

        UserEntity actualUserLaverne = fixture.findUserByName( "laverne" );
        assertNotNull( actualUserLaverne );
        assertEquals( "Laverne Wyatt-Skriubakken", actualUserLaverne.getDisplayName() );
        assertEquals( "laverne", actualUserLaverne.getName() );
        assertEquals( "laverne@test.com", actualUserLaverne.getEmail() );
        assertEquals( UserType.NORMAL, actualUserLaverne.getType() );
        assertEquals( false, actualUserLaverne.isDeleted() );
        assertEquals( "Laverne", actualUserLaverne.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken", actualUserLaverne.getUserFields().getLastName() );
    }

    @Test
    public void storeNewUser_creating_two_new_users_with_same_uid_in_empty_remote_userStore_throws_exception()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // exercise
        StoreNewUserCommand command1 = new StoreNewUserCommand();
        command1.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command1.setUsername( "laverne" );
        command1.setEmail( "laverne@test.com" );
        command1.setDisplayName( "Laverne Wyatt-Skriubakken" );
        command1.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command1.setPassword( "password" );
        UserFields userFields1 = new UserFields();
        userFields1.setFirstName( "Laverne" );
        userFields1.setLastName( "Wyatt-Skriubakken" );
        command1.setUserFields( userFields1 );
        userStoreService.storeNewUser( command1 );

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertNotNull( fixture.findUserByName( "laverne" ) );

        StoreNewUserCommand command2 = new StoreNewUserCommand();
        command2.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command2.setUsername( "laverne" );
        command2.setEmail( "laverne2@test.com" );
        command2.setDisplayName( "Laverne Wyatt-Skriubakken2" );
        command2.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command2.setPassword( "password" );
        UserFields userFields2 = new UserFields();
        userFields2.setFirstName( "Laverne2" );
        userFields2.setLastName( "Wyatt-Skriubakken2" );
        command2.setUserFields( userFields2 );
        try
        {
            userStoreService.storeNewUser( command2 );
            fail( "Expected UserAlreadyExistsException" );
        }
        catch ( Throwable e )
        {
            assertTrue( e instanceof UserAlreadyExistsException );
            UserAlreadyExistsException userAlreadyExistsException = (UserAlreadyExistsException) e;
            assertEquals( "myRemoteUserStore", userAlreadyExistsException.getUserStoreName() );
            assertEquals( "laverne", userAlreadyExistsException.getUid() );
        }
    }

    @Test
    public void storeNewUser_given_userField_which_is_readOnly_then_exception_is_thrown()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "read-only" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Arn" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserFields( userFields );
        try
        {
            userStoreService.storeNewUser( createCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ReadOnlyUserFieldPolicyException );
            assertTrue( e.getMessage().startsWith( "Read only user field not expected: first-name" ) );
        }
    }

    @Test
    public void storeNewUser_given_userField_which_is_not_readOnly_but_others_is_then_exception_is_not_thrown()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "read-only" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserFields( userFields );
        userStoreService.storeNewUser( createCommand );

        assertEquals( null, fixture.findUserByName( "arn" ).getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken", fixture.findUserByName( "arn" ).getUserFields().getLastName() );
    }

    @Test
    public void storeNewUser_creating_a_new_user_with_same_uid_as_existing_in_remote_userStore_throws_exception()
        throws Exception
    {
        addUser( "laverne" );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // synchronize so that remote users are populated in db
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertNotNull( fixture.findUserByName( "laverne" ) );

        // exercise creating new user with same uid as existing
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne2@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken2" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne2" );
        userFields.setLastName( "Wyatt-Skriubakken2" );
        command.setUserFields( userFields );
        try
        {
            userStoreService.storeNewUser( command );
            fail( "Expected UserAlreadyExistsException" );
        }
        catch ( Throwable e )
        {
            assertTrue( e instanceof UserAlreadyExistsException );
            UserAlreadyExistsException userAlreadyExistsException = (UserAlreadyExistsException) e;
            assertEquals( "myRemoteUserStore", userAlreadyExistsException.getUserStoreName() );
            assertEquals( "laverne", userAlreadyExistsException.getUid() );
        }
    }

    @Test
    public void updateUser_given_userField_which_is_readOnly_then_exception_is_thrown()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFieldsForCreate = new UserFields();
        userFieldsForCreate.setFirstName( "Arn" );
        userFieldsForCreate.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserFields( userFieldsForCreate );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "read-only" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        try
        {
            UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
            UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
            updateUserCommand.setupModifyStrategy();
            updateUserCommand.setEmail( "arn@test.com" );
            UserFields userFields = new UserFields();
            userFields.setFirstName( "Changed firstname" );
            updateUserCommand.setUserFields( userFields );
            userStoreService.updateUser( updateUserCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ReadOnlyUserFieldPolicyException );
            assertTrue( e.getMessage().startsWith( "Read only user field not expected: first-name" ) );
        }
    }

    @Test
    public void updateUser_given_no_changes_when_strategy_is_update_then_no_changes_must_be_made()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        createCommand.setUsername( "laverne" );
        createCommand.setEmail( "laverne@test.com" );
        createCommand.setDisplayName( "Laverne Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhone( "11111111" );
        createCommand.setUserFields( userFields );
        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity user = fixture.findUserByName( "laverne" );
        DateTime timestampBeforeUpdate = user.getTimestamp();

        // setup: ensure any updated timestamp on user is after the timestamp before the update command is run
        Thread.sleep( 100 );

        // exercise
        UpdateUserCommand command =
            new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), UserSpecification.usingKey( userKey ) );
        command.setupUpdateStrategy();
        command.setEmail( "laverne@test.com" );
        userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhone( "11111111" );
        command.setUserFields( userFields );

        // verify: user's timestamp have not changed
        userStoreService.updateUser( command );
        UserEntity updatedUser = fixture.findUserByKey( userKey );
        assertEquals( "timestamp to be equal the timestamp before update", timestampBeforeUpdate, updatedUser.getTimestamp() );
    }

    @Test
    @Ignore("Not working for memory user store")
    public void updateUser_given_no_changed_values_for_readOnly_fields_when_strategy_is_update_then_no_exception_thrown_and_user_is_not_changed()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity user = fixture.findUserByName( "laverne" );
        DateTime timestampBeforeUpdate = user.getTimestamp();
        UserKey userKey = user.getKey();

        // seutp: make the user fields first name and last name read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote, read-only" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote, read-only" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.INITIALS, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "remote" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // setup: ensure any updated timestamp on user is after the timestamp before the update command is run
        Thread.sleep( 100 );
        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setKey( userKey );

        // exercise:
        UpdateUserCommand command = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userSpecification );
        command.setupUpdateStrategy();
        command.setEmail( "laverne@test.com" );
        command.setRemovePhoto( false );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhone( "12345678" );
        command.setUserFields( userFields );

        userStoreService.updateUser( command );
        UserEntity updatedUser = fixture.findUserByKey( userKey );
        assertEquals( "timestamp to be equal the timestamp before update", timestampBeforeUpdate, updatedUser.getTimestamp() );
    }

    @Test
    @Ignore("Not working for memory user store")
    public void updateUser_given_changed_email_when_userPolicy_is_not_update_then_exception_is_thrown()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity user = fixture.findUserByName( "laverne" );
        UserKey userKey = user.getKey();

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setKey( userKey );

        // exercise
        UpdateUserCommand command = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userSpecification );
        command.setupModifyStrategy();
        command.setEmail( "changed@test.com" );

        try
        {
            userStoreService.updateUser( command );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserStoreConnectorPolicyBrokenException );
            assertTrue( e.getMessage().startsWith(
                "Userstore connector policy broken for userstore 'myRemoteUserStore' using connector 'myRemoteUserStore': Trying to update email on a user store without 'update' policy." ) );
        }
    }

    @Test
    public void updateUser_given_changed_userField_which_is_local_when_userPolicy_is_not_update_policy_then_field_should_be_updated()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.INITIALS, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.DESCRIPTION, "remote" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity laverne = fixture.findUserByName( "laverne" );
        assertEquals( null, laverne.getUserFields().getPhone() );
        assertEquals( null, laverne.getUserFields().getBirthday() );
        UserKey userKey = laverne.getKey();

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setKey( userKey );

        // exercise
        UpdateUserCommand command = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userSpecification );
        command.setupModifyStrategy();
        UserFields userFields = new UserFields();
        userFields.setPhone( "11111111" );
        userFields.setBirthday( new DateMidnight( 1976, 4, 19 ).toDate() );
        command.setUserFields( userFields );
        userStoreService.updateUser( command );

        // verify
        laverne = fixture.findUserByName( "laverne" );
        assertEquals( "11111111", laverne.getUserFields().getPhone() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), laverne.getUserFields().getBirthday() );

        // exercise again
        command = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userSpecification );
        command.setupModifyStrategy();
        userFields = new UserFields();
        userFields.setPhone( "22222222" );
        userFields.setBirthday( new DateMidnight( 1976, 1, 19 ).toDate() );
        command.setUserFields( userFields );
        userStoreService.updateUser( command );

        // verify
        laverne = fixture.findUserByName( "laverne" );
        assertEquals( "22222222", laverne.getUserFields().getPhone() );
        assertEquals( new DateMidnight( 1976, 1, 19 ).toDate(), laverne.getUserFields().getBirthday() );
    }

    @Test
    public void updateUser_given_changes_when_userPolicy_is_not_update_and_strategy_is_update_then_exception_is_thrown()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify: arn must not exist in db
        assertNull( fixture.findUserByName( "laverne" ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // after synchronization we have:
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity user = fixture.findUserByName( "laverne" );

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setName( "laverne" );

        UpdateUserCommand command = new UpdateUserCommand( user.getKey(), userSpecification );
        command.setupModifyStrategy();
        command.setAllowUpdateSelf( true );
        command.setDisplayName( "" );
        command.setEmail( "laverne@test.com" );
        command.setRemovePhoto( false );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhone( "" );        // Removing existing attribute
        userFields.setInitials( "LWS" );  // Setting new attribute
        command.setUserFields( userFields );

        try
        {
            userStoreService.updateUser( command );
            fail( "Updating a user with userPolicy set to read-only should throw an Exception." );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserStoreConnectorPolicyBrokenException );
        }
    }

    @Test
    public void updateUser_setting_new_remote_attribute_and_removing_existing_attribute()
        throws Exception
    {
        addUser( "laverne" );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "update" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify: arn must not exist in db
        assertNull( fixture.findUserByName( "laverne" ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // after synchronization we have:
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        UserEntity user = fixture.findUserByName( "laverne" );
        assertNotNull( user );
        assertEquals( "12345678", user.getUserFields().getPhone() );
        assertEquals( null, user.getUserFields().getInitials() );

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setName( "laverne" );
        UpdateUserCommand command = new UpdateUserCommand( user.getKey(), userSpecification );
        command.setupModifyStrategy();
        command.setAllowUpdateSelf( true );
        command.setDisplayName( "" );
        command.setEmail( "laverne@test.com" );
        command.setRemovePhoto( false );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhone( "" );        // Removing existing attribute
        userFields.setInitials( "LWS" );  // Setting new attribute
        command.setUserFields( userFields );

        userStoreService.updateUser( command );

        UserEntity updatedUser = fixture.findUserByName( "laverne" );
        assertEquals( "LWS", updatedUser.getUserFields().getInitials() );
        assertEquals( "", updatedUser.getUserFields().getPhone() );
    }

    @Test
    public void updateUser_given_not_all_fields_when_modify_strategy_then_missing_fields_are_not_changed()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHOTO, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.GENDER, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LOCALE, "local" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "laverne" );
        createCommand.setEmail( "laverne@test.com" );
        createCommand.setDisplayName( "Laverne Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhoto( new byte[]{100} );
        userFields.setPhone( "11111111" );
        userFields.setGender( Gender.FEMALE );
        userFields.setBirthday( new DateMidnight( 1980, 1, 1 ).toDate() );
        userFields.setHomePage( "www.skriubakken.com" );
        userFields.setLocale( Locale.ENGLISH );
        createCommand.setUserFields( userFields );
        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // exercise: every user field omitted except last name
        UpdateUserCommand command =
            new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), UserSpecification.usingKey( userKey ) );
        command.setupModifyStrategy();
        command.setEmail( "laverne@test.com" );
        userFields = new UserFields();
        userFields.setLastName( "Changed lastname" );
        command.setUserFields( userFields );
        userStoreService.updateUser( command );

        // verify: every user field omitted is unchanged
        UserEntity updatedUser = fixture.findUserByKey( userKey );
        assertEquals( "Laverne Wyatt-Skriubakken", updatedUser.getDisplayName() );
        assertEquals( "Laverne", updatedUser.getUserFields().getFirstName() );
        assertArrayEquals( new byte[]{100}, updatedUser.getUserFields().getPhoto() );
        assertEquals( "11111111", updatedUser.getUserFields().getPhone() );
        assertEquals( Gender.FEMALE, updatedUser.getUserFields().getGender() );
        assertEquals( new DateMidnight( 1980, 1, 1 ).toDate(), updatedUser.getUserFields().getBirthday() );
        assertEquals( "www.skriubakken.com", updatedUser.getUserFields().getHomePage() );
        assertEquals( Locale.ENGLISH, updatedUser.getUserFields().getLocale() );
        // verify: those changed are changed
        assertEquals( "Changed lastname", updatedUser.getUserFields().getLastName() );
    }

    @Test
    public void updateUser_given_not_all_fields_when_update_strategy_then_missing_fields_are_changed_to_null()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myRemoteUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHOTO, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.PHONE, "remote" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.GENDER, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "local" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LOCALE, "local" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "laverne" );
        createCommand.setEmail( "laverne@test.com" );
        createCommand.setDisplayName( "Laverne Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setPhoto( new byte[]{100} );
        userFields.setPhone( "11111111" );
        userFields.setGender( Gender.FEMALE );
        userFields.setBirthday( new DateMidnight( 1980, 1, 1 ).toDate() );
        userFields.setHomePage( "www.skriubakken.com" );
        userFields.setLocale( Locale.ENGLISH );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // exercise: every user field omitted except last name
        UpdateUserCommand command =
            new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), UserSpecification.usingKey( userKey ) );
        command.setupUpdateStrategy();
        command.setEmail( "laverne@test.com" );
        userFields = new UserFields();
        userFields.setLastName( "Changed lastname" );
        command.setUserFields( userFields );

        userStoreService.updateUser( command );

        // verify: every user field omitted is nulled
        UserEntity updatedUser = fixture.findUserByKey( userKey );
        assertEquals( "Laverne Wyatt-Skriubakken", updatedUser.getDisplayName() );
        assertEquals( null, updatedUser.getUserFields().getFirstName() );
        assertEquals( null, updatedUser.getUserFields().getPhone() );
        assertEquals( null, updatedUser.getUserFields().getGender() );
        assertEquals( null, updatedUser.getUserFields().getBirthday() );
        assertEquals( null, updatedUser.getUserFields().getHomePage() );
        assertEquals( null, updatedUser.getUserFields().getLocale() );
        // verify: photo is special and only remove if told so with command.setRemovePhoto
        assertNotNull( updatedUser.getUserFields().getPhoto() );
        // verify: those changed are changed
        assertEquals( "Changed lastname", updatedUser.getUserFields().getLastName() );
    }

    @Test
    public void synchronizeUsers_locally_existing_user_is_deleted_and_new_created_when_user_have_been_renamed_remotely()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // verify: laverne must not exist in db before synchronization.
        assertNull( fixture.findUserByName( "laverne" ) );

        // setup: synchronize with remote
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // after synchronization we have:
        // local (laverne)  - remote (laverne)

        // setup: make the remote group editors a member of the built-in contributors group (to enable admin)
        createMembershipToGroupOfType( "editors", GroupType.CONTRIBUTORS );

        // setup: rename remote user laverne to veronica
        final RemoteUser remoteUser2 = addUser( "veronica" );
        this.userDatabase.removeMember( remoteGroup, remoteUser );
        this.userDatabase.removeUser( remoteUser );
        this.userDatabase.addMember( remoteGroup, remoteUser2 );

        // dirContext.rename( "uid=laverne,ou=users,dc=bogus,dc=com", "uid=veronica,ou=users,dc=bogus,dc=com" );
        // setup: make remote user veronica member of the remote group editors
        // addToAttribute( "member", "uid=veronica,ou=users,dc=bogus,dc=com", "cn=editors,ou=groups,dc=bogus,dc=com" );

        // exercise
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // verify: laverne is deleted
        assertEquals( true, fixture.findUserByName( "laverne" ).isDeleted() );

        // verify: veronica is created with same email
        assertNotNull( fixture.findUserByName( "veronica" ) );
        assertEquals( "veronica@test.com", fixture.findUserByName( "veronica" ).getEmail() );
    }

    @Test
    public void storeNewUser_given_deleted_user_when_resurrectDeletedUsers_is_true_then_deleted_user_is_resurrected()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "true" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize so that remote users are populated in db
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify created user
        assertEquals( 1, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        UserKey oldUserKey = fixture.findUserByName( "laverne" ).getKey();
        // setup: delete user
        deleteUser( "laverne" );
        assertEquals( 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );

        // exercise: create new user with same uid as existing
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken2" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        command.addMembership( fixture.findGroupByType( GroupType.CONTRIBUTORS ).getGroupKey() );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne2" );
        userFields.setLastName( "Wyatt-Skriubakken2" );
        command.setUserFields( userFields );
        UserKey newUserKey = userStoreService.storeNewUser( command );

        // verify
        assertEquals( "1 user in total", 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "1 user to be not deleted", 1, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        assertEquals( "0 users to be deleted", 0, fixture.countDeletedUsersByType( UserType.NORMAL ) );
        assertEquals( "newUserKey to be equal oldUserKey", oldUserKey, newUserKey );

        // verify: resurrected user have new values
        UserEntity resurrectedUser = fixture.findUserByKey( newUserKey );
        assertEquals( "laverne", resurrectedUser.getName() );
        assertEquals( "laverne@test.com", resurrectedUser.getEmail() );
        assertEquals( "Laverne Wyatt-Skriubakken2", resurrectedUser.getDisplayName() );
        assertEquals( "Laverne2", resurrectedUser.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken2", resurrectedUser.getUserFields().getLastName() );
        assertTrue( "expected user to be member of group CONTRIBUTORS",
                    resurrectedUser.getDirectMemberships().contains( fixture.findGroupByType( GroupType.CONTRIBUTORS ) ) );
    }

    @Test
    public void storeNewUser_when_resurrecting_deleted_user_and_new_user_does_not_have_all_userFields_then_these_userFields_are_deleted()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "true" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize so that remote users are populated in db
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_ONLY );

        // setup: verify created user has phone number
        assertEquals( "12345678", fixture.findUserByName( "laverne" ).getUserFields().getPhone() );
        // setup: delete user
        deleteUser( "laverne" );

        // exercise: create new user with same uid
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken2" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        command.addMembership( fixture.findGroupByType( GroupType.CONTRIBUTORS ).getGroupKey() );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne2" );
        userFields.setLastName( "Wyatt-Skriubakken2" );
        command.setUserFields( userFields );
        UserKey newUserKey = userStoreService.storeNewUser( command );

        // verify: user no longer have phone number
        UserEntity resurrectedUser = fixture.findUserByKey( newUserKey );
        assertEquals( null, resurrectedUser.getUserFields().getPhone() );
    }

    @Test
    public void storeNewUser_given_deleted_user_when_resurrectDeletedUsers_is_false_then_new_user_is_created_and_deleted_is_made_non_syncable()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup remoteGroup = addGroup( "editors" );
        this.userDatabase.addMember( remoteGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: synchronize so that remote users are populated in db
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.USERS_AND_GROUPS );

        // setup: verify created user
        assertEquals( 1, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        UserKey oldUserKey = fixture.findUserByName( "laverne" ).getKey();
        assertEquals( "12345678", fixture.findUserByName( "laverne" ).getUserFields().getPhone() );
        // setup: delete user
        deleteUser( "laverne" );
        assertEquals( 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );

        // exercise: create new user with same uid as existing
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        command.setUsername( "laverne" );
        command.setEmail( "laverne@test.com" );
        command.setDisplayName( "Laverne Wyatt-Skriubakken2" );
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setPassword( "password" );
        command.addMembership( fixture.findGroupByType( GroupType.CONTRIBUTORS ).getGroupKey() );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Laverne2" );
        userFields.setLastName( "Wyatt-Skriubakken2" );
        command.setUserFields( userFields );
        UserKey newUserKey = userStoreService.storeNewUser( command );

        // verify
        assertEquals( "1 user in total", 2, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "1 user to be not deleted", 1, fixture.countNonDeletedUsersByType( UserType.NORMAL ) );
        assertEquals( "1 users to be deleted", 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );
        assertFalse( "newUserKey to be not equal oldUserKey", oldUserKey.equals( newUserKey ) );

        // verify: new user as new values
        UserEntity newUser = fixture.findUserByKey( newUserKey );
        assertEquals( "laverne", newUser.getName() );
        assertEquals( "laverne@test.com", newUser.getEmail() );
        assertEquals( "Laverne Wyatt-Skriubakken2", newUser.getDisplayName() );
        assertEquals( "Laverne2", newUser.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken2", newUser.getUserFields().getLastName() );
        assertEquals( null, newUser.getUserFields().getPhone() );
        assertTrue( "expected user to be member of group CONTRIBUTORS",
                    newUser.getDirectMemberships().contains( fixture.findGroupByType( GroupType.CONTRIBUTORS ) ) );
    }

    @Test
    public void synchronizeGroups()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup editorsGroup = addGroup( "editors" );
        final RemoteGroup writersGroup = addGroup( "writers" );
        final RemoteGroup cleanersGroup = addGroup( "cleaners" );

        this.userDatabase.addMember( editorsGroup, remoteUser );
        this.userDatabase.addMember( writersGroup, editorsGroup );
        this.userDatabase.addMember( cleanersGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // exercise
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );

        // verify
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( 3, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );
        GroupEntity editors = fixture.findGroupByName( "editors" );
        assertNotNull( editors );

        List<GroupEntity> editorsMemberships = Lists.newArrayList( editors.getMemberships( false ) );
        assertEquals( 1, editorsMemberships.size() );
        assertEquals( "writers", editorsMemberships.get( 0 ).getName() );
    }

    @Test
    public void synchronizeGroup_add_membership()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup editorsGroup = addGroup( "editors" );
        final RemoteGroup writersGroup = addGroup( "writers" );
        final RemoteGroup cleanersGroup = addGroup( "cleaners" );

        this.userDatabase.addMember( editorsGroup, remoteUser );
        this.userDatabase.addMember( writersGroup, editorsGroup );
        this.userDatabase.addMember( cleanersGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );

        // setup: verify editors have one membership - with writers
        GroupEntity editors = fixture.findGroupByName( "editors" );
        assertEquals( 1, editors.getMemberships( false ).size() );
        List<GroupEntity> editorsMemberships = Lists.newArrayList( editors.getMemberships( false ) );
        assertEquals( "writers", editorsMemberships.get( 0 ).getName() );

        // setup: verify cleaners have no members, since we only synchronized groups
        GroupEntity cleaners = fixture.findGroupByName( "cleaners" );
        List<GroupEntity> cleanersMembers = Lists.newArrayList( cleaners.getMembers( false ) );
        assertEquals( 0, cleanersMembers.size() );

        // setup: make editors member of cleaners too
        this.userDatabase.addMember( cleanersGroup, editorsGroup );

        // exercise
        userStoreService.synchronizeGroup( fixture.findGroupByName( "editors" ).getGroupKey() );

        // verify: editors is member of both writes and cleaners
        editors = fixture.findGroupByName( "editors" );
        editorsMemberships = Lists.newArrayList( editors.getMemberships( false ) );
        assertEquals( 2, editorsMemberships.size() );
        assertEquals( "writers", editorsMemberships.get( 0 ).getName() );
        assertEquals( "cleaners", editorsMemberships.get( 1 ).getName() );

        // verify: cleaners have now editors as a member
        cleaners = fixture.findGroupByName( "cleaners" );
        cleanersMembers = Lists.newArrayList( cleaners.getMembers( false ) );
        assertEquals( 1, cleanersMembers.size() );
        assertEquals( "editors", cleanersMembers.get( 0 ).getName() );
    }

    @Test
    public void synchronizeGroup_membership_removed()
        throws Exception
    {
        final RemoteUser remoteUser = addUser( "laverne" );
        final RemoteGroup editorsGroup = addGroup( "editors" );
        final RemoteGroup writersGroup = addGroup( "writers" );
        final RemoteGroup cleanersGroup = addGroup( "cleaners" );

        this.userDatabase.addMember( editorsGroup, remoteUser );
        this.userDatabase.addMember( writersGroup, editorsGroup );
        this.userDatabase.addMember( cleanersGroup, remoteUser );

        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );

        // setup: verify editors have one membership - with writers
        GroupEntity editors = fixture.findGroupByName( "editors" );
        assertEquals( 1, editors.getMemberships( false ).size() );
        List<GroupEntity> editorsMemberships = Lists.newArrayList( editors.getMemberships( false ) );
        assertEquals( "writers", editorsMemberships.get( 0 ).getName() );

        // setup: make editors no longer member of writers
        this.userDatabase.removeMember( writersGroup, editorsGroup );

        // exercise
        userStoreService.synchronizeGroup( fixture.findGroupByName( "editors" ).getGroupKey() );
        fixture.flushAndClearHibernateSession();

        // verify: editor no longer member of writer
        editors = fixture.findGroupByName( "editors" );
        assertEquals( 0, editors.getMemberships( false ).size() );
        GroupEntity writers = fixture.findGroupByName( "writers" );
        List<GroupEntity> writersMembers = Lists.newArrayList( writers.getMembers( false ) );
        assertEquals( 0, writersMembers.size() );
    }

    @Test
    public void storeNewGroup()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // exercise: create new group
        StoreNewGroupCommand command = new StoreNewGroupCommand();
        command.setExecutor( fixture.findUserByName( "admin" ) );
        command.setType( GroupType.USERSTORE_GROUP );
        command.setName( "myGroup" );
        command.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        GroupKey newGroupKey = userStoreService.storeNewGroup( command );

        // verify: group stored in db
        GroupEntity group = fixture.findGroupByName( "myGroup" );
        assertNotNull( group );
        assertEquals( newGroupKey, group.getGroupKey() );

        // verify: group stored in ldap
        List<RemoteGroup> remoteGroups = getRemoteUserStoreConnector( "myRemoteUserStore" ).getAllGroups();
        assertEquals( 1, remoteGroups.size() );
        assertEquals( "myGroup", remoteGroups.get( 0 ).getId() );
    }

    @Test
    public void storeNewGroup_given_group_with_name_that_is_already_used_then_exception_is_thrown()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "ALL" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "ALL" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        userStoreService.storeNewGroup( storeNewGroupCommand );

        // exercise & verify
        try
        {
            userStoreService.storeNewGroup( storeNewGroupCommand );
            fail( "Exception expected" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof GroupAlreadyExistsException );
        }
    }

    @Test
    public void deleteGroup()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: create group to delete
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // exercise
        GroupSpecification groupToDeleteSpec = new GroupSpecification();
        groupToDeleteSpec.setKey( groupKey );
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( fixture.findUserByName( "admin" ), groupToDeleteSpec );
        userStoreService.deleteGroup( deleteGroupCommand );

        // verify:
        GroupEntity deletedGroup = fixture.findGroupByKey( groupKey );
        assertEquals( true, deletedGroup.isDeleted() );
    }

    @Test
    public void updateGroup()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // exercise
        UpdateGroupCommand updateGroupCommand = new UpdateGroupCommand( fixture.findUserByName( "admin" ).getKey(), groupKey );
        updateGroupCommand.setName( "myGroup" );
        updateGroupCommand.setDescription( "Changed" );
        userStoreService.updateGroup( updateGroupCommand );

        // verify:
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );
        GroupEntity updatedGroup = fixture.findGroupByKey( groupKey );
        assertEquals( false, updatedGroup.isDeleted() );
        assertEquals( "Changed", updatedGroup.getDescription() );
    }

    @Test
    public void updateGroup_given_different_name_from_what_is_stored_remote_then_exception_is_thrown()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedUsers", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // exercise
        UpdateGroupCommand updateGroupCommand = new UpdateGroupCommand( fixture.findUserByName( "admin" ).getKey(), groupKey );
        updateGroupCommand.setName( "myChange" );
        updateGroupCommand.setDescription( "Changed" );
        try
        {
            userStoreService.updateGroup( updateGroupCommand );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            assertTrue( e instanceof IllegalArgumentException );
            assertTrue( e.getMessage().startsWith( "Changing names of a groups in remote user stores is not supported" ) );
        }
    }

    @Test
    public void storeNewGroup_given_deleted_name_when_resurrectDeletedGroups_is_false_then_new_group_is_created_and_deleted_is_made_non_syncable()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedGroups", "false" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: create new group
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        GroupKey previousGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );

        // setup: delete the group
        deleteGroup( previousGroupKey );

        // exercise:
        GroupKey newGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );

        // verify: new group
        assertEquals( 2, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        GroupEntity newGroup = fixture.findGroupByKey( newGroupKey );
        assertEquals( false, newGroup.isDeleted() );

        // verify: deleted group is made non syncable
        GroupEntity previousDeletedGroup = fixture.findGroupByKey( previousGroupKey );
        assertEquals( true, previousDeletedGroup.isDeleted() );
        assertFalse( previousDeletedGroup.getSyncValue().equals( newGroup.getSyncValue() ) );
        assertTrue( previousDeletedGroup.getSyncValue().contains( "nonRepeatable" ) );

        // verify: sync does not fail with exception
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );
    }

    @Test
    public void storeNewGroup_given_deleted_name_when_resurrectDeletedGroups_is_true_then_deleted_group_is_resurrected()
        throws Exception
    {
        // setup vertical properties
        ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.resurrectDeletedGroups", "true" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "create,update,delete,updatePassword" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "read,create,update,delete" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( properties );

        // setup: create new group
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey() );
        GroupKey previousGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );

        // setup: delete the group
        deleteGroup( previousGroupKey );

        // exercise:
        GroupKey newGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );

        // verify:
        assertEquals( previousGroupKey, newGroupKey );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // verify: sync does not fail with exception
        synchronizeUserStore( "myRemoteUserStore", SynchronizeUserStoreType.GROUPS_ONLY );
    }

    private void deleteGroup( GroupKey group )
    {
        GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setKey( group );
        DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( fixture.findUserByName( "admin" ), groupSpec );
        userStoreService.deleteGroup( deleteGroupCommand );
    }

    private RemoteUserStoreConnector getRemoteUserStoreConnector( String userStoreName )
    {
        return userStoreConnectorManager.getRemoteUserStoreConnector( fixture.findUserStoreByName( userStoreName ).getKey() );
    }

    private SynchronizeUserStoreJob synchronizeUserStore( String userStoreName, SynchronizeUserStoreType type )
    {
        final SynchronizeUserStoreJob job =
            synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( userStoreName ).getKey(), type,
                                                                          100 );
        job.start();
        fixture.flushAndClearHibernateSession();
        return job;
    }

    private void deleteUser( String name )
    {
        UserSpecification userToDelete = new UserSpecification();
        userToDelete.setName( name );
        DeleteUserCommand deleteUserCommand = new DeleteUserCommand( fixture.findUserByName( "admin" ).getKey(), userToDelete );
        userStoreService.deleteUser( deleteUserCommand );
    }

    private void createMembershipToGroupOfType( String memberName, GroupType groupType )
    {
        GroupSpecification contributorsSpec = new GroupSpecification();
        contributorsSpec.setName( memberName );
        AddMembershipsCommand addMembershipsCommand =
            new AddMembershipsCommand( contributorsSpec, fixture.findUserByName( User.ROOT_UID ).getKey() );
        addMembershipsCommand.addGroupToAddTo( fixture.findGroupByType( groupType ).getGroupKey() );
        userStoreService.addMembershipsToGroup( addMembershipsCommand );
        fixture.flushAndClearHibernateSession();
    }

    private RemoteUser addUser( final String id )
    {
        final RemoteUser user = new RemoteUser( id );
        user.setEmail( id + "@test.com" );
        user.getUserFields().setFirstName( "First" );
        user.getUserFields().setLastName( "Last" );
        user.getUserFields().setPhone( "12345678" );
        this.userDatabase.addUser( user );
        this.userDatabase.setPassword( user.getId(), "mypassword" );
        return user;
    }

    private RemoteGroup addGroup( final String name )
    {
        final RemoteGroup group = new RemoteGroup( name );
        this.userDatabase.addGroup( group );
        return group;
    }
}
