/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.cms.api.plugin.ext.userstore.RemoteGroup;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.AdminSecurityHolder;
import com.enonic.cms.core.security.ImpersonateCommand;
import com.enonic.cms.core.security.LoginAdminUserCommand;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.AddMembershipsCommand;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJob;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class SecurityServiceImplTest
    extends AbstractSpringTest
{
    @Autowired
    private SynchronizeUserStoreJobFactory synchronizeUserStoreJobFactory;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MemUserDatabase userDatabase;

    @Autowired
    private DomainFixture fixture;

    @Before
    public void before()
        throws Exception
    {
        this.fixture.initSystemData();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes( httpServletRequest );
        RequestContextHolder.setRequestAttributes( servletRequestAttributes );

        httpServletRequest.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( httpServletRequest );

        setupRemoteUserStore();
    }

    @Test
    public void impersonate_impersonating_admin_user_throws_exception()
        throws Exception
    {
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        ImpersonateCommand command = new ImpersonateCommand( false, fixture.findUserByName( User.ROOT_UID ).getKey() );

        try
        {
            securityService.impersonatePortalUser( command );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            System.out.println( e.getMessage() );
        }
    }

    @Test
    public void getLoggedInPortalUser_returns_logged_in_portal_user()
        throws Exception
    {
        addUser( "arn" );

        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "arn" ), "mypassword" );
        assertEquals( "arn", securityService.getLoggedInPortalUserAsEntity().getName() );
    }

    @Test
    public void getLoggedInPortalUser_returns_anonymous_user_when_no_user_have_logged_in()
        throws Exception
    {
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        assertEquals( "anonymous", securityService.getLoggedInPortalUserAsEntity().getName() );
    }

    @Test
    public void impersonatePortalUser_with_required_access_check_throws_exception_when_current_logged_in_user_is_not_admin()
        throws Exception
    {
        addUser( "arn" );
        addUser( "jvs" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "arn" ), "mypassword" );

        // exercise
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( true, fixture.findUserByName( "jvs" ).getKey() );

        try
        {
            securityService.impersonatePortalUser( impersonateCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Impersonate not allowed", e.getMessage() );
        }
    }

    @Test
    public void impersonatePortalUser_throws_exception_when_trying_impersonate_anonymous()
        throws Exception
    {
        addUser( "arn" );

        // setup
        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "arn" ), "mypassword" );
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        // exercise
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( false, fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );

        try
        {
            securityService.impersonatePortalUser( impersonateCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Not allowed to impersonate anonymous user, use method removePortalImpersonation instead", e.getMessage() );
        }
    }

    @Test
    public void impersonatePortalUser_throws_exception_when_trying_impersonate_admin()
        throws Exception
    {
        addUser( "arn" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "arn" ), "mypassword" );

        // exercise
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( false, fixture.findUserByName( User.ROOT_UID ).getKey() );

        try
        {
            securityService.impersonatePortalUser( impersonateCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Not allowed to impersonate the admin user", e.getMessage() );
        }
    }

    @Test
    public void impersonatePortalUser_throws_exception_when_trying_impersonate_none_existing_user()
        throws Exception
    {
        addUser( "arn" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();

        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "arn" ), "mypassword" );

        // exercise
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( false, new UserKey( "NONEXISTING" ) );

        try
        {
            securityService.impersonatePortalUser( impersonateCommand );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
            assertEquals( "User not found, key: 'NONEXISTING'", e.getMessage() );
        }
    }

    @Test
    public void impersonatePortalUser_with_required_access_check_passes_when_current_logged_in_user_is_admin()
        throws Exception
    {
        addUser( "jvs" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();
        securityService.loginPortalUser( new QualifiedUsername( "admin" ), "password" );

        // exercise
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( true, fixture.findUserByName( "jvs" ).getKey() );
        securityService.impersonatePortalUser( impersonateCommand );

        assertEquals( "jvs", securityService.getImpersonatedPortalUser().getName() );
    }

    @Test
    public void getImpersonatedPortalUser_returns_impersonated_user()
        throws Exception
    {
        addUser( "arn" );
        addUser( "jvs" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();
        // setup: login as jvs
        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "jvs" ), "mypassword" );
        // setup: impersonate arn
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( false, fixture.findUserByName( "arn" ).getKey() );
        securityService.impersonatePortalUser( impersonateCommand );

        // exercise and verify
        assertEquals( "arn", securityService.getImpersonatedPortalUser().getName() );
    }

    @Test
    public void removePortalImpersonation_removes_currently_active_impersonation()
        throws Exception
    {
        addUser( "jvs" );
        addUser( "arn" );

        // setup
        synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( fixture.findUserStoreByName( "myRemoteUserStore" ).getKey(),
                                                                      SynchronizeUserStoreType.USERS_ONLY, 10 ).start();
        // setup: login as jvs
        securityService.loginPortalUser( new QualifiedUsername( "myRemoteUserStore", "jvs" ), "mypassword" );
        // setup: impersonate arn
        ImpersonateCommand impersonateCommand = new ImpersonateCommand( false, fixture.findUserByName( "arn" ).getKey() );
        securityService.impersonatePortalUser( impersonateCommand );

        // setup: verify current impersonation
        assertEquals( "arn", securityService.getImpersonatedPortalUser().getName() );

        securityService.removePortalImpersonation();

        // verify
        assertEquals( "jvs", securityService.getImpersonatedPortalUser().getName() );
    }

    @Test
    public void loginAdminUser_locally_existing_user_is_deleted_and_new_created_when_user_have_been_renamed_remotely()
        throws Exception
    {
        final RemoteUser arnUser = addUser( "arn" );
        final RemoteGroup editorsGroup = addGroup( "editors" );
        this.userDatabase.addMember( editorsGroup, arnUser );

        // verify: arn must not exist in db
        assertNull( fixture.findUserByName( "arn" ) );

        // setup
        UserStoreKey userStoreKey = fixture.findUserStoreByName( "myRemoteUserStore" ).getKey();
        // setup: synchronize with remote
        SynchronizeUserStoreJob job =
            synchronizeUserStoreJobFactory.createSynchronizeUserStoreJob( userStoreKey, SynchronizeUserStoreType.USERS_AND_GROUPS, 100 );
        job.start();

        // after synchronization we have:
        // local (arn)  - remote (arn)

        // setup: make the remote group editors a member of the built-in contributors group (to enable admin)
        createMembershipToGroupOfType( "editors", GroupType.CONTRIBUTORS );

        // setup: verify login with arn works
        securityService.loginAdminUser( new LoginAdminUserCommand( new QualifiedUsername( userStoreKey, "arn" ), "mypassword" ) );
        assertEquals( fixture.findUserByName( "arn" ).getKey(), AdminSecurityHolder.getUser() );
        securityService.logoutAdminUser();
        assertNull( AdminSecurityHolder.getUser() );

        // setup: rename remote user arn to archer
        this.userDatabase.removeMember( editorsGroup, arnUser );
        this.userDatabase.removeUser( arnUser );
        final RemoteUser archerUser = addUser( "archer" );
        this.userDatabase.addMember( editorsGroup, archerUser );

        // after renaming remote user we have:
        // local (arn)  - remote (n/a)
        // local (n/a)  - remote (archer)
        assertNull( this.userDatabase.getUser( "arn" ) );
        assertNotNull( fixture.findUserByName( "arn" ) );
        assertNotNull( this.userDatabase.getUser( "archer" ) );
        assertNull( fixture.findUserByName( "archer" ) );

        // exercise: provoke synchronization of user archer.
        securityService.loginAdminUser( new LoginAdminUserCommand( new QualifiedUsername( userStoreKey, "archer" ), "mypassword" ) );

        // verify: archer is created with same email
        assertNotNull( fixture.findUserByName( "archer" ) );
        assertEquals( "archer@test.com", fixture.findUserByName( "archer" ).getEmail() );

        // verify: user archer can login to admin
        securityService.loginAdminUser( new LoginAdminUserCommand( new QualifiedUsername( userStoreKey, "archer" ), "mypassword" ) );
        assertEquals( fixture.findUserByName( "archer" ).getKey(), AdminSecurityHolder.getUser() );
        securityService.logoutAdminUser();
        assertNull( AdminSecurityHolder.getUser() );
    }


    private void setupRemoteUserStore()
        throws Exception
    {
        this.userDatabase.clear();

        // setup vertical properties
        final ConfigProperties propsForVP = new ConfigProperties();
        propsForVP.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "all" );
        propsForVP.setProperty( "cms.userstore.connector.myRemoteUserStore.groupPolicy", "all" );
        propsForVP.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        userStoreConnectorConfigLoader.setProperties( propsForVP );

        final DomainFactory factory = this.fixture.getFactory();
        final UserStoreEntity userStore = factory.createUserStore( "myRemoteUserStore", "myRemoteUserStore", true );
        final UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStore.setConfig( userStoreConfig );

        fixture.save( userStore );

        fixture.save(
            factory.createGroupInUserstore( GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS, "myRemoteUserStore" ) );
        fixture.save(
            factory.createGroupInUserstore( GroupType.USERSTORE_ADMINS.getName(), GroupType.USERSTORE_ADMINS, "myRemoteUserStore" ) );

        fixture.flushAndClearHibernateSession();
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
