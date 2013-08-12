/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.client;


import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.model.CreateUserParams;
import com.enonic.cms.api.client.model.DeleteUserParams;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_DeleteUserTest
    extends AbstractSpringTest
{
    @Autowired
    @Qualifier("localClient")
    private InternalClient internalClient;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private MemUserDatabase userDatabase;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture.initSystemData();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );
    }

    @Test
    public void delete_user_in_local_userstore_by_userstorename_and_username_deletes_the_user()
        throws Exception
    {
        // setup:
        clientLogin( "admin" );
        createUser( "testuserstore", "myuser", "mypassword", "myemail@test.com", "My User" );
        fixture.flushAndClearHibernateSession();

        // verify setup:
        assertEquals( false, fixture.findUserByName( "myuser" ).isDeleted() );

        DeleteUserParams params = new DeleteUserParams();
        params.user = "testuserstore:myuser";
        internalClient.deleteUser( params );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findUserByName( "myuser" ).isDeleted() );
    }

    @Test
    public void delete_user_in_local_userstore_by_userkey_deletes_the_user()
        throws Exception
    {
        // setup:
        clientLogin( "admin" );
        UserKey userKey = createUser( "testuserstore", "myuser", "mypassword", "myemail@test.com", "My User" );
        fixture.flushAndClearHibernateSession();

        // verify setup:
        assertEquals( false, fixture.findUserByName( "myuser" ).isDeleted() );

        DeleteUserParams params = new DeleteUserParams();
        params.user = "#" + userKey;
        internalClient.deleteUser( params );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findUserByName( "myuser" ).isDeleted() );
    }

    @Test
    public void delete_user_in_remote_userstore_by_userstorename_and_username_deletes_the_user()
        throws Exception
    {
        setupRemoteUserStore();

        // setup:
        clientLogin( "admin" );
        createUserWithFirstAndLastName( "myRemoteUserStore", "myuser", "mypassword", "myemail@test.com", "Firstname", "Lastname" );
        fixture.flushAndClearHibernateSession();

        // verify setup:
        assertEquals( false, fixture.findUserByName( "myuser" ).isDeleted() );

        DeleteUserParams params = new DeleteUserParams();
        params.user = "myRemoteUserStore:myuser";
        internalClient.deleteUser( params );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findUserByName( "myuser" ).isDeleted() );
    }

    @Test
    public void delete_user_in_remote_userstore_by_userkey_deletes_the_user()
        throws Exception
    {
        setupRemoteUserStore();

        // setup:
        clientLogin( "admin" );
        UserKey userKey =
            createUserWithFirstAndLastName( "myRemoteUserStore", "myuser", "mypassword", "myemail@test.com", "Firstname", "Lastname" );
        fixture.flushAndClearHibernateSession();

        // verify setup:
        assertEquals( false, fixture.findUserByName( "myuser" ).isDeleted() );

        DeleteUserParams params = new DeleteUserParams();
        params.user = "#" + userKey;
        internalClient.deleteUser( params );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findUserByName( "myuser" ).isDeleted() );
    }

    private UserKey createUser( String userstoreName, String username, String password, String email, String displayName )
    {
        CreateUserParams params = new CreateUserParams();
        params.userstore = userstoreName;
        params.username = username;
        params.password = password;
        params.email = email;
        params.displayName = displayName;
        return new UserKey( internalClient.createUser( params ) );
    }

    private UserKey createUserWithFirstAndLastName( String userstoreName, String username, String password, String email, String firstName,
                                                    String lastName )
    {
        CreateUserParams params = new CreateUserParams();
        params.userstore = userstoreName;
        params.username = username;
        params.password = password;
        params.email = email;
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( firstName );
        userInfo.setLastName( lastName );
        params.userInfo = userInfo;
        return new UserKey( internalClient.createUser( params ) );
    }

    private void clientLogin( String username )
    {
        UserEntity user = fixture.findUserByName( username );
        PortalSecurityHolder.setLoggedInUser( user.getKey() );
        PortalSecurityHolder.setImpersonatedUser( user.getKey() );
    }

    private void setupRemoteUserStore()
        throws Exception
    {
        this.userDatabase.clear();

        final ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        this.userStoreConnectorConfigLoader.setProperties( properties );

        final DomainFactory factory = this.fixture.getFactory();
        UserStoreEntity userStore = factory.createUserStore( "myRemoteUserStore", "myRemoteUserStore", true );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote, required" ) );
        userStore.setConfig( userStoreConfig );

        this.fixture.save( userStore );

        this.fixture.save(
            factory.createGroupInUserstore( GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS, "myRemoteUserStore" ) );
        this.fixture.save(
            factory.createGroupInUserstore( GroupType.USERSTORE_ADMINS.getName(), GroupType.USERSTORE_ADMINS, "myRemoteUserStore" ) );

        this.fixture.flushAndClearHibernateSession();
    }
}
