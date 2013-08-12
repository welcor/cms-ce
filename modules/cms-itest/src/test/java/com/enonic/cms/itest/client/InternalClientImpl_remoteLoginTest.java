/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.client;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_remoteLoginTest
    extends AbstractSpringTest
{
    @Autowired
    @Qualifier(value = "localClient")
    private InternalClient localClient;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private SynchronizeUserStoreJobFactory synchronizeUserStoreJobFactory;

    @Autowired
    private MemUserDatabase userDatabase;

    @Autowired
    private DomainFixture fixture;

    private final MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    @Before
    public void before()
        throws Exception
    {
        fixture.initSystemData();
        fixture.createAndStoreUserAndUserGroup( "testuser", "password", "Test user", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "avatar", "password", "Avatar", UserType.NORMAL, "testuserstore" );

        httpServletRequest.setRemoteAddr( "127.0.0.1" );

        setupRemoteUserStore();
    }

    @Test
    public void login_with_user_deleted_remotely_fails()
        throws Exception
    {
        addUser( "jvs" );
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();
        // setup: verify user jvs exists in db
        assertEquals( false, fixture.findUserByName( "jvs" ).isDeleted() );
        // setup: delete user
        removeUser( "jvs" );

        try
        {
            localClient.login( "myRemoteUserStore:jvs", "mypassword" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains( "Invalid username or password, username: 'jvs'" ) );
        }
    }

    @Test
    public void login_with_user_not_existing_in_cms_but_remotely_passes()
        throws Exception
    {
        // setup
        addUser( "jvs" );

        // verify user does not exists locally, it has be synchronized by login
        assertNull( fixture.findUserByName( "jvs" ) );

        // exercise
        localClient.login( "myRemoteUserStore:jvs", "password" );

        // verify
        assertEquals( "jvs", localClient.getUserName() );
        assertEquals( "jvs", localClient.getRunAsUserName() );
    }

    @Test
    public void login_with_no_userstore_specified_logs_in_user_in_default_userstore()
        throws Exception
    {
        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        // exercise
        localClient.login( "testuser", "password" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getRunAsUserName() );
    }

    @Test
    public void removePortalImpersonation_removes_currently_active_impersonation()
        throws Exception
    {
        // setup
        addUser( "jvs" );
        addUser( "arn" );

        // setup: login as jvs
        localClient.login( "myRemoteUserStore:jvs", "password" );
        // setup: impersonate arn
        localClient.impersonate( "myRemoteUserStore:arn" );

        // setup: verify current impersonation
        assertEquals( "arn", localClient.getRunAsUserName() );

        localClient.removeImpersonation();

        // verify
        assertEquals( "jvs", localClient.getRunAsUserName() );
    }

    private void setupRemoteUserStore()
    {
        this.userDatabase.clear();

        final ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "all" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        this.userStoreConnectorConfigLoader.setProperties( properties );

        final DomainFactory factory = this.fixture.getFactory();
        final UserStoreEntity userStore = factory.createUserStore( "myRemoteUserStore", "myRemoteUserStore", false );
        final UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStore.setConfig( userStoreConfig );

        this.fixture.save( userStore );

        this.fixture.save(
            factory.createGroupInUserstore( GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS, "myRemoteUserStore" ) );
        this.fixture.save(
            factory.createGroupInUserstore( GroupType.USERSTORE_ADMINS.getName(), GroupType.USERSTORE_ADMINS, "myRemoteUserStore" ) );

        this.fixture.flushAndClearHibernateSession();
    }

    private void addUser( final String id )
    {
        final RemoteUser user = new RemoteUser( id );
        user.setSync( id );
        this.userDatabase.addUser( user );
        this.userDatabase.setPassword( id, "password" );
    }

    private void removeUser( final String id )
    {
        this.userDatabase.removeUser( this.userDatabase.getUser( id ) );
    }
}
