package com.enonic.cms.itest.content;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.core.content.category.CategoryAccessRights;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.CreateCategoryAccessException;
import com.enonic.cms.core.content.category.DeleteCategoryCommand;
import com.enonic.cms.core.content.category.MoveCategoryCommand;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CategoryServiceImplTest
    extends AbstractSpringTest
{
    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected GroupDao groupDao;

    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;


    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "MyUser", "MyUser fullname", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "NoRightsUser", "NoRightsUser fullname", UserType.NORMAL, "testuserstore" );

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
    }

    @Test
    public void move_category()
    {
        // setup
        fixture.save( factory.createUnit( "FirstUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory1", null, null, "FirstUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory1", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        fixture.save( factory.createUnit( "SecondUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory2", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );
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
        CategoryKey article3Key = categoryService.storeNewCategory( command );

        assertEquals( 8, categoryDao.findAll( 0, 100 ).getList().size() );

        // verify state before moving (unit = 2; subCat3 doesn't contain articleX categories)
        CategoryEntity cat = categoryDao.findByKey( subCat3Key );
        CategoryEntity article1 = categoryDao.findByKey( article1Key );
        assertFalse( cat.getChildren().contains( article1 ) );
        assertEquals( 2, article1.getUnit().getKey().toInt() );
        for ( CategoryEntity categoryEntity : article1.getChildren() )
        {
            assertEquals( 2, categoryEntity.getUnit().getKey().toInt() );
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
        assertEquals( 1, child.getUnit().getKey().toInt() );
        for ( CategoryEntity categoryEntity : child.getChildren() )
        {
            assertEquals( 1, categoryEntity.getUnit().getKey().toInt() );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void move_category_to_subcategory()
    {
        // setup
        fixture.save( factory.createUnit( "FirstUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory1", null, null, "FirstUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory1", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        fixture.save( factory.createUnit( "SecondUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory2", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );
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
        CategoryKey article3Key = categoryService.storeNewCategory( command );

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
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory1", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );

        fixture.save( factory.createUnit( "SecondUnit", "by" ) );
        fixture.save( factory.createCategory( "ParentCategory2", null, null, "SecondUnit", "MyUser", "MyUser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ParentCategory2", "MyUser",
                                                           "administrate, read, create, approve, admin_browse" ) );
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
        CategoryKey article3Key = categoryService.storeNewCategory( command );

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
        fixture.flushAndClearHibernateSesssion();

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
        fixture.flushAndClearHibernateSesssion();

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
        fixture.flushAndClearHibernateSesssion();

        // exercise
        StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setName( "Test category" );
        command.setCreator( fixture.findUserByName( "MyUser" ).getKey() );
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
        fixture.flushAndClearHibernateSesssion();

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

        CategoryAccessRights accessRights = new CategoryAccessRights();
        accessRights.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessRights.setAdminAccess( false );
        accessRights.setAdminBrowseAccess( false );
        accessRights.setCreateAccess( false );
        accessRights.setPublishAccess( true );
        accessRights.setReadAccess( true );
        command.addAccessRight( accessRights );

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
        fixture.flushAndClearHibernateSesssion();

        // create top category with defined access rights
        StoreNewCategoryCommand topCategoryCommand = new StoreNewCategoryCommand();
        topCategoryCommand.setName( "Test category" );
        topCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        topCategoryCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );

        CategoryAccessRights accessRights = new CategoryAccessRights();
        accessRights.setGroupKey( groupDao.findBuiltInDeveloper().getGroupKey() );
        accessRights.setAdminAccess( false );
        accessRights.setAdminBrowseAccess( false );
        accessRights.setCreateAccess( false );
        accessRights.setPublishAccess( true );
        accessRights.setReadAccess( true );
        topCategoryCommand.addAccessRight( accessRights );

        CategoryKey topCategoryKey = categoryService.storeNewCategory( topCategoryCommand );

        fixture.flushAndClearHibernateSesssion();

        //create child category under the top category
        StoreNewCategoryCommand childCategoryCommand = new StoreNewCategoryCommand();
        childCategoryCommand.setName( "Child category" );
        childCategoryCommand.setCreator( fixture.findUserByName( User.ROOT_UID ).getKey() );
        childCategoryCommand.setParentCategory( topCategoryKey );

        CategoryKey childCategoryKey = categoryService.storeNewCategory( childCategoryCommand );

        fixture.flushAndClearHibernateSesssion();

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

        fixture.flushAndClearHibernateSesssion();

        // exercise
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategoryKey( fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).getKey() );
        categoryService.deleteCategory( command );

        fixture.flushAndClearHibernateSesssion();

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

        fixture.flushAndClearHibernateSesssion();

        // exercise
        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( fixture.findUserByName( "MyUser" ).getKey() );
        command.setCategoryKey( fixture.findCategoryByName( "MyContentArchiveToBeDeleted" ).getKey() );
        command.setRecursive( true );
        categoryService.deleteCategory( command );

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( true, fixture.findCategoryByName( "MySubCategory" ).isDeleted() );
    }

}
