package com.enonic.cms.itest.content;


import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.GetRelatedContentExecutor;
import com.enonic.cms.core.content.GetRelatedContentResult;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;

public class GetRelatedContentExecutorTest
    extends AbstractSpringTest
{
    @Autowired
    private DomainFixture fixture;

    private DomainFactory factory;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ContentService contentService;

    private static final DateTime BEGINNING_OF_2010 = new DateTime( 2010, 1, 1, 0, 0, 0, 0 );

    private static final DateTime NOW = new DateTime( 2010, 1, 1, 12, 0, 0, 0 );

    private static final DateTime AFTER_NOW = new DateTime( 2010, 1, 1, 13, 0, 0, 0 );


    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        MockHttpServletRequest httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContent", "title" );
        ctyconf.startBlock( "MyContent" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addRelatedContentInput( "myRelatedContent", "contentdata/myRelatedContent", "My related content", false, true );
        ctyconf.endBlock();
        Document configAsXML = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.save( factory.createContentType( "MyRelatedType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXML ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save(
            factory.createCategory( "cat-default", null, "MyRelatedType", "MyUnit", "content-creator", "content-creator", false ) );

        fixture.save( factory.createUnit( "MyOtherUnit", "en" ) );
        fixture.save( factory.createCategory( "cat-content-querier-no-read", null, "MyRelatedType", "MyOtherUnit", "content-creator",
                                              "content-creator", false ) );

        fixture.save( factory.createCategoryAccessForUser( "cat-default", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "cat-content-querier-no-read", "content-creator",
                                                           "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "cat-default", "content-querier", "read, admin_browse" ) );

        fixture.flushAndClearHibernateSession();
    }

    @Test
    public void related_parent_with_availableFrom_after_now_is_not_in_content_result()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        createContent( "mother", ContentStatus.APPROVED, "cat-default", AFTER_NOW, son );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_parent_with_availableFrom_after_now_is_in_content_result_when_includeOfflineContent_is_true()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mother = createContent( "mother", ContentStatus.APPROVED, "cat-default", AFTER_NOW, son );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        executor.includeOfflineContent( true );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father, mother ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_parent_with_status_draft_is_not_in_content_result()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        createContent( "mother", ContentStatus.DRAFT, "cat-default", son );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_parent_that_user_cannot_read_is_not_in_content_result()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mother = createContent( "mother", ContentStatus.APPROVED, "cat-content-querier-no-read", son );
        createContent( "fathers father", ContentStatus.APPROVED, "cat-default", mother );
        createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", mother );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify mother is not in content result
        assertEquals( Lists.newArrayList( father ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }


    @Test
    public void related_child_with_availableFrom_after_now_is_not_in_content_result()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-default", AFTER_NOW );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_child_with_availableFrom_after_now_is_in_content_result_when_includeOfflineContent_is_true()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-default", AFTER_NOW );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        executor.includeOfflineContent( true );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son, daughter ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_child_with_status_draft_is_not_in_content_result()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.DRAFT, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_child_that_user_cannot_read_is_not_in_content_result()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-content-querier-no-read" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_grand_parents_with_status_draft_is_not_in_related_content_result()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mother = createContent( "mother", ContentStatus.APPROVED, "cat-default", son );
        createContent( "mothers father", ContentStatus.DRAFT, "cat-default", mother );
        createContent( "mothers mother", ContentStatus.DRAFT, "cat-default", mother );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father, mother ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_grand_parents_that_user_cannot_read_is_not_in_related_content_result()
    {
        // setup
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mother = createContent( "mother", ContentStatus.APPROVED, "cat-default", son );
        createContent( "mothers father", ContentStatus.APPROVED, "cat-content-querier-no-read", mother );
        createContent( "mothers mother", ContentStatus.APPROVED, "cat-content-querier-no-read", mother );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father, mother ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother ), result.getRelatedContent().getContentKeys() );
    }


    @Test
    public void related_grand_parents_with_status_draft_is_in_related_content_result_when_includeOfflineContent_is_true()
    {
        // setup

        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default" );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mother = createContent( "mother", ContentStatus.APPROVED, "cat-default", son );
        ContentKey mothers_father = createContent( "mothers father", ContentStatus.DRAFT, "cat-default", mother );
        ContentKey mothers_mother = createContent( "mothers mother", ContentStatus.DRAFT, "cat-default", mother );
        ContentKey fathers_father = createContent( "fathers father", ContentStatus.APPROVED, "cat-default", father );
        ContentKey fathers_mother = createContent( "fathers mother", ContentStatus.APPROVED, "cat-default", father );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( son ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( -1 );
        executor.parentLevel( 1 );
        executor.includeOfflineContent( true );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( father, mother ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( fathers_father, fathers_mother, mothers_father, mothers_mother ),
                      result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_grand_children_with_status_draft_is_not_in_related_content_result()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey daughter_son = createContent( "daughter son", ContentStatus.DRAFT, "cat-default" );
        ContentKey daughter_daughter = createContent( "daughter daughter", ContentStatus.DRAFT, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-default", daughter_son, daughter_daughter );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son, daughter ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_grand_children_that_user_cannot_read_is_not_in_related_content_result()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey daughter_son = createContent( "daughter son", ContentStatus.APPROVED, "cat-content-querier-no-read" );
        ContentKey daughter_daughter = createContent( "daughter daughter", ContentStatus.APPROVED, "cat-content-querier-no-read" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-default", daughter_son, daughter_daughter );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify that daughter_son and daughter_daughter is not in related content result
        assertEquals( Lists.newArrayList( son, daughter ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter ), result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void related_grand_children_with_status_draft_is_in_related_content_result_when_includeOfflineContent_is_true()
    {
        // setup
        ContentKey son_son = createContent( "son son", ContentStatus.APPROVED, "cat-default" );
        ContentKey son_daughter = createContent( "son daughter", ContentStatus.APPROVED, "cat-default" );
        ContentKey daughter_son = createContent( "daughter son", ContentStatus.DRAFT, "cat-default" );
        ContentKey daughter_daughter = createContent( "daughter daughter", ContentStatus.DRAFT, "cat-default" );
        ContentKey son = createContent( "son", ContentStatus.APPROVED, "cat-default", son_son, son_daughter );
        ContentKey daughter = createContent( "daughter", ContentStatus.APPROVED, "cat-default", daughter_son, daughter_daughter );
        ContentKey father = createContent( "father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        executor.includeOfflineContent( true );
        GetRelatedContentResult result = executor.execute();

        // verify
        assertEquals( Lists.newArrayList( son, daughter ), result.getContent().getKeys() );
        assertEquals( Sets.newHashSet( son_son, son_daughter, daughter_son, daughter_daughter ),
                      result.getRelatedContent().getContentKeys() );
    }

    @Test
    public void parentRelation_with_positive_childrenLevel_and_positive_parentLevel()
    {
        // setup:
        ContentKey grandSon = createContent( "Grand son", ContentStatus.APPROVED, "cat-default" );

        ContentKey grandDaughter = createContent( "Grand daughter", ContentStatus.APPROVED, "cat-default" );

        ContentKey son = createContent( "Son", ContentStatus.APPROVED, "cat-default", grandSon, grandDaughter );

        ContentKey daughter = createContent( "Daughter", ContentStatus.APPROVED, "cat-default" );

        ContentKey father = createContent( "Father", ContentStatus.APPROVED, "cat-default", son, daughter );

        // exercise
        GetRelatedContentExecutor executor = new GetRelatedContentExecutor( contentService, NOW.toDate(), PreviewContext.NO_PREVIEW );
        executor.user( fixture.findUserByName( "content-querier" ) );
        executor.contentFilter( Lists.newArrayList( father ) );
        executor.count( 1000 );
        executor.orderBy( "@key" );
        executor.relation( 1 );
        executor.childrenLevel( 1 );
        GetRelatedContentResult result = executor.execute();

        // verify
        ContentResultSet contentResultSet = result.getContent();
        assertEquals( Lists.newArrayList( son, daughter ), contentResultSet.getKeys() );

        RelatedContentResultSet relatedContent = result.getRelatedContent();
        assertEquals( Sets.newHashSet( grandSon, grandDaughter ), relatedContent.getContentKeys() );
    }


    private ContentKey createContentInMyCategory( String contentTitle, ContentStatus status, ContentKey... relatedContents )
    {
        return contentService.createContent(
            createCreateContentCommand( "cat-default", createMyRelatedContentData( contentTitle, relatedContents ), "content-creator",
                                        status, BEGINNING_OF_2010 ) );
    }

    private ContentKey createContent( String contentTitle, ContentStatus status, String categoryName, ContentKey... relatedContents )
    {
        final ContentKey contentKey = contentService.createContent(
            createCreateContentCommand( categoryName, createMyRelatedContentData( contentTitle, relatedContents ), "content-creator",
                                        status, BEGINNING_OF_2010 ) );
        fixture.flushIndexTransaction();
        return contentKey;
    }

    private ContentKey createContent( String contentTitle, ContentStatus status, String categoryName, DateTime availableFrom,
                                      ContentKey... relatedContents )
    {
        final ContentKey contentKey = contentService.createContent(
            createCreateContentCommand( categoryName, createMyRelatedContentData( contentTitle, relatedContents ), "content-creator",
                                        status, availableFrom ) );
        fixture.flushIndexTransaction();
        return contentKey;
    }

    private ContentData createMyRelatedContentData( String title, ContentKey... relatedContents )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyRelatedType" ).getContentTypeConfig() );
        if ( title != null )
        {
            contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );
        }
        if ( relatedContents != null && relatedContents.length > 0 )
        {
            RelatedContentsDataEntry relatedContentsDataEntry =
                new RelatedContentsDataEntry( contentData.getInputConfig( "myRelatedContent" ) );
            for ( ContentKey relatedKey : relatedContents )
            {
                relatedContentsDataEntry.add( new RelatedContentDataEntry( contentData.getInputConfig( "myRelatedContent" ), relatedKey ) );
            }
            contentData.add( relatedContentsDataEntry );
        }
        return contentData;
    }

    private CreateContentCommand createCreateContentCommand( String categoryName, ContentData contentData, String creatorUid,
                                                             ContentStatus status, DateTime availableFrom )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( status );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setAvailableFrom( availableFrom.toDate() );
        createContentCommand.setAvailableTo( null );
        createContentCommand.setContentName( contentData.getTitle() );
        return createContentCommand;
    }
}

