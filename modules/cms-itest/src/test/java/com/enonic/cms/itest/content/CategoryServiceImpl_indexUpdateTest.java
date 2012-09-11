package com.enonic.cms.itest.content;

import java.util.List;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.category.CategoryAccessControl;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryService;
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
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.security.user.User;
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

public class CategoryServiceImpl_indexUpdateTest
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
    public void test_index_updated_for_content_in_category_when_acl_changed()
    {
        // setup
        fixture.save(
            factory.createContentType( "personContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personCtyConfigAsDocument ) );

        StoreNewCategoryCommand storeNewCategoryCommand = createStoreNewCategoryCommand( "Category", "personContentType", null );
        final CategoryKey categoryKey = categoryService.storeNewCategory( storeNewCategoryCommand );
        assertNotNull( categoryDao.findByKey( categoryKey ) );

        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "personContentType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "person" ) );
        CreateContentCommand createContentCommand = createCreateContentCommand( "MyUser", "Category", contentData );
        final ContentKey contentKey = contentService.createContent( createContentCommand );

        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        printAllIndexContent();

        // exercise

        CategoryAccessControl acl = new CategoryAccessControl();
        acl.setGroupKey( fixture.findGroupByName( "MyUser" ).getGroupKey() );
        acl.setAdminBrowseAccess( true );

        ModifyCategoryACLCommand modifyCategoryACLCommand = new ModifyCategoryACLCommand();
        modifyCategoryACLCommand.addToBeAdded( acl );
        modifyCategoryACLCommand.includeContent();
        modifyCategoryACLCommand.setUpdater( fixture.findUserByName( "MyUser" ).getKey() );

        categoryService.modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( modifyCategoryACLCommand );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        printAllIndexContent();

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
}
