/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.client;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.model.GetUserParams;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_getRemoteUserTest
    extends AbstractSpringTest
{
    @Autowired
    @Qualifier("localClient")
    private InternalClient internalClient;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private MemUserDatabase userDatabase;

    @Autowired
    private DomainFixture fixture;

    @Before
    public void before()
        throws Exception
    {
        this.fixture.initSystemData();
        this.fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void getUserExistsRemoteButNotLocally()
        throws Exception
    {
        GetUserParams params = new GetUserParams();
        params.user = "myRemoteUserStore:arn";

        // User should not exist in db
        assertNull( fixture.findUserByName( "arn" ) );

        setupRemoteUserStore();

        // User should be synced from LDAP and returned
        internalClient.impersonate( "myRemoteUserStore:arn" );
        Document userDocument = internalClient.getUser( params );
        Element userNameElement = userDocument.getRootElement().getChild( "display-name" );
        assertNotNull( userNameElement );

        // User should now exist in db as well.
        fixture.flushAndClearHibernateSession();
        assertNotNull( fixture.findUserByName( "arn" ) );

    }

    private void setupRemoteUserStore()
        throws Exception
    {
        this.userDatabase.clear();

        final ConfigProperties properties = new ConfigProperties();
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.userPolicy", "" );
        properties.setProperty( "cms.userstore.connector.myRemoteUserStore.plugin", "mem" );
        this.userStoreConnectorConfigLoader.setProperties( properties );

        final DomainFactory factory = this.fixture.getFactory();
        final UserStoreEntity userStore = factory.createUserStore( "myRemoteUserStore", "myRemoteUserStore", true );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote, required" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote, required" ) );
        userStore.setConfig( userStoreConfig );

        this.fixture.storeAndSetupUserStore( userStore );

        final RemoteUser user = new RemoteUser( "arn" );
        user.setSync( "arn" );
        user.getUserFields().setFirstName( "Arn" );
        user.getUserFields().setLastName( "Wyatt-Skriubakken" );
        this.userDatabase.addUser( user );
    }
}
