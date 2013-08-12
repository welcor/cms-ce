/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.services;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.esl.util.DateUtil;
import com.enonic.vertical.adminweb.UserHandlerServlet;

import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.plugin.ext.userstore.RemoteUser;
import com.enonic.cms.api.plugin.ext.userstore.UserFieldType;
import com.enonic.cms.api.plugin.ext.userstore.UserFields;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfig;
import com.enonic.cms.api.plugin.ext.userstore.UserStoreConfigField;
import com.enonic.cms.core.config.ConfigProperties;
import com.enonic.cms.core.security.AdminSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJob;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.userstore.MemUserDatabase;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.UserStoreDao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class UserServicesProcessor_remoteUserStoreTest
    extends AbstractSpringTest
{
    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private SynchronizeUserStoreJobFactory synchronizeUserStoreJobFactory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private MemUserDatabase userDatabase;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockHttpSession session = new MockHttpSession();

    private UserHandlerServlet userHandlerServlet;

    private ConfigProperties properties;

    private final static String DEFAULT_USERSTORE_NAME = "myRemoteUserStore";

    @Before
    public void setUp()
        throws Exception
    {
        request = createAndSetupMockHttpServletRequest();
        fixture.initSystemData();

        userHandlerServlet = new UserHandlerServlet();
        userHandlerServlet.setUserStoreDao( userStoreDao );
        userHandlerServlet.setSecurityService( securityService );
        userHandlerServlet.setUserStoreService( userStoreService );
        userHandlerServlet.setMemberOfResolver( memberOfResolver );

        // setup vertical properties
        properties = new ConfigProperties();
        setPropertiesForRemoteUserStoreConnector( DEFAULT_USERSTORE_NAME, properties );
        userStoreConnectorConfigLoader.setProperties( properties );

        this.userDatabase.clear();
    }

    @Test
    public void update_given_fields_that_are_local_in_remote_userStore_with_some_value()
        throws Exception
    {
        // setup: userstore
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LOCALE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.GENDER, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HTML_EMAIL, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        createRemoteUserStore( DEFAULT_USERSTORE_NAME, true, userStoreConfig );
        fixture.flushAndClearHibernateSession();

        // setup: create user
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        userFields.setOrganization( "Java Mafia" );
        userFields.setHomePage( "http://www.homepage.com" );
        userFields.setLocale( Locale.FRENCH );
        userFields.setGender( Gender.FEMALE );
        userFields.setHtmlEmail( true );
        createNormalUser( "testuser", DEFAULT_USERSTORE_NAME, userFields );

        // setup: verify user
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNotNull( userFields.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( userFields.getBirthday() ) );
        assertEquals( "INI", userFields.getInitials() );
        assertEquals( new DateMidnight( 2012, 12, 12 ).toDate(), userFields.getBirthday() );
        assertEquals( "http://www.homepage.com", userFields.getHomePage() );
        assertEquals( "Java Mafia", userFields.getOrganization() );
        assertEquals( Locale.FRENCH, userFields.getLocale() );
        assertEquals( Gender.FEMALE, userFields.getGender() );
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );

        loginAdminUser( "testuser" );

        // exercise: update user
        applyParametersForUpdate( request );
        applyParametersForUser( fixture.findUserByName( "testuser" ), request );
        request.setParameter( "email", "testuser@example.com" );
        request.setParameter( "first_name", "First name changed" );
        request.setParameter( "last_name", "Last name changed" );
        request.setParameter( "initials", "Initials changed" );
        request.setParameter( "organization", "Organization" );
        request.setParameter( "home_page", "http://www.otherpage.com" );
        request.setParameter( "gender", "female" );
        request.setParameter( "html_email", "on" ); // small trick: sending on instead of true, because true is converted to a Boolean
        request.setParameter( "locale", "es" );
        request.setParameter( "birthday", "14.05.2012" );
        userHandlerServlet.doGet( request, response );

        // verify
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "Initials changed", userFields.getInitials() );
        assertEquals( "Last name changed", userFields.getLastName() );
        assertEquals( "First name changed", userFields.getFirstName() );
        assertEquals( "http://www.otherpage.com", userFields.getHomePage() );
        assertEquals( new DateMidnight( 2012, 5, 14 ).toDate(), userFields.getBirthday() );
        assertEquals( "Organization", userFields.getOrganization() );
        assertEquals( new Locale( "es" ), userFields.getLocale() );
        assertEquals( Gender.FEMALE, userFields.getGender() );
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );
    }

    @Test
    public void update_given_fields_with_some_value()
        throws Exception
    {
        // setup: userstore
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LOCALE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.GENDER, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HTML_EMAIL, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        createRemoteUserStore( DEFAULT_USERSTORE_NAME, true, userStoreConfig );
        fixture.flushAndClearHibernateSession();

        // setup: create user
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        userFields.setOrganization( "Java Mafia" );
        userFields.setHomePage( "http://www.homepage.com" );
        userFields.setLocale( Locale.FRENCH );
        userFields.setGender( Gender.FEMALE );
        userFields.setHtmlEmail( true );
        createNormalUser( "testuser", DEFAULT_USERSTORE_NAME, userFields );

        // setup: verify user
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNotNull( userFields.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( userFields.getBirthday() ) );
        assertEquals( "INI", userFields.getInitials() );
        assertEquals( new DateMidnight( 2012, 12, 12 ).toDate(), userFields.getBirthday() );
        assertEquals( "http://www.homepage.com", userFields.getHomePage() );
        assertEquals( "Java Mafia", userFields.getOrganization() );
        assertEquals( Locale.FRENCH, userFields.getLocale() );
        assertEquals( Gender.FEMALE, userFields.getGender() );
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );

        loginAdminUser( "testuser" );

        // exercise: update user
        applyParametersForUpdate( request );
        applyParametersForUser( fixture.findUserByName( "testuser" ), request );
        request.setParameter( "email", "testuser@example.com" );
        request.setParameter( "first_name", "First name changed" );
        request.setParameter( "last_name", "Last name changed" );
        request.setParameter( "initials", "Initials changed" );
        request.setParameter( "organization", "Organization" );
        request.setParameter( "home_page", "http://www.otherpage.com" );
        request.setParameter( "gender", "female" );
        request.setParameter( "html_email", "on" ); // small trick: sending on instead of true, because true is converted to a Boolean
        request.setParameter( "locale", "es" );
        request.setParameter( "birthday", "14.05.2012" );
        userHandlerServlet.doGet( request, response );

        // verify
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "Initials changed", userFields.getInitials() );
        assertEquals( "Last name changed", userFields.getLastName() );
        assertEquals( "First name changed", userFields.getFirstName() );
        assertEquals( "http://www.otherpage.com", userFields.getHomePage() );
        assertEquals( new DateMidnight( 2012, 5, 14 ).toDate(), userFields.getBirthday() );
        assertEquals( "Organization", userFields.getOrganization() );
        assertEquals( new Locale( "es" ), userFields.getLocale() );
        assertEquals( Gender.FEMALE, userFields.getGender() );
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );
    }


    @Test
    public void update_given_html_email_field_with_false_value()
        throws Exception
    {
        // setup: userstore
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HTML_EMAIL, "" ) );
        createRemoteUserStore( DEFAULT_USERSTORE_NAME, true, userStoreConfig );
        fixture.flushAndClearHibernateSession();

        // setup: create user
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setHtmlEmail( true );
        createNormalUser( "testuser", DEFAULT_USERSTORE_NAME, userFields );

        // setup: verify user
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );

        loginAdminUser( "testuser" );

        // exercise: update user without sending html_email
        applyParametersForUpdate( request );
        applyParametersForUser( fixture.findUserByName( "testuser" ), request );
        request.setParameter( "email", "testuser@example.com" );
        request.setParameter( "first_name", "First name changed" );
        request.setParameter( "last_name", "Last name changed" );
        userHandlerServlet.doGet( request, response );

        // verify
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "Last name changed", userFields.getLastName() );
        assertEquals( "First name changed", userFields.getFirstName() );
        assertEquals( Boolean.FALSE, userFields.getHtmlEmail() );
    }


    @Test
    public void update_given_user_field_with_empty_value()
        throws Exception
    {
        // setup: userstore
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HOME_PAGE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.ORGANIZATION, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LOCALE, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.GENDER, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.HTML_EMAIL, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "" ) );
        createRemoteUserStore( DEFAULT_USERSTORE_NAME, true, userStoreConfig );
        fixture.flushAndClearHibernateSession();

        // setup: create user
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setInitials( "INI" );
        userFields.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        userFields.setOrganization( "Java Mafia" );
        userFields.setHomePage( "http://www.homepage.com" );
        userFields.setLocale( Locale.FRENCH );
        userFields.setGender( Gender.FEMALE );
        userFields.setHtmlEmail( true );
        createNormalUser( "testuser", DEFAULT_USERSTORE_NAME, userFields );

        // setup: verify user
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertNotNull( userFields.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( userFields.getBirthday() ) );
        assertEquals( "INI", userFields.getInitials() );
        assertEquals( new DateMidnight( 2012, 12, 12 ).toDate(), userFields.getBirthday() );
        assertEquals( "http://www.homepage.com", userFields.getHomePage() );
        assertEquals( "Java Mafia", userFields.getOrganization() );
        assertEquals( Locale.FRENCH, userFields.getLocale() );
        assertEquals( Gender.FEMALE, userFields.getGender() );
        assertEquals( Boolean.TRUE, userFields.getHtmlEmail() );

        loginAdminUser( "testuser" );

        // exercise: update user
        applyParametersForUpdate( request );
        applyParametersForUser( fixture.findUserByName( "testuser" ), request );
        request.setParameter( "email", "testuser@example.com" );
        request.setParameter( "first_name", "First name changed" );
        request.setParameter( "last_name", "Last name changed" );
        request.setParameter( "initials", "" );
        request.setParameter( "organization", "" );
        request.setParameter( "home_page", "" );
        request.setParameter( "gender", "" );
        request.setParameter( "html_email", "" );
        request.setParameter( "locale", "" );
        request.setParameter( "birthday", "" );
        userHandlerServlet.doGet( request, response );

        // verify
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "Last name changed", userFields.getLastName() );
        assertEquals( "First name changed", userFields.getFirstName() );
        assertEquals( "", userFields.getInitials() );
        assertEquals( "", userFields.getHomePage() );
        assertEquals( "", userFields.getOrganization() );
        assertEquals( null, userFields.getBirthday() );
        assertEquals( null, userFields.getLocale() );
        assertEquals( null, userFields.getGender() );
        assertEquals( Boolean.FALSE, userFields.getHtmlEmail() );
    }

    @Test
    public void update_given_changed_userField_which_is_local_and_remote_fields_have_same_value_when_userPolicy_is_not_update_policy_then_field_should_be_updated()
        throws Exception
    {
        // Creating test user in remote userstore
        final RemoteUser user = new RemoteUser( "testuser" );
        user.setSync( "testuser" );
        user.setEmail( "testuser@example.com" );
        user.getUserFields().setFirstName( "First name" );
        user.getUserFields().setLastName( "Last name" );
        user.getUserFields().setDescription( null );
        this.userDatabase.addUser( user );

        setPropertyForRemoteUserStoreConnector( DEFAULT_USERSTORE_NAME, "userPolicy", "" );
        setPropertyForRemoteUserStoreConnector( DEFAULT_USERSTORE_NAME, "groupPolicy", "read" );

        // setup: userstore
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "remote" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.TITLE, "local" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.DESCRIPTION, "remote" ) );
        createRemoteUserStore( DEFAULT_USERSTORE_NAME, true, userStoreConfig );
        fixture.flushAndClearHibernateSession();

        // setup: synch
        synchronizeUserStore( DEFAULT_USERSTORE_NAME, SynchronizeUserStoreType.USERS_ONLY );

        // setup: verify user
        UserFields userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "First name", userFields.getFirstName() );
        assertEquals( "Last name", userFields.getLastName() );
        assertEquals( null, userFields.getTitle() );
        assertEquals( null, userFields.getDescription() );

        loginAdminUser( "testuser" );

        // exercise: update user having only title changing value
        applyParametersForUpdate( request );
        applyParametersForUser( fixture.findUserByName( "testuser" ), request );
        request.setParameter( "email", "testuser@example.com" );
        request.setParameter( "first_name", "First name" );
        request.setParameter( "last_name", "Last name" );
        request.setParameter( "title", "Title changed" );
        request.setParameter( "description", "" );
        userHandlerServlet.doGet( request, response );

        // verify: title was changed
        userFields = fixture.findUserByName( "testuser" ).getUserFields();
        assertEquals( "Last name", userFields.getLastName() );
        assertEquals( "First name", userFields.getFirstName() );
        assertEquals( "Title changed", userFields.getTitle() );
        assertEquals( null, userFields.getDescription() );
    }

    private UserStoreConfigField createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreConfigField fieldConfig = new UserStoreConfigField( type );
        fieldConfig.setRemote( properties.contains( "remote" ) );
        fieldConfig.setReadOnly( properties.contains( "read-only" ) );
        fieldConfig.setRequired( properties.contains( "required" ) );
        fieldConfig.setIso( properties.contains( "iso" ) );
        return fieldConfig;
    }

    private void loginAdminUser( String userName )
    {
        AdminSecurityHolder.setUser( fixture.findUserByName( userName ).getKey() );
    }

    private MockHttpServletRequest createAndSetupMockHttpServletRequest()
        throws UnsupportedEncodingException
    {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod( "POST" );
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( request ) );
        request.setSession( session );
        return request;
    }

    private UserStoreKey createRemoteUserStore( String name, boolean defaultStore, UserStoreConfig config )
    {
        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setName( name );
        command.setDefaultStore( defaultStore );
        command.setConfig( config );
        command.setConnectorName( name );
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

    private void applyParametersForUpdate( MockHttpServletRequest request )
    {
        request.setParameter( "op", "update" );
        request.setParameter( "page", "700" );
    }

    private void applyParametersForUser( UserEntity user, MockHttpServletRequest request )
    {
        request.setParameter( "userstorekey", user.getUserStore().getKey().toString() );
        request.setParameter( "uid_dummy", user.getName() );
    }

    private void setPropertiesForRemoteUserStoreConnector( String connectorName, Properties properties )
    {
        properties.setProperty( "cms.userstore.connector." + connectorName + ".userPolicy", "all" );
        properties.setProperty( "cms.userstore.connector." + connectorName + ".groupPolicy", "all" );
        properties.setProperty( "cms.userstore.connector." + connectorName + ".plugin", "mem" );
    }

    private void setPropertyForRemoteUserStoreConnector( String connectorName, String name, String value )
    {
        properties.setProperty( "cms.userstore.connector." + connectorName + "." + name, value );
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
}
