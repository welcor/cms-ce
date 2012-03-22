/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.model.ChangeUserPasswordParams;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static junit.framework.Assert.assertEquals;

public class InternalClientImpl_changeUserPasswordTest
        extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private InternalClient internalClient;

    @Before
    public void before()
            throws IOException, JDOMException
    {
        factory = fixture.getFactory();

        fixture.initSystemData();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( "anonymous" ).getKey() );
    }

    @Test
    public void change_password()
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );

        createLocalUserStore( "myLocalStore", true, userStoreConfig );
        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // verify
        UserEntity resultUser = fixture.findUserByName( "testuser" );
        assertEquals( "INI", resultUser.getUserInfo().getInitials() );
        assertEquals( DigestUtils.shaHex( "password" ), resultUser.getPassword() );

        loginPortalUser( "testuser" );

        // exercise
        ChangeUserPasswordParams params = new ChangeUserPasswordParams();
        params.userstore = "myLocalStore";
        params.username = "testuser";
        params.password = "changed";

        internalClient.changeUserPassword( params );

        // verify
        resultUser = fixture.findUserByName( "testuser" );
        assertEquals( DigestUtils.shaHex( "changed" ), resultUser.getPassword() );
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

    private UserKey createNormalUser( String userName, String userStoreName, UserInfo userInfo )
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
        command.setUserInfo( userInfo );

        return userStoreService.storeNewUser( command );
    }
}
