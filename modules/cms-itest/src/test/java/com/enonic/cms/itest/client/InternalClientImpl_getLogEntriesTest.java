package com.enonic.cms.itest.client;

import java.util.Date;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.model.GetLogEntriesParams;
import com.enonic.cms.api.client.model.log.LogEntries;
import com.enonic.cms.api.client.model.log.LogEntry;
import com.enonic.cms.api.client.model.log.LogEventType;
import com.enonic.cms.core.client.InternalClientImpl;
import com.enonic.cms.core.client.InternalLocalClient;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.log.LogEntryKey;
import com.enonic.cms.core.log.LogEntryResultSet;
import com.enonic.cms.core.log.LogEntrySpecification;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.time.SystemTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class InternalClientImpl_getLogEntriesTest
    extends AbstractSpringTest
{
    @Autowired
    private DomainFixture fixture;

    @Autowired
    private ContentService contentService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LogService logService;

    private InternalClientImpl internalClient;

    private SiteEntity site;

    @Before
    public void setUp()
    {
        // setup needed common data for each test
        fixture.initSystemData();

        final DomainFactory factory = fixture.getFactory();
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        final MockHttpServletRequest httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        fixture.save( factory.createUnit( "MyUnit", "en" ) );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-updater", "Updater", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type: Person
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.addRelatedContentInput( "my-relatedcontent", "contentdata/my-relatedcontent", "My relatedcontent", false, false );
        ctyconf.addRelatedContentInput( "my-relatedcontents", "contentdata/my-relatedcontents", "My relatedcontents", false, true );
        ctyconf.endBlock();
        final Document personConfigAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.save(
            factory.createContentType( "MyPersonType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personConfigAsXmlBytes ) );

        fixture.save(
            factory.createCategory( "MyPersonCategory", null, "MyPersonType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-updater", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-querier", "read, admin_browse" ) );

        site = factory.createSite( "MySite", new Date(), null, "en" );
        fixture.save( site );

        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "content-querier" ).getKey() );

        internalClient = new InternalLocalClient();
        internalClient.setSecurityService( securityService );
        internalClient.setContentService( contentService );
        internalClient.setContentDao( contentDao );
        internalClient.setUserDao( userDao );
        internalClient.setTimeService( new SystemTimeService() );
        internalClient.setLivePortalTraceService( livePortalTraceService );
        internalClient.setLogService( logService );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );

        final UserKey user = fixture.findUserByName( "content-creator" ).getKey();
        PortalSecurityHolder.setLoggedInUser( user );
        PortalSecurityHolder.setImpersonatedUser( user );
    }

    @Test
    public void getLogEntries()
        throws Exception
    {
        // setup

        // create content event
        ContentKey contentKey1 = createPersonContent( "Some content created", ContentStatus.DRAFT );
        ContentEntity requestContent = new ContentEntity( fixture.findContentByKey( contentKey1 ) );
        fixture.flushAndClearHibernateSession();

        // update content event
        ContentData contentData = getCustomContentData( "Some content updated" );
        updateContent( contentKey1, requestContent.getDraftVersion().getKey(), ContentStatus.APPROVED, contentData );

        fixture.flushAndClearHibernateSession();

        // open content event
        final UserEntity userQuerier = fixture.findUserByName( "content-querier" );
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setUser( userQuerier.getKey() );
        command.setTableKeyValue( contentKey1.toInt() );
        command.setTableKey( Table.CONTENT );
        command.setType( LogType.ENTITY_OPENED );
        command.setTitle( "Some content opened" + " (" + contentKey1 + ")" );
        command.setPath( requestContent.getPathAsString() );
        command.setXmlData( requestContent.getMainVersion().getContentDataAsJDomDocument() );
        final LogEntryKey logEntryKey3 = logService.storeNew( command );

        fixture.flushAndClearHibernateSession();

        // login event
        final StoreNewLogEntryCommand newLoginEventCommand = new StoreNewLogEntryCommand();
        newLoginEventCommand.setType( LogType.LOGIN );
        newLoginEventCommand.setInetAddress( "127.0.0.1" );
        newLoginEventCommand.setTitle( userQuerier.getDisplayName() + " (" + userQuerier.getName() + ")" );
        newLoginEventCommand.setUser( userQuerier.getKey() );
        newLoginEventCommand.setSite( site );
        logService.storeNew( newLoginEventCommand );

        LogEntrySpecification spec = new LogEntrySpecification();
        spec.setAllowDuplicateEntries( true );
        LogEntryResultSet resultSet = logService.getLogEntries( spec, "", 100, 0 );
        assertEquals( 4, resultSet.getLogEntries().size() );

        // exercise
        GetLogEntriesParams params = new GetLogEntriesParams();

        final LogEntries logEntries = internalClient.getLogEntries( params );

        // verify
        assertEquals( 4, logEntries.getCount() );
        final LogEntry logEntry1 = logEntries.getLogEntry( 0 );
        final LogEntry logEntry2 = logEntries.getLogEntry( 1 );
        final LogEntry logEntry3 = logEntries.getLogEntry( 2 );
        final LogEntry logEntry4 = logEntries.getLogEntry( 3 );

        assertEquals( contentKey1.toInt(), logEntry1.getContentKey().intValue() );
        assertEquals( contentKey1.toInt(), logEntry2.getContentKey().intValue() );
        assertEquals( contentKey1.toInt(), logEntry3.getContentKey().intValue() );

        assertEquals( LogEventType.ENTITY_CREATED, logEntry1.getEventType() );
        assertEquals( LogEventType.ENTITY_UPDATED, logEntry2.getEventType() );
        assertEquals( LogEventType.ENTITY_OPENED, logEntry3.getEventType() );
        assertEquals( LogEventType.LOGIN, logEntry4.getEventType() );

        assertEquals( "Some content created" + " (" + contentKey1.toInt() + ")", logEntry1.getTitle() );
        assertEquals( "Some content updated" + " (" + contentKey1.toInt() + ")", logEntry2.getTitle() );
        assertEquals( "Some content opened" + " (" + contentKey1.toInt() + ")", logEntry3.getTitle() );
        assertEquals( userQuerier.getDisplayName() + " (" + userQuerier.getName() + ")", logEntry4.getTitle() );

        assertEquals( "content-creator", logEntry1.getUser() );
        assertEquals( "content-updater", logEntry2.getUser() );
        assertEquals( "content-querier", logEntry3.getUser() );
        assertEquals( "content-querier", logEntry4.getUser() );

        assertEquals( "Creator", logEntry1.getUsername() );
        assertEquals( "Updater", logEntry2.getUsername() );
        assertEquals( "Querier", logEntry3.getUsername() );
        assertEquals( "Querier", logEntry4.getUsername() );

        assertEquals( "127.0.0.1", logEntry1.getInetAddress() );
        assertEquals( "/MyPersonCategory/testcontent", logEntry1.getPath() );
        assertEquals( logEntryKey3.toString(), logEntry3.getLogKey() );
        assertEquals( site.getName(), logEntry4.getSite() );
        assertEquals( site.getKey().toInt(), logEntry4.getSiteKey().intValue() );
    }

    @Test
    public void getLogEntriesWithCountParam()
        throws Exception
    {
        // setup

        // create content events
        ContentKey contentKey1 = createPersonContent( "Some content created - 1", ContentStatus.DRAFT );
        ContentEntity requestContent1 = new ContentEntity( fixture.findContentByKey( contentKey1 ) );
        ContentKey contentKey2 = createPersonContent( "Some content created - 2", ContentStatus.DRAFT );
        ContentEntity requestContent2 = new ContentEntity( fixture.findContentByKey( contentKey2 ) );
        ContentKey contentKey3 = createPersonContent( "Some content created - 3", ContentStatus.DRAFT );
        ContentEntity requestContent3 = new ContentEntity( fixture.findContentByKey( contentKey3 ) );
        fixture.flushAndClearHibernateSession();

        LogEntrySpecification spec = new LogEntrySpecification();
        spec.setAllowDuplicateEntries( true );
        LogEntryResultSet resultSet = logService.getLogEntries( spec, "", 100, 0 );
        assertEquals( 3, resultSet.getLogEntries().size() );

        // exercise
        GetLogEntriesParams params = new GetLogEntriesParams();
        params.count = 2;
        final LogEntries logEntries = internalClient.getLogEntries( params );

        // verify
        assertEquals( 2, logEntries.getCount() );
        final LogEntry logEntry1 = logEntries.getLogEntry( 0 );
        final LogEntry logEntry2 = logEntries.getLogEntry( 1 );

        assertEquals( contentKey1.toInt(), logEntry1.getContentKey().intValue() );
        assertEquals( contentKey2.toInt(), logEntry2.getContentKey().intValue() );
    }

    @Test
    public void getLogEntriesWithFromParam()
        throws Exception
    {
        // setup

        // create content events
        ContentKey contentKey1 = createPersonContent( "Some content created - 1", ContentStatus.DRAFT );
        ContentEntity requestContent1 = new ContentEntity( fixture.findContentByKey( contentKey1 ) );
        ContentKey contentKey2 = createPersonContent( "Some content created - 2", ContentStatus.DRAFT );
        ContentEntity requestContent2 = new ContentEntity( fixture.findContentByKey( contentKey2 ) );
        fixture.flushAndClearHibernateSession();

        Thread.sleep( 50 );
        Date timeBeforeLastEvent = new Date();
        ContentKey contentKey3 = createPersonContent( "Some content created - 3", ContentStatus.DRAFT );
        ContentEntity requestContent3 = new ContentEntity( fixture.findContentByKey( contentKey3 ) );
        fixture.flushAndClearHibernateSession();

        LogEntrySpecification spec = new LogEntrySpecification();
        spec.setAllowDuplicateEntries( true );
        LogEntryResultSet resultSet = logService.getLogEntries( spec, "", 100, 0 );
        assertEquals( 3, resultSet.getLogEntries().size() );

        // exercise
        GetLogEntriesParams params = new GetLogEntriesParams();
        params.from = timeBeforeLastEvent;
        final LogEntries logEntries = internalClient.getLogEntries( params );

        // verify
        assertEquals( 1, logEntries.getCount() );
        final LogEntry logEntry1 = logEntries.getLogEntry( 0 );

        assertEquals( contentKey3.toInt(), logEntry1.getContentKey().intValue() );
    }

    @Test
    public void getLogEntriesWithToParam()
        throws Exception
    {
        // setup

        // create content events
        ContentKey contentKey1 = createPersonContent( "Some content created - 1", ContentStatus.DRAFT );
        ContentEntity requestContent1 = new ContentEntity( fixture.findContentByKey( contentKey1 ) );
        ContentKey contentKey2 = createPersonContent( "Some content created - 2", ContentStatus.DRAFT );
        ContentEntity requestContent2 = new ContentEntity( fixture.findContentByKey( contentKey2 ) );
        fixture.flushAndClearHibernateSession();

        Date timeBeforeLastEvent = new Date();
        Thread.sleep( 50 );
        ContentKey contentKey3 = createPersonContent( "Some content created - 3", ContentStatus.DRAFT );
        ContentEntity requestContent3 = new ContentEntity( fixture.findContentByKey( contentKey3 ) );
        fixture.flushAndClearHibernateSession();

        LogEntrySpecification spec = new LogEntrySpecification();
        spec.setAllowDuplicateEntries( true );
        LogEntryResultSet resultSet = logService.getLogEntries( spec, "", 100, 0 );
        assertEquals( 3, resultSet.getLogEntries().size() );

        // exercise
        GetLogEntriesParams params = new GetLogEntriesParams();
        params.to = timeBeforeLastEvent;
        final LogEntries logEntries = internalClient.getLogEntries( params );

        // verify
        assertEquals( 2, logEntries.getCount() );
        final LogEntry logEntry1 = logEntries.getLogEntry( 0 );
        final LogEntry logEntry2 = logEntries.getLogEntry( 1 );

        assertEquals( contentKey1.toInt(), logEntry1.getContentKey().intValue() );
        assertEquals( contentKey2.toInt(), logEntry2.getContentKey().intValue() );
    }

    private ContentKey createPersonContent( String name, ContentStatus status )
    {
        CustomContentData contentData = getCustomContentData( name );
        ContentKey expectedContentKey = contentService.createContent(
            createCreateContentCommand( "MyPersonCategory", "content-creator", status, contentData, new DateTime( 2013, 1, 1, 0, 0, 0, 0 ),
                                        null ) );
        return expectedContentKey;
    }

    private CustomContentData getCustomContentData( final String name )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyPersonType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), name ) );
        return contentData;
    }

    private CreateContentCommand createCreateContentCommand( String categoryName, String creatorUid, ContentStatus contentStatus,
                                                             ContentData contentData, DateTime availableFrom, DateTime availableTo )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testcontent" );

        if ( availableFrom != null )
        {
            createContentCommand.setAvailableFrom( availableFrom.toDate() );
        }
        if ( availableTo != null )
        {
            createContentCommand.setAvailableTo( availableTo.toDate() );
        }
        return createContentCommand;
    }

    private UpdateContentResult updateContent( ContentKey contentKey, ContentVersionKey versionKey, ContentStatus status,
                                               ContentData contentData )
    {
        UpdateContentCommand command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( versionKey );
        command.setModifier( fixture.findUserByName( "content-updater" ) );
        command.setUpdateAsMainVersion( true );
        command.setLanguage( fixture.findLanguageByCode( "en" ) );
        command.setStatus( status );
        command.setContentKey( contentKey );
        command.setContentData( contentData );
        return contentService.updateContent( command );
    }
}
