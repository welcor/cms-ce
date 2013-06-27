/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.datasource.service;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.portal.datasource.service.DataSourceServiceImpl;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import static com.enonic.cms.itest.util.AssertTool.assertXPathEquals;
import static com.enonic.cms.itest.util.AssertTool.assertXPathExist;


public class DatasourceServiceImpl_getIndexValuesTest
    extends AbstractSpringTest
{
    private static final DateTime DATE_TIME_2010_07_01_12_00_00_0 = new DateTime( 2010, 7, 1, 12, 0, 0, 0 );

    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    private DataSourceServiceImpl dataSourceService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    private Document personConfigAsXmlBytes;

    private MockHttpServletRequest httpRequest;

    @Before
    public void setUp()
    {

        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        fixture.save( factory.createUnit( "MyUnit", "en" ) );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type: Person
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.addRelatedContentInput( "my-relatedcontent", "contentdata/my-relatedcontent", "My relatedcontent", false, false );
        ctyconf.addRelatedContentInput( "my-relatedcontents", "contentdata/my-relatedcontents", "My relatedcontents", false, true );
        ctyconf.endBlock();
        personConfigAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.save(
            factory.createContentType( "MyPersonType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personConfigAsXmlBytes ) );

        fixture.save(
            factory.createCategory( "MyPersonCategory", null, "MyPersonType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-querier", "read, admin_browse" ) );

        // setup content type: Related
        ContentTypeConfigBuilder ctyconfMyRelated = new ContentTypeConfigBuilder( "MyRelatedType", "title" );
        ctyconfMyRelated.startBlock( "General" );
        ctyconfMyRelated.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconfMyRelated.addRelatedContentInput( "myRelatedContent", "contentdata/myRelatedContent", "My related content", false, true );
        ctyconfMyRelated.endBlock();
        Document myRelatedconfigAsXmlBytes = XMLDocumentFactory.create( ctyconfMyRelated.toString() ).getAsJDOMDocument();

        fixture.save(
            factory.createContentType( "MyRelatedType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), myRelatedconfigAsXmlBytes ) );

        fixture.save(
            factory.createCategory( "MyRelatedCategory", null, "MyRelatedType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save(
            factory.createCategoryAccessForUser( "MyRelatedCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyRelatedCategory", "content-querier", "read, admin_browse" ) );

        dataSourceService = new DataSourceServiceImpl();
        dataSourceService.setUserDao( userDao );
        dataSourceService.setContentDao( contentDao );
        dataSourceService.setContentService( contentService );
        dataSourceService.setTimeService( new MockTimeService( DATE_TIME_2010_07_01_12_00_00_0 ) );

        fixture.flushIndexTransaction();
    }

    @Test
    public void getIndexValuesOrder_ASC_Test()
    {
        ContentKey requested1Grandchild1 = createPersonContent( "Grandchild of 1" );
        ContentKey requested1Child1 = createPersonContentWithRelatedContent( "Child of 1", requested1Grandchild1 );
        ContentKey requested2Child1 = createPersonContent( "Child of 2" );
        ContentKey requested1 = createPersonContentWithRelatedContent( "Requested content 1", requested1Child1 );
        ContentKey requested2 = createPersonContentWithRelatedContent( "Requested content 2", requested2Child1 );

        // Exercise
        final String indexPath = "title";
        final String order = "ASC";
        XMLDocument resultAsXMLDocument = getIndexValues( order, indexPath );

        // verify
        Document docResult = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathExist( "/index", docResult );
        assertXPathExist( "/index/values", docResult );
        assertXPathExist( "/index/values/value", docResult );

        assertXPathEquals( "/index/@path", docResult, indexPath );
        assertXPathEquals( "/index/values/@count", docResult, 5 );
        assertXPathEquals( "/index/values/@totalcount", docResult, 5 );
        assertXPathEquals( "/index/values/@index", docResult, 0 );
        assertXPathEquals( "/index/values/value[1]/@contentkey", docResult, requested1Child1.toInt() );
        assertXPathEquals( "/index/values/value[2]/@contentkey", docResult, requested2Child1.toInt() );
        assertXPathEquals( "/index/values/value[3]/@contentkey", docResult, requested1Grandchild1.toInt() );
        assertXPathEquals( "/index/values/value[4]/@contentkey", docResult, requested1.toInt() );
        assertXPathEquals( "/index/values/value[5]/@contentkey", docResult, requested2.toInt() );

        assertXPathEquals( "/index/values/value[1]/text()", docResult, "child of 1" );
        assertXPathEquals( "/index/values/value[5]/text()", docResult, "requested content 2" );
    }

    @Test
    public void getIndexValuesOrder_DESC_Test()
    {
        ContentKey requested1Grandchild1 = createPersonContent( "Grandchild of 1" );
        ContentKey requested1Child1 = createPersonContentWithRelatedContent( "Child of 1", requested1Grandchild1 );
        ContentKey requested2Child1 = createPersonContent( "Child of 2" );
        ContentKey requested1 = createPersonContentWithRelatedContent( "Requested content 1", requested1Child1 );
        ContentKey requested2 = createPersonContentWithRelatedContent( "Requested content 2", requested2Child1 );

        // Exercise
        final String indexPath = "title";
        final String order = "DESC";
        XMLDocument resultAsXMLDocument = getIndexValues( order, indexPath );

        // verify
        Document docResult = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathExist( "/index", docResult );
        assertXPathExist( "/index/values", docResult );
        assertXPathExist( "/index/values/value", docResult );

        assertXPathEquals( "/index/@path", docResult, indexPath );
        assertXPathEquals( "/index/values/@count", docResult, 5 );
        assertXPathEquals( "/index/values/@totalcount", docResult, 5 );
        assertXPathEquals( "/index/values/@index", docResult, 0 );
        assertXPathEquals( "/index/values/value[5]/@contentkey", docResult, requested1Child1.toInt() );
        assertXPathEquals( "/index/values/value[4]/@contentkey", docResult, requested2Child1.toInt() );
        assertXPathEquals( "/index/values/value[3]/@contentkey", docResult, requested1Grandchild1.toInt() );
        assertXPathEquals( "/index/values/value[2]/@contentkey", docResult, requested1.toInt() );
        assertXPathEquals( "/index/values/value[1]/@contentkey", docResult, requested2.toInt() );

        assertXPathEquals( "/index/values/value[5]/text()", docResult, "child of 1" );
        assertXPathEquals( "/index/values/value[1]/text()", docResult, "requested content 2" );
    }

    private XMLDocument getIndexValues( final String order, final String indexPath )
    {
        final DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        final int[] categories = new int[]{fixture.findCategoryByName( "MyPersonCategory" ).getKey().toInt()};
        final boolean includeSubCategories = true;
        final int index = 0;
        final int count = 10;
        final boolean distinct = false;

        final ContentTypeEntity personContentType = fixture.findContentTypeByName( "MyPersonType" );
        final int[] contentTypes = new int[]{personContentType.getKey()};

        return dataSourceService.getIndexValues( context, indexPath, categories, includeSubCategories, contentTypes, index, count, distinct,
                                                 order );
    }

    private ContentKey createPersonContent( String name )
    {
        return createPersonContentWithRelatedContent( name, ContentStatus.APPROVED, null );
    }

    private ContentKey createPersonContentWithRelatedContent( String name, ContentKey relatedContent )
    {
        return createPersonContentWithRelatedContent( name, ContentStatus.APPROVED, relatedContent );
    }

    private ContentKey createPersonContentWithRelatedContent( String name, ContentStatus status, ContentKey relatedContent )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyPersonType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), name ) );
        if ( relatedContent != null )
        {
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "my-relatedcontent" ), relatedContent ) );
        }

        ContentKey expectedContentKey = contentService.createContent(
            createCreateContentCommand( "MyPersonCategory", "content-creator", status, contentData, new DateTime( 2010, 1, 1, 0, 0, 0, 0 ),
                                        null ) );
        fixture.flushIndexTransaction();
        return expectedContentKey;
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

}

