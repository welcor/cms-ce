package com.enonic.cms.itest.search;

import java.util.Calendar;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateAssignmentCommand;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.query.OpenContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.web.portal.SiteRedirectHelper;
import com.enonic.cms.web.portal.services.ContentServicesProcessor;
import com.enonic.cms.web.portal.services.UserServicesRedirectUrlResolver;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContentIndexServiceImpl_indexUpdateIndexTest
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
    public void updateCategoryAccess_yields_index_updateTest()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "norway_user", "testuserstore" );
        createAndSaveNormalUser( "europe_user", "testuserstore" );
        createAndSaveNormalUser( "no_access_user", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "norway_user", "create, admin_browse" );

        final CreateContentCommand createContentCommand = createCreateContentCommand( categoryName, "norway_user", ContentStatus.DRAFT );

        ContentAccessEntity norwayUser = createContentAccess( "norway_user" );
        ContentAccessEntity europeUser = createContentAccess( "europe_user" );

        createContentCommand.addContentAccessRights( Lists.newArrayList( norwayUser, europeUser ), null );

        ContentKey contentKey = contentService.createContent( createContentCommand );
        assertNotNull( contentDao.findByKey( contentKey ) );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        OpenContentQuery query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "norway_user" ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        query.setCount( 10 );
        ContentResultSet contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "europe_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        query.setCount( 10 );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 0, contentResultSet.getKeys().size() );

        // Add admin_browse for europe_user in category, update index and expect admin to get access





        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        UpdateAssignmentCommand updateAssignmentCommand = new UpdateAssignmentCommand();
        updateAssignmentCommand.setAssignmentDescription( "test" );
        updateAssignmentCommand.setAssignmentDueDate( Calendar.getInstance().getTime() );
        updateAssignmentCommand.setContentKey( contentKey );
        updateAssignmentCommand.setUpdater( fixture.findUserByName( "norway_user" ).getKey() );

        contentService.updateAssignment( updateAssignmentCommand );

        fixture.flushAndClearHibernateSesssion();
        fixture.flushIndexTransaction();

        query = new OpenContentQuery();
        query.setUser( fixture.findUserByName( "europe_user" ) );
        query.setContentKeyFilter( Lists.newArrayList( contentKey ) );
        query.setCategoryAccessTypeFilter( Lists.newArrayList( CategoryAccessType.ADMIN_BROWSE ),
                                           ContentIndexQuery.CategoryAccessTypeFilterPolicy.AND );
        query.setCount( 10 );
        contentResultSet = contentService.queryContent( query );
        assertEquals( 1, contentResultSet.getKeys().size() );


    }


}
