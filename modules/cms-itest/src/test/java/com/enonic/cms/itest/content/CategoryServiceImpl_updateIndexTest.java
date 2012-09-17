package com.enonic.cms.itest.content;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.DeleteCategoryCommand;
import com.enonic.cms.core.content.category.ModifyCategoryACLCommand;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.category.SynchronizeCategoryACLCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.query.OpenContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.search.ContentIndexServiceTestHibernatedBase;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CategoryServiceImpl_updateIndexTest
    extends ContentIndexServiceTestHibernatedBase
{

    private Document personCtyConfigAsDocument;

    @Autowired
    private ContentService contentService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected GroupDao groupDao;

    @Autowired
    private ContentDao contentDao;

    public static final String CONTENT_TYPE_NAME = "aContentType";

    @Before
    public void setUp()
    {
        SynchronizeCategoryACLCommand.executeInOneTransaction = true;
        ModifyCategoryACLCommand.executeInOneTransaction = true;

        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        // setting up a simple content type config
        ContentTypeConfigBuilder contentTypeConfigBuilder = new ContentTypeConfigBuilder( "Person", "name" );
        contentTypeConfigBuilder.startBlock( "Person" );
        contentTypeConfigBuilder.addInput( "name", "text", "contentdata/name", "Name", true );
        contentTypeConfigBuilder.endBlock();
        personCtyConfigAsDocument = XMLDocumentFactory.create( contentTypeConfigBuilder.toString() ).getAsJDOMDocument();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( CONTENT_TYPE_NAME, ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 personCtyConfigAsDocument ) );
    }

    @Test
    public void index_updated_for_content_in_category_when_acl_modified()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();

        // exercise

        // Verify that user does not have access, since admin_browse is needed
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( fixture.findGroupByName( aNormalUserUid ).getGroupKey() );
        acl.setAdminBrowseAccess( true );
        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }


    @Test
    public void index_updated_for_content_in_category_when_acl_syncronized()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();

        // exercise

        // Verify that user does not have access, since admin_browse is needed
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( fixture.findGroupByName( aNormalUserUid ).getGroupKey() );
        acl.setAdminBrowseAccess( true );
        syncronizeACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_multiple_content_in_category_when_acl_modified()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity content1Access = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey1 =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( content1Access ), "content1" );

        ContentAccessEntity content2Access = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey2 =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( content2Access ), "content2" );

        fixture.flushAndClearHibernateSesssion();

        assertNotNull( contentDao.findByKey( contentKey1 ) );
        assertNotNull( contentDao.findByKey( contentKey2 ) );

        // exercise

        // Verify that user does not have access, since admin_browse is needed
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( fixture.findGroupByName( aNormalUserUid ).getGroupKey() );
        acl.setAdminBrowseAccess( true );
        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        printAllIndexContent();

        // Verify that user now get all content from category
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 2, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_multiple_content_in_category_when_acl_syncronized()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity content1Access = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey1 =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( content1Access ), "content1" );

        ContentAccessEntity content2Access = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey2 =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( content2Access ), "content2" );

        fixture.flushAndClearHibernateSesssion();

        assertNotNull( contentDao.findByKey( contentKey1 ) );
        assertNotNull( contentDao.findByKey( contentKey2 ) );

        // exercise

        // Verify that user does not have access, since admin_browse is needed
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( fixture.findGroupByName( aNormalUserUid ).getGroupKey() );
        acl.setAdminBrowseAccess( true );
        syncronizeACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        printAllIndexContent();

        // Verify that user now get all content from category
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 2, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_content_in_category_when_acl_modified_removed()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        final GroupKey aNormalUserGroupKey = fixture.findGroupByName( aNormalUserUid ).getGroupKey();
        final UserKey adminUserKey = fixture.findUserByName( adminUser ).getKey();

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( true );

        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Assert user access
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );

        // exercise

        // Remove ACL for user on category
        ModifyCategoryACLCommand modifyCategoryACLCommand = new ModifyCategoryACLCommand();
        modifyCategoryACLCommand.addToBeRemoved( aNormalUserGroupKey );
        modifyCategoryACLCommand.includeContent();
        modifyCategoryACLCommand.setUpdater( adminUserKey );
        modifyCategoryACLCommand.addCategory( fixture.findCategoryByName( categoryName ).getKey() );
        categoryService.modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( modifyCategoryACLCommand );

        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_content_in_category_when_acl_syncronized_removed()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        final GroupKey aNormalUserGroupKey = fixture.findGroupByName( aNormalUserUid ).getGroupKey();
        final UserKey adminUserKey = fixture.findUserByName( adminUser ).getKey();

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( true );

        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Assert user access
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );

        // exercise

        // Remove ACL for user on category
        syncronizeACLForCategory( categoryName, adminUser, null );

        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_content_in_category_when_acl_modified_changed()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        final GroupKey aNormalUserGroupKey = fixture.findGroupByName( aNormalUserUid ).getGroupKey();
        final UserKey adminUserKey = fixture.findUserByName( adminUser ).getKey();

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( true );

        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Assert user access
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );

        // exercise

        // Change ACL to admin browse false for user on category
        acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( false );

        ModifyCategoryACLCommand modifyCategoryACLCommand = new ModifyCategoryACLCommand();
        modifyCategoryACLCommand.addToBeModified( acl );
        modifyCategoryACLCommand.includeContent();
        modifyCategoryACLCommand.setUpdater( adminUserKey );
        modifyCategoryACLCommand.addCategory( fixture.findCategoryByName( categoryName ).getKey() );
        categoryService.modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( modifyCategoryACLCommand );

        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_content_in_category_when_acl_syncronized_changed()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        final GroupKey aNormalUserGroupKey = fixture.findGroupByName( aNormalUserUid ).getGroupKey();

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( true );

        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Assert user access
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );

        // exercise

        // Change ACL to admin browse false for user on category
        acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( false );

        syncronizeACLForCategory( categoryName, adminUser, acl );
        fixture.flushAndClearHibernateSesssion();

        // Verify that user now get content from query
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    @Test
    public void index_updated_for_content_in_category_category_deleted_including_content()
    {
        // setup
        final String categoryName = "Category";
        final String adminUser = "admin";
        final String aNormalUserUid = "aUser";

        createUser( aNormalUserUid, UserType.NORMAL );

        final CategoryKey categoryKey = storeCategory( CONTENT_TYPE_NAME, categoryName );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        // Create content with read access
        ContentAccessEntity normalUserAccess = createContentAccess( aNormalUserUid, true, false );
        final ContentKey contentKey =
            createContent( CONTENT_TYPE_NAME, categoryName, adminUser, Lists.newArrayList( normalUserAccess ), "aContent" );
        assertNotNull( contentDao.findByKey( contentKey ) );

        final GroupKey aNormalUserGroupKey = fixture.findGroupByName( aNormalUserUid ).getGroupKey();

        // Add admin browse for user on category
        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( aNormalUserGroupKey );
        acl.setAdminBrowseAccess( true );

        modifyACLForCategory( categoryName, adminUser, acl );

        fixture.flushAndClearHibernateSesssion();

        // Assert content exists and accessable
        OpenContentQuery queryAssertingCategoryBrowse = createQueryAssertingCategoryBrowse( aNormalUserUid, categoryKey );
        ContentResultSet contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 1, contentResultSet.getKeys().size() );

        // exercise

        // Delete category including content
        DeleteCategoryCommand deleteCategoryCommand = new DeleteCategoryCommand();
        deleteCategoryCommand.setIncludeContent( true );
        deleteCategoryCommand.setCategoryKey( categoryKey );
        deleteCategoryCommand.setDeleter( fixture.findUserByName( adminUser ).getKey() );
        deleteCategoryCommand.setRecursive( false );
        categoryService.deleteCategory( deleteCategoryCommand );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        // Verify that content is now also deleted from index
        contentResultSet = contentService.queryContent( queryAssertingCategoryBrowse );
        assertEquals( 0, contentResultSet.getKeys().size() );
    }

    private OpenContentQuery createQueryAssertingCategoryBrowse( final String aNormalUserUid, final CategoryKey categoryKey )
    {
        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( aNormalUserUid ) );
        query.setCategoryKeyFilter( Lists.newArrayList( categoryKey ), 1 );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE, CategoryAccessType.READ ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        return query;
    }

    private void modifyACLForCategory( final String categoryName, final String updaterUid, final CategoryAccessControl acl )
    {
        ModifyCategoryACLCommand modifyCategoryACLCommand = new ModifyCategoryACLCommand();
        modifyCategoryACLCommand.addToBeAdded( acl );
        modifyCategoryACLCommand.includeContent();
        modifyCategoryACLCommand.setUpdater( fixture.findUserByName( updaterUid ).getKey() );
        modifyCategoryACLCommand.addCategory( fixture.findCategoryByName( categoryName ).getKey() );

        categoryService.modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( modifyCategoryACLCommand );
    }

    private void syncronizeACLForCategory( final String categoryName, final String updaterUid, final CategoryAccessControl acl )
    {
        SynchronizeCategoryACLCommand synchronizeCategoryACLCommand = new SynchronizeCategoryACLCommand();
        synchronizeCategoryACLCommand.includeContent();
        synchronizeCategoryACLCommand.setUpdater( fixture.findUserByName( updaterUid ).getKey() );
        synchronizeCategoryACLCommand.addCategory( fixture.findCategoryByName( categoryName ).getKey() );

        synchronizeCategoryACLCommand.addAccessControlList(
            acl != null ? Lists.newArrayList( acl ) : new ArrayList<CategoryAccessControl>() );

        categoryService.synchronizeCategoryACL_withoutRequiresNewPropagation_for_test_only( synchronizeCategoryACLCommand );
    }

    private void createUser( final String aNormalUserUid, final UserType userType )
    {
        fixture.createAndStoreUserAndUserGroup( aNormalUserUid, aNormalUserUid + "fullname", userType, "testuserstore" );
    }

    private ContentKey createContent( final String contentTypeName, final String categoryName, final String creatorUid,
                                      List<ContentAccessEntity> contentAccesses, final String contentName )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( contentTypeName ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "person" ) );
        CreateContentCommand createContentCommand = createCreateContentCommand( contentName, categoryName, contentData, creatorUid );
        createContentCommand.addContentAccessRights( contentAccesses, null );
        final ContentKey content = contentService.createContent( createContentCommand );

        fixture.flushAndClearHibernateSesssion();

        return content;
    }

    private CategoryKey storeCategory( final String contentTypeName, final String categoryName )
    {
        StoreNewCategoryCommand storeNewCategoryCommand = createStoreNewCategoryCommand( categoryName, contentTypeName, null );
        return categoryService.storeNewCategory( storeNewCategoryCommand );
    }

    protected CreateContentCommand createCreateContentCommand( String categoryName, String creatorUid, ContentStatus contentStatus )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setContentName( "name_" + categoryName + "_" + contentStatus );

        ContentTypeConfig contentTypeConfig = fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig();
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Initial" ) );
        createContentCommand.setContentData( contentData );
        return createContentCommand;
    }

    private CreateContentCommand createCreateContentCommand( String contentName, String categoryName, ContentData contentData,
                                                             final String creatorUid )
    {
        CreateContentCommand command = new CreateContentCommand();
        command.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        command.setStatus( ContentStatus.APPROVED );
        command.setContentName( contentName );
        command.setCategory( fixture.findCategoryByName( categoryName ).getKey() );
        command.setContentData( contentData );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        command.setPriority( 0 );
        return command;
    }

    private StoreNewCategoryCommand createStoreNewCategoryCommand( String name, String contentTypeName, String parentCategoryName )
    {
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setCreator( fixture.findUserByName( "admin" ).getKey() );
        command.setParentCategory( parentCategoryName != null ? fixture.findCategoryByName( parentCategoryName ).getKey() : null );
        command.setName( name );
        command.setDescription( "A " + name + "." );
        command.setContentType( fixture.findContentTypeByName( contentTypeName ).getContentTypeKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        command.setAutoApprove( true );
        return command;
    }
}
