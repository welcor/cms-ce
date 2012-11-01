/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;
import java.text.ParseException;

import org.jdom.JDOMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.esl.util.DateUtil;

import com.enonic.cms.api.client.model.UpdateUserParams;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class InternalClientImpl_UpdateUserTest
    extends AbstractSpringTest
{

    @Autowired
    @Qualifier("localClient")
    private InternalClient internalClient;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private DomainFixture fixture;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture.initSystemData();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( "anonymous" ).getKey() );
    }

    @After
    public void after()
    {
        securityService.logoutPortalUser();
    }

    @Test
    // UpdateUserParams.userInfo.phone = null -> row for phone field must be removed if existing
    public void update_phone_to_null()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( null );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.UPDATE;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNull( resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.phone = <blank> -> row for phone field must be updated or created with blank
    public void update_phone_to_empty()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( "" );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.UPDATE;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "", resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.phone = 1234 -> row for phone field must be updated or created with 1234
    public void update_phone_to_valued()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( "1234" );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.UPDATE;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "1234", resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.birthday = null -> birthday field for user should end up being removed from db (row in tUserfield)
    public void update_birthday_to_null()
        throws ParseException
    {
        prepareUserStoreConfig( UserFieldType.BIRTHDAY );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "12.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "INI", resultUserFields.getInitials() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setBirthday( null );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.UPDATE;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNull( resultUserFields.getBirthday() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.birthday = <date> -> birthday field in table tUserfield should end up being created if missing or updated if already existing
    public void update_birthday_to_valued()
        throws ParseException
    {
        prepareUserStoreConfig( UserFieldType.BIRTHDAY );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNotNull( resultUserFields.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "INI", resultUserFields.getInitials() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setBirthday( DateUtil.parseDate( "22.12.2012" ) );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.UPDATE;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "22.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.phone = null -> phone field must not be changed
    public void modify_phone_to_null()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( null );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.MODIFY;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "2771188", resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.phone = <blank> -> row for phone field must be updated or created with blank
    public void modify_phone_to_empty()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( "" );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.MODIFY;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "", resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.phone = 1234 -> row for phone field must be updated or created with 1234
    public void modify_phone_to_valued()
    {
        prepareUserStoreConfig( UserFieldType.PHONE );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setPhone( "2771188" );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "INI", resultUserFields.getInitials() );
        assertEquals( "2771188", resultUserFields.getPhone() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setPhone( "1234" );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.MODIFY;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "1234", resultUserFields.getPhone() );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.birthday = null -> birthday field must not be changed
    public void modify_birthday_to_null()
        throws ParseException
    {
        prepareUserStoreConfig( UserFieldType.BIRTHDAY );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "12.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "INI", resultUserFields.getInitials() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setBirthday( null );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.MODIFY;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "12.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    @Test
    // UpdateUserParams.userInfo.birthday = <date> -> birthday field in table tUserfield should end up being created if missing or updated if already existing
    public void modify_birthday_to_valued()
        throws ParseException
    {
        prepareUserStoreConfig( UserFieldType.BIRTHDAY );

        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userFields );

        // verify
        UserFields resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNotNull( resultUserFields.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "INI", resultUserFields.getInitials() );

        loginPortalUser( "testuser" );

        // exercise
        UpdateUserParams params = new UpdateUserParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.displayName = "dName";
        params.email = "email@company.com";

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setFirstName( "First name changed" );
        updUserInfo.setLastName( "Last name changed" );
        updUserInfo.setInitials( "Initials changed" );
        updUserInfo.setBirthday( DateUtil.parseDate( "22.12.2012" ) );
        params.userInfo = updUserInfo;

        params.updateStrategy = UpdateUserParams.UpdateStrategy.MODIFY;

        internalClient.updateUser( params );

        // verify
        resultUserFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "22.12.2012", DateUtil.formatDate( resultUserFields.getBirthday() ) );
        assertEquals( "Initials changed", resultUserFields.getInitials() );
    }

    private void prepareUserStoreConfig( UserFieldType type )
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( type, "" ) );

        createLocalUserStore( "myLocalStore", true, userStoreConfig );
        fixture.flushAndClearHibernateSession();
    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreUserFieldConfig fieldConfig = new UserStoreUserFieldConfig( type );
        fieldConfig.setRemote( properties.contains( "remote" ) );
        fieldConfig.setReadOnly( properties.contains( "read-only" ) );
        fieldConfig.setRequired( properties.contains( "required" ) );
        fieldConfig.setIso( properties.contains( "iso" ) );
        return fieldConfig;
    }

    private void loginPortalUser( String userName )
    {
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( userName ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( userName ).getKey() );
    }

    private UserStoreKey createLocalUserStore( String name, boolean defaultStore, UserStoreConfig config )
    {
        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setName( name );
        command.setDefaultStore( defaultStore );
        command.setConfig( config );
        return userStoreService.storeNewUserStore( command );
    }

    private UserKey createNormalUser( String userName, String userStoreName, UserFields userFields )
    {
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setUsername( userName );
        command.setUserStoreKey( fixture.findUserStoreByName( userStoreName ).getKey() );
        command.setAllowAnyUserAccess( true );
        command.setEmail( userName + "@example.com" );
        command.setPassword( "password" );
        command.setType( UserType.NORMAL );
        command.setDisplayName( userName );
        command.setUserFields( userFields );

        return userStoreService.storeNewUser( command );
    }
}
