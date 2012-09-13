package com.enonic.cms.itest.search;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.query.OpenContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.web.portal.SiteRedirectHelper;
import com.enonic.cms.web.portal.services.ContentServicesProcessor;
import com.enonic.cms.web.portal.services.UserServicesRedirectUrlResolver;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.*;

public class ContentIndexServiceImpl_accessRightsTest
    extends ContentIndexServiceTestHibernatedBase
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected GroupDao groupDao;

    @Autowired
    protected ContentService contentService;

    private SiteRedirectHelper siteRedirectHelper;

    private ContentServicesProcessor customContentHandlerController;

    private UserServicesRedirectUrlResolver userServicesRedirectUrlResolver;

    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // Dummy

        customContentHandlerController = new ContentServicesProcessor();
        customContentHandlerController.setContentService( contentService );
        customContentHandlerController.setSecurityService( securityService );
        customContentHandlerController.setCategoryDao( categoryDao );

        userServicesRedirectUrlResolver = Mockito.mock( UserServicesRedirectUrlResolver.class );
        customContentHandlerController.setUserServicesRedirectHelper( userServicesRedirectUrlResolver );

        // just need a dummy of the SiteRedirectHelper
        siteRedirectHelper = createMock( SiteRedirectHelper.class );
        customContentHandlerController.setSiteRedirectHelper( siteRedirectHelper );

        // setup needed common data for each test
        fixture.initSystemData();

        //SecurityHolder.setUser( findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void testContentAccessible_user_access_test()
        throws Exception
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "norway_user", "testuserstore" );
        createAndSaveNormalUser( "europe_user", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "norway_user", "create" );

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "norway_user", ContentStatus.DRAFT );

        ContentAccessEntity rmyAccess = createContentAccess( "norway_user", true, false );
        ContentAccessEntity adminAccess = createContentAccess( "europe_user", true, false );
        createContentCommand.addContentAccessRights( Lists.newArrayList( rmyAccess, adminAccess ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "norway_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "europe_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );

        contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "no_access_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testContentAccessible_group_direct_access_test()
        throws Exception
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "rmy", "testuserstore" );
        createAndSaveNormalUser( "administrator", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "rmy", "create" );

        fixture.flushAndClearHibernateSesssion();

        final GroupEntity groupWithAccess = createAndSaveGroup( "group_with_access", "testuserstore", GroupType.USERSTORE_GROUP );

        fixture.findUserByName( "rmy" ).getUserGroup().addMembership( groupWithAccess );
        fixture.findUserByName( "administrator" ).getUserGroup().addMembership( groupWithAccess );

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "rmy", ContentStatus.DRAFT );

        ContentAccessEntity groupAccess = createContentAccess( groupWithAccess, true, false );
        createContentCommand.addContentAccessRights( Lists.newArrayList( groupAccess ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "rmy" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "administrator" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "no_access_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }


    @Test
    public void testContentAccessible_group_transitive_access_test()
        throws Exception
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "norway_user", "testuserstore" );
        createAndSaveNormalUser( "europe_user", "testuserstore" );
        createAndSaveNormalUser( "world_user", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "norway_user", "create" );

        fixture.flushAndClearHibernateSesssion();

        final GroupEntity groupNorway = createAndSaveGroup( "norway", "testuserstore", GroupType.USERSTORE_GROUP );
        final GroupEntity groupEurope = createAndSaveGroup( "europe", "testuserstore", GroupType.USERSTORE_GROUP );
        final GroupEntity groupWorld = createAndSaveGroup( "world", "testuserstore", GroupType.USERSTORE_GROUP );

        fixture.findUserByName( "norway_user" ).getUserGroup().addMembership( groupNorway );
        fixture.findUserByName( "europe_user" ).getUserGroup().addMembership( groupEurope );
        fixture.findUserByName( "world_user" ).getUserGroup().addMembership( groupWorld );
        fixture.findGroupByName( "norway" ).addMembership( groupEurope );
        fixture.findGroupByName( "europe" ).addMembership( groupWorld );

        fixture.flushAndClearHibernateSesssion();

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "norway_user", ContentStatus.DRAFT );

        // Add access rights to all in world - group
        ContentAccessEntity groupAccess = createContentAccess( groupWorld, true, false);
        createContentCommand.addContentAccessRights( Lists.newArrayList( groupAccess ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "norway_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( "norway_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "europe_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( "europe_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "world_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( "world_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "no_access_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void testContentAccessible_child_not_parent_no_access_test()
        throws Exception
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "rmy", "testuserstore" );
        createAndSaveNormalUser( "administrator", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "rmy", "admin_browse, read, create" );

        fixture.flushAndClearHibernateSesssion();

        final GroupEntity groupWithAccess = createAndSaveGroup( "group_with_access", "testuserstore", GroupType.USERSTORE_GROUP );
        final GroupEntity groupWithAccessParent =
            createAndSaveGroup( "group_with_access_parent", "testuserstore", GroupType.USERSTORE_GROUP );

        fixture.findUserByName( "rmy" ).getUserGroup().addMembership( groupWithAccessParent );
        fixture.findUserByName( "administrator" ).getUserGroup().addMembership( groupWithAccessParent );

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "rmy", ContentStatus.DRAFT );

        ContentAccessEntity groupAccess = createContentAccess( groupWithAccess, true, false );
        createContentCommand.addContentAccessRights( Lists.newArrayList( groupAccess ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "rmy" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "administrator" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "no_access_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }


    @Test
    public void testContentAccessible_category_access_test()
        throws Exception
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "norway_user", "testuserstore" );
        createAndSaveNormalUser( "europe_user", "testuserstore" );
        createAndSaveNormalUser( "world_user", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );

        fixture.flushAndClearHibernateSesssion();

        final GroupEntity groupNorway = createAndSaveGroup( "norway", "testuserstore", GroupType.USERSTORE_GROUP );
        final GroupEntity groupEurope = createAndSaveGroup( "europe", "testuserstore", GroupType.USERSTORE_GROUP );
        final GroupEntity groupWorld = createAndSaveGroup( "world", "testuserstore", GroupType.USERSTORE_GROUP );

        fixture.findUserByName( "norway_user" ).getUserGroup().addMembership( groupNorway );
        fixture.findUserByName( "europe_user" ).getUserGroup().addMembership( groupEurope );
        fixture.findUserByName( "world_user" ).getUserGroup().addMembership( groupWorld );
        fixture.findGroupByName( "norway" ).addMembership( groupEurope );
        fixture.findGroupByName( "europe" ).addMembership( groupWorld );

        fixture.flushAndClearHibernateSesssion();

        createAndSaveCategoryAccessForGroup( categoryName, "world", "create, read, admin_browse" );

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "norway_user", ContentStatus.DRAFT );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );

        // Add access rights to all in world - group
        //ContentAccessEntity groupAccess = createContentAccess( groupWorld );
        //createContentCommand.addContentAccessRights( Lists.newArrayList( groupAccess ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "norway_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.READ ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( "norway_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "europe_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        contentResultSet = contentService.queryContent( query );
        assertEquals( "europe_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "world_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        contentResultSet = contentService.queryContent( query );
        assertEquals( "world_user should have access", 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "no_access_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }


}
