package com.enonic.cms.itest.content;


import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessRights;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.category.CreateCategoryAccessException;
import com.enonic.cms.core.content.category.DeleteCategoryCommand;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
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
