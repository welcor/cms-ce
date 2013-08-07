/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.security.userstore;

import java.util.Locale;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.api.client.model.user.Gender;
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
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.api.plugin.ext.userstore.UserField;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;

public class UserStoreServiceImpl_localUserStoreTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private UserStoreConnectorManager userStoreConnectorManager;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private UserStoreService userStoreService;

    @Before
    public void setUp()
        throws Exception
    {
        factory = fixture.getFactory();
        fixture.initSystemData();

        UserStoreEntity userStore = factory.createUserStore( "myLocalUserStore", null, true );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
    }

    @Test
    public void storeNewUser_given_userField_which_is_readOnly_then_exception_is_thrown()
        throws Exception
    {

        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
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
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
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
    public void updateUser_given_userField_which_is_readOnly_then_exception_is_thrown()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Arn" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
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
            userFields = new UserFields();
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
    public void updateUser_given_missing_user_fields_when_strategy_is_update_then_fields_are_nulled()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Arn" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        userFields.setOrganization( "Organization" );
        userFields.setBirthday( new DateMidnight( 2010, 3, 18 ).toDate() );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
        updateUserCommand.setupUpdateStrategy();
        updateUserCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        updateUserCommand.setUserFields( userFields );
        userStoreService.updateUser( updateUserCommand );

        // verify
        userFields = fixture.findUserByName( "arn" ).getUserFields();
        assertEquals( null, userFields.getOrganization() );
        assertEquals( null, userFields.getBirthday() );
    }

    @Test
    public void updateUser_given_missing_user_fields_when_strategy_is_modify_then_fields_are_unchanged()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setOrganization( "Organization" );
        userFields.setBirthday( new DateMidnight( 2010, 3, 18 ).toDate() );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
        updateUserCommand.setupModifyStrategy();
        updateUserCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        updateUserCommand.setUserFields( userFields );
        userStoreService.updateUser( updateUserCommand );

        // verify
        userFields = fixture.findUserByName( "arn" ).getUserFields();
        assertEquals( "Organization", userFields.getOrganization() );
        assertEquals( new DateMidnight( 2010, 3, 18 ).toDate(), userFields.getBirthday() );
    }

    @Test
    public void updateUser_given_user_fields_with_value_null_when_strategy_is_update_then_fields_are_nulled()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setOrganization( "Organization" );
        userFields.setBirthday( new DateMidnight( 2010, 3, 18 ).toDate() );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
        updateUserCommand.setupUpdateStrategy();
        updateUserCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        userFields.add( new UserField( UserFieldType.ORGANIZATION, null ) );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, null ) );
        updateUserCommand.setUserFields( userFields );
        userStoreService.updateUser( updateUserCommand );

        // verify
        userFields = fixture.findUserByName( "arn" ).getUserFields();
        assertEquals( null, userFields.getOrganization() );
        assertEquals( null, userFields.getBirthday() );
    }

    @Test
    public void updateUser_given_textual_user_field_with_value_null_when_strategy_is_modfiy_then_field_is_unchanged()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setOrganization( "Organization" );
        userFields.setBirthday( new DateMidnight( 2010, 3, 18 ).toDate() );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
        updateUserCommand.setupModifyStrategy();
        updateUserCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        userFields.add( new UserField( UserFieldType.ORGANIZATION, null ) );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, null ) );
        updateUserCommand.setUserFields( userFields );
        userStoreService.updateUser( updateUserCommand );

        // verify
        userFields = fixture.findUserByName( "arn" ).getUserFields();
        assertEquals( "Organization", userFields.getOrganization() );
    }

    @Test
    public void updateUser_given_non_textual_user_fields_with_value_null_when_strategy_is_modfiy_then_fields_are_nulled()
        throws Exception
    {

        // setup:
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setLocale( Locale.FRENCH );
        userFields.setBirthday( new DateMidnight( 2010, 3, 18 ).toDate() );
        createCommand.setUserFields( userFields );

        UserKey userKey = userStoreService.storeNewUser( createCommand );

        // setup: make firstname read only
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LOCALE, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
        userStoreConnectorManager.invalidateCachedConfig( userStore.getKey() );

        // exercise & verify
        UserSpecification userToUpdateSpec = UserSpecification.usingKey( userKey );
        UpdateUserCommand updateUserCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdateSpec );
        updateUserCommand.setupModifyStrategy();
        updateUserCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        userFields.add( new UserField( UserFieldType.LOCALE, null ) );
        userFields.add( new UserField( UserFieldType.BIRTHDAY, null ) );
        updateUserCommand.setUserFields( userFields );
        userStoreService.updateUser( updateUserCommand );

        // verify
        userFields = fixture.findUserByName( "arn" ).getUserFields();
        assertEquals( null, userFields.getLocale() );
        assertEquals( null, userFields.getBirthday() );
    }

    @Test
    public void updateUser_replaceAll_changing_names()
        throws Exception
    {

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );

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
        userStoreService.storeNewUser( createCommand );

        fixture.flushAndClearHibernateSession();

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "Arn Wyatt-Skriubakken", fixture.findUserByName( "arn" ).getDisplayName() );

        UserSpecification userToUpdate = new UserSpecification();
        userToUpdate.setKey( fixture.findUserByName( "arn" ).getKey() );
        UpdateUserCommand updateCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdate );
        updateCommand.setupUpdateStrategy();
        updateCommand.setDisplayName( "Wyatt-Skriubakken" );
        updateCommand.setEmail( "arn@test.com" );
        userFields = new UserFields();
        userFields.setFirstName( "Arn Umshlaba" );
        userFields.setLastName( "Wyatt-Zulu-Skriubakken" );
        updateCommand.setUserFields( userFields );
        userStoreService.updateUser( updateCommand );

        fixture.flushAndClearHibernateSession();

        UserEntity actualUser = fixture.findUserByName( "arn" );
        assertEquals( "Wyatt-Skriubakken", actualUser.getDisplayName() );
        assertEquals( "Arn Umshlaba", actualUser.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Zulu-Skriubakken", actualUser.getUserFields().getLastName() );
    }

    @Test
    public void updateUser_replaceAll_omitting_names()
        throws Exception
    {

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );

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
        userStoreService.storeNewUser( createCommand );

        fixture.flushAndClearHibernateSession();

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "Arn Wyatt-Skriubakken", fixture.findUserByName( "arn" ).getDisplayName() );

        UserSpecification userToUpdate = new UserSpecification();
        userToUpdate.setKey( fixture.findUserByName( "arn" ).getKey() );
        UpdateUserCommand updateCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdate );
        updateCommand.setupUpdateStrategy();
        updateCommand.setDisplayName( "Wyatt-Skriubakken" );
        updateCommand.setEmail( "arn@test.com" );
        UserFields userInfoForUpdate = new UserFields();
        updateCommand.setUserFields( userInfoForUpdate );
        userStoreService.updateUser( updateCommand );

        fixture.flushAndClearHibernateSession();

        UserEntity actualUser = fixture.findUserByName( "arn" );
        assertEquals( "Wyatt-Skriubakken", actualUser.getDisplayName() );
        assertEquals( null, actualUser.getUserFields().getFirstName() );
        assertEquals( null, actualUser.getUserFields().getLastName() );
    }

    @Test
    public void updateUser_given_not_all_fields_when_modify_strategy_then_missing_fields_are_not_changed()
        throws Exception
    {
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
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
        createCommand.setUsername( "Laverne" );
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
        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );
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
        createCommand.setUsername( "Laverne" );
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
    public void storeNewUser_given_deleted_uid_then_new_user_is_created()
        throws Exception
    {
        // setup: exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserFields userFields = new UserFields();
        userFields.setFirstName( "Arn" );
        userFields.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserFields( userFields );
        UserKey oldUserKey = userStoreService.storeNewUser( createCommand );

        // setup: verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );

        // setup: delete user
        deleteUser( "arn" );
        assertEquals( 1, fixture.countDeletedUsersByType( UserType.NORMAL ) );

        // exercise: create new user with same uid
        createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken2" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        userFields = new UserFields();
        userFields.setFirstName( "Arn2" );
        userFields.setLastName( "Wyatt-Skriubakken2" );
        createCommand.setUserFields( userFields );
        UserKey newUserKey = userStoreService.storeNewUser( createCommand );

        // verify: new user is created
        UserEntity newUser = fixture.findUserByKey( newUserKey );
        assertEquals( false, newUser.isDeleted() );
        assertEquals( "arn", newUser.getName() );
        assertEquals( "arn@test.com", newUser.getEmail() );
        assertEquals( "Arn Wyatt-Skriubakken2", newUser.getDisplayName() );
        assertEquals( "Arn2", newUser.getUserFields().getFirstName() );
        assertEquals( "Wyatt-Skriubakken2", newUser.getUserFields().getLastName() );

        // verify: old user is still deleted
        UserEntity oldUser = fixture.findUserByKey( oldUserKey );
        assertEquals( true, oldUser.isDeleted() );
    }

    @Test
    public void storeNewGroup()
        throws Exception
    {
        // exercise:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        GroupEntity actualGroup = fixture.findGroupByKey( groupKey );

        // verify:
        assertEquals( "myGroup", actualGroup.getName() );
        assertEquals( "Description", actualGroup.getDescription() );
        assertEquals( GroupType.USERSTORE_GROUP, actualGroup.getType() );
    }

    @Test
    public void storeNewGroup_given_group_with_name_that_is_already_used_then_exception_is_thrown()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
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
    public void storeNewGroup_given_group_with_name_that_is_already_used_but_is_deleted_then_new_group_is_stored()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        DeleteGroupCommand deleteGroupCommand =
            new DeleteGroupCommand( fixture.findUserByName( "admin" ), GroupSpecification.usingKey( groupKey ) );
        userStoreService.deleteGroup( deleteGroupCommand );
        assertEquals( true, fixture.findGroupByKey( groupKey ).isDeleted() );

        // exercise & verify
        GroupKey newGroupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertNotNull( newGroupKey );
        assertFalse( groupKey.equals( newGroupKey ) );
        assertEquals( false, fixture.findGroupByKey( newGroupKey ).isDeleted() );
    }

    @Test
    public void deleteGroup()
        throws Exception
    {
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
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
        // setup:
        StoreNewGroupCommand storeNewGroupCommand = new StoreNewGroupCommand();
        storeNewGroupCommand.setExecutor( fixture.findUserByName( "admin" ) );
        storeNewGroupCommand.setUserStoreKey( fixture.findUserStoreByName( "myLocalUserStore" ).getKey() );
        storeNewGroupCommand.setType( GroupType.USERSTORE_GROUP );
        storeNewGroupCommand.setName( "myGroup" );
        storeNewGroupCommand.setDescription( "Description" );

        GroupKey groupKey = userStoreService.storeNewGroup( storeNewGroupCommand );
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );

        // exercise
        UpdateGroupCommand updateGroupCommand = new UpdateGroupCommand( fixture.findUserByName( "admin" ).getKey(), groupKey );
        updateGroupCommand.setName( "myChange" );
        updateGroupCommand.setDescription( "Changed" );
        userStoreService.updateGroup( updateGroupCommand );

        // verify:
        assertEquals( 1, fixture.countGroupsByType( GroupType.USERSTORE_GROUP ) );
        GroupEntity updatedGroup = fixture.findGroupByKey( groupKey );
        assertEquals( false, updatedGroup.isDeleted() );
        assertEquals( "myChange", updatedGroup.getName() );
        assertEquals( "Changed", updatedGroup.getDescription() );
    }

    private void deleteUser( String name )
    {
        UserSpecification userToDelete = new UserSpecification();
        userToDelete.setName( name );
        DeleteUserCommand deleteUserCommand = new DeleteUserCommand( fixture.findUserByName( "admin" ).getKey(), userToDelete );
        userStoreService.deleteUser( deleteUserCommand );
    }

}
