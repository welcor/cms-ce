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
import com.enonic.cms.api.client.model.JoinGroupsParams;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_CreateUserTest
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

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void create_user_in_local_userstore()
        throws Exception
    {
        clientLogin( "admin" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "testuserstore";
        params.username = "test1";
        params.password = "password";
        params.email = "jvs@enonic.com";
        params.userInfo.setFirstName( "Jorund Vier" );
        params.userInfo.setLastName( "Skriubakken" );
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
        UserEntity actualUser = fixture.findUserByName( "test1" );
        assertEquals( "jvs@enonic.com", actualUser.getEmail() );
        assertEquals( "test1", actualUser.getName() );
        assertEquals( "Jorund Vier", actualUser.getUserFields().getFirstName() );
        assertEquals( "Skriubakken", actualUser.getUserFields().getLastName() );
    }

    @Test
    public void create_user_in_local_userstore_with_name_equals_email()
        throws Exception
    {
        clientLogin( "admin" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "testuserstore";
        params.username = "jvs@enonic.com";
        params.password = "password";
        params.email = "jvs@enonic.com";
        params.userInfo.setFirstName( "Jorund Vier" );
        params.userInfo.setLastName( "Skriubakken" );
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
        UserEntity actualUser = fixture.findUserByName( "jvs@enonic.com" );
        assertEquals( "jvs@enonic.com", actualUser.getEmail() );
        assertEquals( "jvs@enonic.com", actualUser.getName() );
    }

    @Test
    public void create_user_as_root()
        throws Exception
    {
        clientLogin( "admin" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "testuserstore";
        params.username = "jvs";
        params.password = "password";
        params.email = "jvs@enonic.com";
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "Jorund" );
        userInfo.setLastName( "Skriubakken" );
        params.userInfo = userInfo;
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
    }

    @Test
    public void create_user_as_an_enterprise_admin()
        throws Exception
    {
        // setup: create a user and make it member of the enterprise admins group
        clientLogin( "admin" );
        fixture.createAndStoreUserAndUserGroup( "testea", "password", "Test ea", UserType.NORMAL, "testuserstore" );
        JoinGroupsParams joinGroupsParams = new JoinGroupsParams();
        joinGroupsParams.user = "#" + fixture.findUserByName( "testea" ).getKey().toString();
        joinGroupsParams.groupsToJoin = new String[]{"#" + fixture.findGroupByType( GroupType.ENTERPRISE_ADMINS ).getGroupKey().toString()};
        internalClient.joinGroups( joinGroupsParams );
        fixture.flushAndClearHibernateSession();

        // setup: login
        clientLogin( "testea" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "testuserstore";
        params.username = "jvs";
        params.password = "password";
        params.email = "jvs@enonic.com";
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "Jorund" );
        userInfo.setLastName( "Skriubakken" );
        params.userInfo = userInfo;
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
    }

    @Test
    public void create_user_in_remote_userstore()
        throws Exception
    {
        setupRemoteUserStore();

        clientLogin( "admin" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "myRemoteUserStore";
        params.username = "test2";
        params.password = "password";
        params.email = "jvs@enonic.com";
        params.userInfo.setFirstName( "Jorund Vier" );
        params.userInfo.setLastName( "Skriubakken" );
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
        UserEntity actualUser = fixture.findUserByName( "test2" );
        assertEquals( "jvs@enonic.com", actualUser.getEmail() );
        assertEquals( "test2", actualUser.getName() );
        assertEquals( "Jorund Vier", actualUser.getUserFields().getFirstName() );
        assertEquals( "Skriubakken", actualUser.getUserFields().getLastName() );
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
