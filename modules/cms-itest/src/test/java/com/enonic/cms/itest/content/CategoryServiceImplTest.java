package com.enonic.cms.itest.content;


import java.util.List;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessType;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.CreateCategoryAccessException;
import com.enonic.cms.core.content.category.DeleteCategoryCommand;
import com.enonic.cms.core.content.category.ModifyCategoryACLCommand;
import com.enonic.cms.core.content.category.MoveCategoryCommand;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.category.SynchronizeCategoryACLCommand;
import com.enonic.cms.core.content.category.UnitEntity;
import com.enonic.cms.core.content.category.UpdateCategoryCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class CategoryServiceImplTest
    extends AbstractSpringTest
{

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

    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    private Document personCtyConfigAsDocument;

    @Before
    public void setUp()
    {
        SynchronizeCategoryACLCommand.executeInOneTransaction = true;
        ModifyCategoryACLCommand.executeInOneTransaction = true;

        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "MyUser", "MyUser fullname", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "NoRightsUser", "NoRightsUser fullname", UserType.NORMAL, "testuserstore" );

        // setting up a simple content type config
        ContentTypeConfigBuilder contentTypeConfigBuilder = new ContentTypeConfigBuilder( "Person", "name" );
        contentTypeConfigBuilder.startBlock( "Person" );
        contentTypeConfigBuilder.addInput( "name", "text", "contentdata/name", "Name", true );
        contentTypeConfigBuilder.endBlock();
        personCtyConfigAsDocument = XMLDocumentFactory.create( contentTypeConfigBuilder.toString() ).getAsJDOMDocument();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );
    }

    @Test
    public void move_category()
    {
        // setup
        final UnitEntity firstUnit = factory.createUnit( "FirstUnit", "by" );
        fixture.save( firstUnit );
        fixture.save( factory.createCategory( "ParentCategory1", null, null, "FirstUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory1", "MyUser", "administrate, read, create, approve, admin_browse" ) );

        final UnitEntity secondUnit = factory.createUnit( "SecondUnit", "by" );
        fixture.save( secondUnit );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory2", "MyUser", "administrate, read, create, approve, admin_browse" ) );
        CategoryEntity parent1 = fixture.findCategoryByName( "ParentCategory1" );
        CategoryEntity parent2 = fixture.findCategoryByName( "ParentCategory2" );
        User user = fixture.findUserByName( "MyUser" );
        UserKey userKey = user.getKey();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "SubCat1" );
        command.setCreator( userKey );
        command.setParentCategory( parent1.getKey() );
        CategoryKey subCat1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat2" );
        command.setCreator( userKey );
        command.setParentCategory( subCat1Key );
        CategoryKey subCat2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat3" );
        command.setCreator( userKey );
        command.setParentCategory( subCat2Key );
        CategoryKey subCat3Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article1" );
        command.setCreator( userKey );
        command.setParentCategory( parent2.getKey() );
        CategoryKey article1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article2" );
        command.setCreator( userKey );
        command.setParentCategory( article1Key );
        CategoryKey article2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article3" );
        command.setCreator( userKey );
        command.setParentCategory( article2Key );
        categoryService.storeNewCategory( command );

        assertEquals( 8, categoryDao.findAll( 0, 100 ).getList().size() );

        // verify state before moving (unit = 2; subCat3 doesn't contain articleX categories)
        CategoryEntity cat = categoryDao.findByKey( subCat3Key );
        CategoryEntity article1 = categoryDao.findByKey( article1Key );
        assertFalse( cat.getChildren().contains( article1 ) );
        assertEquals( secondUnit.getKey().toInt(), article1.getUnit().getKey().toInt() );
        for ( CategoryEntity categoryEntity : article1.getChildren() )
        {
            assertEquals( secondUnit.getKey().toInt(), categoryEntity.getUnit().getKey().toInt() );
        }

        // exercise
        MoveCategoryCommand moveCommand = new MoveCategoryCommand();
        moveCommand.setUser( fixture.findUserByName( "MyUser" ).getKey() );
        moveCommand.setCategoryToMove( article1Key );
        moveCommand.setDestinationCategory( subCat3Key );
        categoryService.moveCategory( moveCommand );

        // verify state after moving (unit = 1; subCat3 contains articleX categories)
        CategoryEntity result = categoryDao.findByKey( subCat3Key );
        CategoryEntity child = categoryDao.findByKey( article1Key );

        assertTrue( result.getChildren().contains( child ) );
        assertEquals( firstUnit.getKey().toInt(), child.getUnit().getKey().toInt() );
        for ( CategoryEntity categoryEntity : child.getChildren() )
        {
            assertEquals( firstUnit.getKey().toInt(), categoryEntity.getUnit().getKey().toInt() );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void move_category_to_subcategory()
    {
        // setup
        fixture.save( factory.createUnit( "FirstUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory1", null, null, "FirstUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory1", "MyUser", "administrate, read, create, approve, admin_browse" ) );

        fixture.save( factory.createUnit( "SecondUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory2", "MyUser", "administrate, read, create, approve, admin_browse" ) );
        CategoryEntity parent1 = fixture.findCategoryByName( "ParentCategory1" );
        CategoryEntity parent2 = fixture.findCategoryByName( "ParentCategory2" );
        User user = fixture.findUserByName( "MyUser" );
        UserKey userKey = user.getKey();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "SubCat1" );
        command.setCreator( userKey );
        command.setParentCategory( parent1.getKey() );
        CategoryKey subCat1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat2" );
        command.setCreator( userKey );
        command.setParentCategory( subCat1Key );
        CategoryKey subCat2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat3" );
        command.setCreator( userKey );
        command.setParentCategory( subCat2Key );
        CategoryKey subCat3Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article1" );
        command.setCreator( userKey );
        command.setParentCategory( parent2.getKey() );
        CategoryKey article1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article2" );
        command.setCreator( userKey );
        command.setParentCategory( article1Key );
        CategoryKey article2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article3" );
        command.setCreator( userKey );
        command.setParentCategory( article2Key );
        categoryService.storeNewCategory( command );

        assertEquals( 8, categoryDao.findAll( 0, 100 ).getList().size() );

        // verify state before moving (unit = 2; subCat3 doesn't contain articleX categories)
        CategoryEntity cat = categoryDao.findByKey( subCat3Key );
        CategoryEntity article1 = categoryDao.findByKey( article1Key );
        assertFalse( cat.getChildren().contains( article1 ) );

        // exercise
        MoveCategoryCommand moveCommand = new MoveCategoryCommand();
        moveCommand.setUser( fixture.findUserByName( "MyUser" ).getKey() );
        moveCommand.setCategoryToMove( article1Key );
        moveCommand.setDestinationCategory( article2Key );

        categoryService.moveCategory( moveCommand );
    }

    @Test(expected = CategoryAccessException.class)
    public void move_category_no_rights()
    {
        // setup
        fixture.save( factory.createUnit( "FirstUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory1", null, null, "FirstUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory1", "MyUser", "administrate, read, create, approve, admin_browse" ) );

        fixture.save( factory.createUnit( "SecondUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory2", "MyUser", "administrate, read, create, approve, admin_browse" ) );
        CategoryEntity parent1 = fixture.findCategoryByName( "ParentCategory1" );
        CategoryEntity parent2 = fixture.findCategoryByName( "ParentCategory2" );
        User user = fixture.findUserByName( "MyUser" );
        UserKey userKey = user.getKey();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "SubCat1" );
        command.setCreator( userKey );
        command.setParentCategory( parent1.getKey() );
        CategoryKey subCat1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat2" );
        command.setCreator( userKey );
        command.setParentCategory( subCat1Key );
        CategoryKey subCat2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "SubCat3" );
        command.setCreator( userKey );
        command.setParentCategory( subCat2Key );
        CategoryKey subCat3Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article1" );
        command.setCreator( userKey );
        command.setParentCategory( parent2.getKey() );
        CategoryKey article1Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article2" );
        command.setCreator( userKey );
        command.setParentCategory( article1Key );
        CategoryKey article2Key = categoryService.storeNewCategory( command );

        command = new StoreNewCategoryCommand();
        command.setName( "Article3" );
        command.setCreator( userKey );
        command.setParentCategory( article2Key );
        categoryService.storeNewCategory( command );

        assertEquals( 8, categoryDao.findAll( 0, 100 ).getList().size() );

        // verify state before moving (unit = 2; subCat3 doesn't contain articleX categories)
        CategoryEntity cat = categoryDao.findByKey( subCat3Key );
        CategoryEntity article1 = categoryDao.findByKey( article1Key );
        assertFalse( cat.getChildren().contains( article1 ) );

        // exercise
        MoveCategoryCommand moveCommand = new MoveCategoryCommand();
        moveCommand.setUser( fixture.findUserByName( "NoRightsUser" ).getKey() );
        moveCommand.setCategoryToMove( article1Key );
        moveCommand.setDestinationCategory( article2Key );

        categoryService.moveCategory( moveCommand );
    }

    @Test
    public void usual_category_is_created()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory", null, null, "MyUnit", "MyUser", "MyUser" ) );
        fixture.save(
            factory.createCategoryAccessForUser( "ParentCategory", "MyUser", "administrate, read, create, approve, admin_browse" ) );
        fixture.flushAndClearHibernateSession();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setParentCategory( fixture.findCategoryByName( "ParentCategory" ).getKey() );
        CategoryKey key = categoryService.storeNewCategory( command );

        assertEquals( "Test category", categoryDao.findByKey( key ).getName() );
    }

    @Test(expected = CreateCategoryAccessException.class)
    public void create_category_without_access_rights()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "ParentCategory", null, null, "MyUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory", "MyUser", "read" ) );
        fixture.flushAndClearHibernateSession();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setParentCategory( fixture.findCategoryByName( "ParentCategory" ).getKey() );
        categoryService.storeNewCategory( command );
    }

    @Test(expected = CreateCategoryAccessException.class)
    public void create_archive_without_access_rights()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.flushAndClearHibernateSession();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        categoryService.storeNewCategory( command );
    }

    @Test
    public void unit_is_created_when_creating_content_archive()
    {
        // setup
        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "My archive" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        CategoryKey key = categoryService.storeNewCategory( command );

        assertNotNull( categoryDao.findByKey( key ) );
        assertNotNull( categoryDao.findByKey( key ).getUnit() );
        assertEquals( "My archive", categoryDao.findByKey( key ).getUnit().getName() );
        assertEquals( "en", categoryDao.findByKey( key ).getUnit().getLanguage().getCode() );
    }

    @Test
    public void unit_with_allowed_content_types_is_created_when_creating_content_archive()
    {
        // setup
        fixture.save( factory.createContentType( "MyContentType2", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "My archive" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        command.addAllowedContentType( fixture.findContentTypeByName( "MyContentType" ).getContentTypeKey() );
        CategoryKey key = categoryService.storeNewCategory( command );

        assertEquals( fixture.findContentTypeByName( "MyContentType" ).getContentTypeKey(),
                      categoryDao.findByKey( key ).getUnit().getContentTypes().iterator().next().getContentTypeKey() );
    }

    @Test
    public void accessrights_for_administrator_is_persisted_when_creating_content_archive_and_accessrights_are_not_given()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "en" ) );
        fixture.flushAndClearHibernateSession();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );

        CategoryKey key = categoryService.storeNewCategory( command );

        assertNotNull( categoryDao.findByKey( key ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( key ).getAccessRights().size() == 1 );

        CategoryAccessEntity categoryAccess = categoryDao.findByKey( key ).getAccessRights().values().iterator().next();
        assertEquals( groupDao.findBuiltInAdministrator().getGroupKey(), categoryAccess.getKey().getGroupKey() );
        assertTrue( categoryAccess.isAdminAccess() );
        assertTrue( categoryAccess.isAdminBrowseAccess() );
        assertTrue( categoryAccess.isCreateAccess() );
        assertTrue( categoryAccess.isPublishAccess() );
        assertTrue( categoryAccess.isReadAccess() );
    }

    @Test
    public void accessrights_is_persisted_when_creating_content_archive_and_accessrights_are_given()
    {
        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        command.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );

        CategoryAccessControl accessControl = new CategoryAccessControl();
        accessControl.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessControl.setAdminAccess( false );
        accessControl.setAdminBrowseAccess( false );
        accessControl.setCreateAccess( false );
        accessControl.setPublishAccess( true );
        accessControl.setReadAccess( true );
        command.addAccessRight( accessControl );

        CategoryKey key = categoryService.storeNewCategory( command );

        // verify
        assertNotNull( categoryDao.findByKey( key ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( key ).getAccessRights().size() == 2 );
        assertNotNull( categoryDao.findByKey( key ).getAccessRights().get( groupDao.findBuiltInAdministrator().getGroupKey() ) );
        CategoryAccessEntity categoryAccess =
            categoryDao.findByKey( key ).getAccessRights().get( groupDao.findBuiltInDeveloper().getGroupKey() );

        assertFalse( categoryAccess.isAdminAccess() );
        assertFalse( categoryAccess.isAdminBrowseAccess() );
        assertFalse( categoryAccess.isCreateAccess() );
        assertTrue( categoryAccess.isPublishAccess() );
        assertTrue( categoryAccess.isReadAccess() );
    }

    @Test
    public void inherited_accessrights_from_top_category_is_persisted_when_creating_child_category_without_given_accessrights()
    {
        // setup
        fixture.save( factory.createUnit( "MyCommandUnit", "en" ) );
        fixture.flushAndClearHibernateSession();

        // create top category with defined access rights
        StoreNewCategoryCommand topCategoryCommand = new StoreNewCategoryCommand();
        topCategoryCommand.setName( "Test category" );
        topCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        topCategoryCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );

        CategoryAccessControl accessControl = new CategoryAccessControl();
        accessControl.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessControl.setAdminAccess( false );
        accessControl.setAdminBrowseAccess( false );
        accessControl.setCreateAccess( false );
        accessControl.setPublishAccess( true );
        accessControl.setReadAccess( true );
        topCategoryCommand.addAccessRight( accessControl );

        CategoryKey topCategoryKey = categoryService.storeNewCategory( topCategoryCommand );

        fixture.flushAndClearHibernateSession();

        //create child category under the top category
        StoreNewCategoryCommand childCategoryCommand = new StoreNewCategoryCommand();
        childCategoryCommand.setName( "Child category" );
        childCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        childCategoryCommand.setParentCategory( topCategoryKey );

        CategoryKey childCategoryKey = categoryService.storeNewCategory( childCategoryCommand );

        fixture.flushAndClearHibernateSession();

        assertEquals( "Child category", categoryDao.findByKey( childCategoryKey ).getName() );

        // verify same accessrights from parent is persisted
        assertNotNull( categoryDao.findByKey( childCategoryKey ).getAccessRights().values() );
        assertTrue( categoryDao.findByKey( childCategoryKey ).getAccessRights().size() == 2 );

        CategoryAccessEntity categoryDeveloperAccess =
            categoryDao.findByKey( childCategoryKey ).getAccessRights().get( groupDao.findBuiltInDeveloper().getGroupKey() );

        assertEquals( groupDao.findBuiltInDeveloper().getGroupKey(), categoryDeveloperAccess.getKey().getGroupKey() );
        assertFalse( categoryDeveloperAccess.isAdminAccess() );
        assertFalse( categoryDeveloperAccess.isAdminBrowseAccess() );
        assertFalse( categoryDeveloperAccess.isCreateAccess() );
        assertTrue( categoryDeveloperAccess.isPublishAccess() );
        assertTrue( categoryDeveloperAccess.isReadAccess() );

        CategoryAccessEntity categoryAdministratorAccess =
            categoryDao.findByKey( childCategoryKey ).getAccessRights().get( groupDao.findBuiltInAdministrator().getGroupKey() );

        assertEquals( groupDao.findBuiltInAdministrator().getGroupKey(), categoryAdministratorAccess.getKey().getGroupKey() );
        assertTrue( categoryAdministratorAccess.isAdminAccess() );
        assertTrue( categoryAdministratorAccess.isAdminBrowseAccess() );
        assertTrue( categoryAdministratorAccess.isCreateAccess() );
        assertTrue( categoryAdministratorAccess.isPublishAccess() );
        assertTrue( categoryAdministratorAccess.isReadAccess() );
    }

    @Test
    public void given_content_archive_when_deleted_then_unit_is_marked_deleted_and_category_marked_deleted()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnitToBeDeleted", "en" ) );
        fixture.save(
            factory.createCategory( "MyContentArchiveToBeDeleted", null, null, "MyUnitToBeDeleted", User.ROOT_UID, User.ANONYMOUS_UID ) );
        fixture.save( factory.createCategoryAccessForUser( "MyContentArchiveToBeDeleted", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        assertEquals( false, fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).isDeleted() );

        fixture.flushAndClearHibernateSession();

        // exercise
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategoryKey( fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).getKey() );
        categoryService.deleteCategory( command );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnitToBeDeleted" ).isDeleted() );
    }

    @Test
    public void deleteCategory_given_content_archive_with_sub_category_and_recursive_is_true_when_deleted_then_sub_category_is_marked_deleted()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnitToBeDeleted", "en" ) );
        fixture.save(
            factory.createCategory( "MyContentArchiveToBeDeleted", null, null, "MyUnitToBeDeleted", User.ROOT_UID, User.ANONYMOUS_UID ) );
        fixture.save( factory.createCategory( "MySubCategory", "MyContentArchiveToBeDeleted", null, "MyUnitToBeDeleted", User.ROOT_UID,
                                              User.ANONYMOUS_UID ) );
        fixture.save( factory.createCategoryAccessForUser( "MyContentArchiveToBeDeleted", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        assertEquals( false, fixture.findCategoryByName( "MySubCategory" ).isDeleted() );

        fixture.flushAndClearHibernateSession();

        // exercise
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategoryKey( fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).getKey() );
        command.setRecursive( true );
        categoryService.deleteCategory( command );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findCategoryByName( "MySubCategory" ).isDeleted() );
    }

    @Test
    public void given_content_archive_with_content_when_deleted_then_content_is_marked_deleted_and_category_marked_deleted()
    {
        // setup
        fixture.save( factory.createUnit( "MyUnitToBeDeleted", "en" ) );
        fixture.save( factory.createCategory( "MyContentArchiveToBeDeleted", null, "MyContentType", "MyUnitToBeDeleted", User.ROOT_UID,
                                              User.ANONYMOUS_UID ) );
        fixture.save( factory.createCategoryAccessForUser( "MyContentArchiveToBeDeleted", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        assertEquals( false, fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).isDeleted() );

        fixture.flushAndClearHibernateSession();

        // setup: create content in category MyTopCategory
        for ( int i = 1; i <= 10; i++ )
        {
            CustomContentData contentData =
                new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
            contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "person_" + i ) );
            CreateContentCommand createContentCommand =
                createCreateContentCommand( "person_" + i, "MyContentArchiveToBeDeleted", contentData );
            contentService.createContent( createContentCommand );
        }

        // exercise
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategoryKey( fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).getKey() );
        command.setIncludeContent( true );
        categoryService.deleteCategory( command );

        fixture.flushAndClearHibernateSession();

        // verify
        assertEquals( true, fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).isDeleted() );
        assertEquals( true, fixture.findUnitByName( "MyUnitToBeDeleted" ).isDeleted() );

        for ( int i = 1; i <= 10; i++ )
        {
            String contentName = "person_" + i;
            ContentEntity deletedContent = fixture.findContentByName( contentName );
            assertNotNull( deletedContent );
            assertEquals( true, deletedContent.isDeleted() );
        }
    }

    @Test
    public void updateCategory_update_top_category()
    {
        // setup
        fixture.save(
            factory.createContentType( "cty1", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );
        fixture.save(
            factory.createContentType( "cty2", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );

        StoreNewCategoryCommand storeNewCategoryCommand = new StoreNewCategoryCommand();
        storeNewCategoryCommand.setCreator( fixture.findUserByName( "admin" ).getKey() );
        storeNewCategoryCommand.setParentCategory( null );
        storeNewCategoryCommand.setName( "MyTopCategory" );
        storeNewCategoryCommand.setDescription( "A top category." );
        storeNewCategoryCommand.setContentType( fixture.findContentTypeByName( "cty1" ).getContentTypeKey() );
        storeNewCategoryCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        storeNewCategoryCommand.setAutoApprove( true );
        addCategoryAC( "MyUser", "read, create, admin_browse, approve, administrate ", storeNewCategoryCommand );

        categoryService.storeNewCategory( storeNewCategoryCommand );

        fixture.flushAndClearHibernateSession();

        // exercise
        UpdateCategoryCommand command = new UpdateCategoryCommand();
        command.setUpdater( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategory( fixture.findCategoryByName( "MyTopCategory" ).getKey() );
        command.setAutoApprove( false );
        command.setContentType( fixture.findContentTypeByName( "cty2" ).getContentTypeKey() );
        command.setName( "Changed name" );
        command.setDescription( "Changed description" );
        command.setLanguage( fixture.findLanguageByCode( "se" ).getKey() );
        command.addAllowedContentType( fixture.findContentTypeByName( "cty1" ).getContentTypeKey() );
        command.addAllowedContentType( fixture.findContentTypeByName( "cty2" ).getContentTypeKey() );
        categoryService.updateCategory( command );

        fixture.flushAndClearHibernateSession();

        // verify
        assertNotNull( fixture.findUnitByName( "Changed name" ) );
        assertEquals( "se", fixture.findUnitByName( "Changed name" ).getLanguage().getCode() );
        assertEquals( "Changed description", fixture.findUnitByName( "Changed name" ).getDescription() );
        assertNotNull( fixture.findUnitByName( "Changed name" ).getLanguage() );
        assertEquals( "se", fixture.findUnitByName( "Changed name" ).getLanguage().getCode() );
        assertTrue( fixture.findUnitByName( "Changed name" ).getContentTypes().contains( fixture.findContentTypeByName( "cty1" ) ) );
        assertTrue( fixture.findUnitByName( "Changed name" ).getContentTypes().contains( fixture.findContentTypeByName( "cty2" ) ) );

        assertNotNull( fixture.findCategoryByName( "Changed name" ) );
        assertEquals( false, fixture.findCategoryByName( "Changed name" ).getAutoMakeAvailableAsBoolean() );
        assertEquals( "Changed description", fixture.findCategoryByName( "Changed name" ).getDescription() );
        assertNotNull( fixture.findCategoryByName( "Changed name" ).getContentType() );
        assertEquals( "cty2", fixture.findCategoryByName( "Changed name" ).getContentType().getName() );
    }

    @Test
    public void synchronizeCategoryACLCommand()
    {
        // setup
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser", "My other user", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser2", "My other user 2", UserType.NORMAL, "testuserstore" );

        fixture.save(
            factory.createContentType( "cty1", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );

        StoreNewCategoryCommand storeNewCategoryCommand = createStoreNewCategoryCommand( "MyTopCategory", "cty1", null );
        addCategoryAC( "MyUser", "read, create, admin_browse, approve, administrate ", storeNewCategoryCommand );
        addCategoryAC( "MyOtherUser2", "read", storeNewCategoryCommand );

        categoryService.storeNewCategory( storeNewCategoryCommand );

        fixture.flushAndClearHibernateSession();

        // exercise
        List<CategoryAccessControl> accessControlList = Lists.newArrayList();
        addCategoryAC( "MyUser", "read, admin_browse ", accessControlList );
        addCategoryAC( "MyOtherUser", "read, create, admin_browse, approve ", accessControlList );

        SynchronizeCategoryACLCommand command = new SynchronizeCategoryACLCommand();
        command.setUpdater( fixture.findUserByName( "MyUser" ).getKey() );
        command.addCategory( fixture.findCategoryByName( "MyTopCategory" ).getKey() );
        command.addAccessControlList( accessControlList );
        categoryService.synchronizeCategoryACL_withoutRequiresNewPropagation_for_test_only( command );

        fixture.flushAndClearHibernateSession();

        // verify access control for myUser have changed
        assertCategoryAC( "MyUser", "read, admin_browse", "MyTopCategory" );

        // verify access control for MyOtherUser is added
        assertCategoryAC( "MyOtherUser", "read, admin_browse, create, approve", "MyTopCategory" );

        // verify access to MyOtherUser2 is removed
        assertCategoryAC( "MyOtherUser2", "", "MyTopCategory" );
        assertFalse(
            fixture.findCategoryByName( "MyTopCategory" ).hasAccessForGroup( fixture.findUserByName( "MyOtherUser2" ).getUserGroupKey() ) );
    }

    @Test
    public void synchronizeCategoryACLCommand_executeInBatches()
    {
        // setup
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser", "My other user", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser2", "My other user 2", UserType.NORMAL, "testuserstore" );

        fixture.save(
            factory.createContentType( "cty1", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );

        StoreNewCategoryCommand storeNewCategoryCommand = createStoreNewCategoryCommand( "MyTopCategory", "cty1", null );
        addCategoryAC( "MyUser", "read, create, admin_browse, approve, administrate", storeNewCategoryCommand );
        addCategoryAC( "MyOtherUser2", "read", storeNewCategoryCommand );

        categoryService.storeNewCategory( storeNewCategoryCommand );

        // setup: create content in category MyTopCategory
        for ( int i = 1; i <= 10; i++ )
        {
            CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "cty1" ).getContentTypeConfig() );
            contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "person_" + i ) );
            CreateContentCommand createContentCommand = createCreateContentCommand( "person_" + i, "MyTopCategory", contentData );
            contentService.createContent( createContentCommand );
        }

        fixture.flushAndClearHibernateSession();

        // setup: verify
        assertCategoryAC( "MyUser", "read, admin_browse, create, approve, administrate", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser2", "read", "MyTopCategory" );
        for ( int i = 1; i <= 10; i++ )
        {
            String contentName = "person_" + i;
            assertContentAC( "MyUser", "read, update, delete", contentName );
            assertContentAC( "MyOtherUser2", "read", contentName );
        }

        // exercise
        List<CategoryAccessControl> accessControlList = Lists.newArrayList();
        addCategoryAC( "MyUser", "read, admin_browse ", accessControlList );
        addCategoryAC( "MyOtherUser", "read, create, admin_browse, approve, administrate", accessControlList );

        SynchronizeCategoryACLCommand command = new SynchronizeCategoryACLCommand();
        command.setUpdater( fixture.findUserByName( "MyUser" ).getKey() );
        command.addCategory( fixture.findCategoryByName( "MyTopCategory" ).getKey() );
        command.addAccessControlList( accessControlList );
        command.includeContent();
        command.executeInBatches( categoryService, contentDao );

        fixture.flushAndClearHibernateSession();

        // verify
        assertCategoryAC( "MyUser", "read, admin_browse", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser", "read, create, admin_browse, approve, administrate", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser2", "", "MyTopCategory" );

        for ( int i = 1; i <= 10; i++ )
        {
            String contentName = "person_" + i;
            assertContentAC( "MyUser", "read", contentName );
            assertContentAC( "MyOtherUser", "read, update, delete", contentName );
            assertContentAC( "MyOtherUser2", "", contentName );
        }
    }

    @Test
    public void modifyCategoryACLCommand_executeInBatches()
    {
        // setup
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser", "My other user", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "MyOtherUser2", "My other user 2", UserType.NORMAL, "testuserstore" );

        fixture.save(
            factory.createContentType( "cty1", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );

        StoreNewCategoryCommand storeNewCategoryCommand = createStoreNewCategoryCommand( "MyTopCategory", "cty1", null );
        addCategoryAC( "MyUser", "read, create, admin_browse, approve, administrate", storeNewCategoryCommand );
        addCategoryAC( "MyOtherUser2", "read", storeNewCategoryCommand );

        categoryService.storeNewCategory( storeNewCategoryCommand );

        // setup: create content in category MyTopCategory
        for ( int i = 1; i <= 10; i++ )
        {
            CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "cty1" ).getContentTypeConfig() );
            contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "person_" + i ) );
            CreateContentCommand createContentCommand = createCreateContentCommand( "person_" + i, "MyTopCategory", contentData );
            contentService.createContent( createContentCommand );
        }

        fixture.flushAndClearHibernateSession();

        // setup: verify
        assertCategoryAC( "MyUser", "read, admin_browse, create, approve, administrate", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser2", "read", "MyTopCategory" );
        for ( int i = 1; i <= 10; i++ )
        {
            String contentName = "person_" + i;
            assertContentAC( "MyUser", "read, update, delete", contentName );
            assertContentAC( "MyOtherUser2", "read", contentName );
        }

        System.out.println( "MyOtherUser group key:" + fixture.findUserByName( "MyOtherUser" ).getUserGroupKey() );
        // exercise
        ModifyCategoryACLCommand command = new ModifyCategoryACLCommand();
        command.setUpdater( fixture.findUserByName( "MyUser" ).getKey() );
        command.addCategory( fixture.findCategoryByName( "MyTopCategory" ).getKey() );
        command.addToBeModified( createCategoryAC( "MyUser", "read, admin_browse" ) );
        command.addToBeAdded( createCategoryAC( "MyOtherUser", "read, admin_browse, create, approve, administrate" ) );
        command.addToBeRemoved( fixture.findUserByName( "MyOtherUser2" ).getUserGroupKey() );
        command.includeContent();
        command.executeInBatches( categoryService, contentDao );

        fixture.flushAndClearHibernateSession();

        // verify
        assertCategoryAC( "MyUser", "read, admin_browse", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser", "read, create, admin_browse, approve, administrate", "MyTopCategory" );
        assertCategoryAC( "MyOtherUser2", "", "MyTopCategory" );

        for ( int i = 1; i <= 10; i++ )
        {
            String contentName = "person_" + i;
            assertContentAC( "MyUser", "read", contentName );
            assertContentAC( "MyOtherUser", "read, update, delete", contentName );
            assertContentAC( "MyOtherUser2", "", contentName );
        }
    }

    private CreateContentCommand createCreateContentCommand( String contentName, String categoryName, ContentData contentData )
    {
        CreateContentCommand command = new CreateContentCommand();
        command.setCreator( fixture.findUserByName( "admin" ).getKey() );
        command.setStatus( ContentStatus.APPROVED );
        command.setContentName( contentName );
        command.setCategory( fixture.findCategoryByName( categoryName ).getKey() );
        command.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
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

    private void addCategoryAC( String userName, String accesses, StoreNewCategoryCommand command )
    {
        command.addAccessRight( factory.createCategoryAccessControl( fixture.findUserByName( userName ).getUserGroup(), accesses ) );
    }

    private void addCategoryAC( String userName, String accesses, List<CategoryAccessControl> list )
    {
        list.add( factory.createCategoryAccessControl( fixture.findUserByName( userName ).getUserGroup(), accesses ) );
    }

    private CategoryAccessControl createCategoryAC( String userName, String accesses )
    {
        return factory.createCategoryAccessControl( fixture.findUserByName( userName ).getUserGroup(), accesses );
    }

    private void assertCategoryAC( String userName, String accesses, String categoryName )
    {
        CategoryEntity category = fixture.findCategoryByName( categoryName );
        assertNotNull( "Expected category with name: " + categoryName );

        UserEntity user = fixture.findUserByName( userName );
        assertNotNull( "Expected user with name: " + userName );
        GroupKey group = user.getUserGroup().getGroupKey();

        boolean expectRead = accesses.contains( CategoryAccessType.READ.toString().toLowerCase() );
        boolean expectAdminBrowse = accesses.contains( CategoryAccessType.ADMIN_BROWSE.toString().toLowerCase() );
        boolean expectCreate = accesses.contains( CategoryAccessType.CREATE.toString().toLowerCase() );
        boolean expectApprove = accesses.contains( CategoryAccessType.APPROVE.toString().toLowerCase() );
        boolean expectAdministrate = accesses.contains( CategoryAccessType.ADMINISTRATE.toString().toLowerCase() );

        assertEquals( "Unexpected read access for user [" + userName + "] on category [" + categoryName + "]", expectRead,
                      category.hasAccessRightSet( group, CategoryAccessType.READ ) );
        assertEquals( "Unexpected admin_browse access for user [" + userName + "] on category [" + categoryName + "]", expectAdminBrowse,
                      category.hasAccessRightSet( group, CategoryAccessType.ADMIN_BROWSE ) );
        assertEquals( "Unexpected create access for user [" + userName + "] on category [" + categoryName + "]", expectCreate,
                      category.hasAccessRightSet( group, CategoryAccessType.CREATE ) );
        assertEquals( "Unexpected approve access for user [" + userName + "] on category [" + categoryName + "]", expectApprove,
                      category.hasAccessRightSet( group, CategoryAccessType.APPROVE ) );
        assertEquals( "Unexpected administrate access for user [" + userName + "] on category [" + categoryName + "]", expectAdministrate,
                      category.hasAccessRightSet( group, CategoryAccessType.ADMINISTRATE ) );
    }

    private void assertContentAC( String userName, String accesses, String contentName )
    {
        ContentEntity content = fixture.findContentByName( contentName );
        assertNotNull( "Expected content with name: " + contentName );

        UserEntity user = fixture.findUserByName( userName );
        assertNotNull( "Expected user with name: " + userName );
        GroupKey group = user.getUserGroup().getGroupKey();

        boolean expectedRead = accesses.contains( ContentAccessType.READ.toString().toLowerCase() );
        boolean expectedUpdate = accesses.contains( ContentAccessType.UPDATE.toString().toLowerCase() );
        boolean expectedDelete = accesses.contains( ContentAccessType.DELETE.toString().toLowerCase() );

        assertEquals( "Unexpected read access for user [" + userName + "] on content [" + contentName + "]", expectedRead,
                      content.hasAccessRightSet( group, ContentAccessType.READ ) );
        assertEquals( "Unexpected update access for user [" + userName + "] on content [" + contentName + "]", expectedUpdate,
                      content.hasAccessRightSet( group, ContentAccessType.UPDATE ) );
        assertEquals( "Unexpected delete access for user [" + userName + "] on content [" + contentName + "]", expectedDelete,
                      content.hasAccessRightSet( group, ContentAccessType.DELETE ) );
    }
}
