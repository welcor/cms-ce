package com.enonic.vertical.adminweb;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletException;

import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.esl.util.DateUtil;

import com.enonic.cms.api.client.model.user.Gender;
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
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.UserStoreDao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class UserHandlerServlet_localUserStoreTest
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
    private DomainFixture fixture;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockHttpSession session = new MockHttpSession();

    private UserHandlerServlet userHandlerServlet;

    @Before
    public void setUp()
        throws ServletException, UnsupportedEncodingException
    {
        request = createAndSetupMockHttpServletRequest();

        fixture.initSystemData();

        userHandlerServlet = new UserHandlerServlet();
        userHandlerServlet.setUserStoreDao( userStoreDao );
        userHandlerServlet.setSecurityService( securityService );
        userHandlerServlet.setUserStoreService( userStoreService );
        userHandlerServlet.setMemberOfResolver( memberOfResolver );

    }

    @After
    public void after()
    {
        securityService.logoutPortalUser();
    }

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
        createLocalUserStore( "myLocalUserStore", true, userStoreConfig );
        fixture.flushAndClearHibernateSesssion();

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
        createNormalUser( "testuser", "myLocalUserStore", userFields );

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
        createLocalUserStore( "myLocalUserStore", true, userStoreConfig );
        fixture.flushAndClearHibernateSesssion();

        // setup: create user
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.setLastName( "Last name" );
        userFields.setHtmlEmail( true );
        createNormalUser( "testuser", "myLocalUserStore", userFields );

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
        createLocalUserStore( "myLocalUserStore", true, userStoreConfig );
        fixture.flushAndClearHibernateSesssion();

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
        createNormalUser( "testuser", "myLocalUserStore", userFields );

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


    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreUserFieldConfig fieldConfig = new UserStoreUserFieldConfig( type );
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

    private UserStoreKey createLocalUserStore( String name, boolean defaultStore, UserStoreConfig config )
    {
        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setName( name );
        command.setDefaultStore( defaultStore );
        command.setConfig( config );
        return userStoreService.storeNewUserStore( command );
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
}
