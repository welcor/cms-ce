/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static com.enonic.cms.itest.util.AssertTool.assertXPathEquals;
import static com.enonic.cms.itest.util.AssertTool.assertXPathNotExist;
import static org.junit.Assert.*;

public class ContentServiceImpl_deleteContentTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private ContentService contentService;

    @Before
    public void setUp()
        throws JDOMException, IOException
    {
        // setup content type
        final ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyRelatedTypes", "title" );

        ctyconf.startBlock( "MyRelatedTypes" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );

        ctyconf.addRelatedContentInput( "mySingleRelatedToBeUnmodified", "contentdata/mySingleRelatedToBeUnmodified", "My related1", false,
                                        false );
        ctyconf.addRelatedContentInput( "mySingleRelatedToBeModified", "contentdata/mySingleRelatedToBeModified", "My related2", false,
                                        false );

        ctyconf.addRelatedContentInput( "myMultipleRelatedToBeModified", "contentdata/myMultipleRelatedToBeModified", "My related3", false,
                                        true );

        ctyconf.endBlock();

        ctyconf.startBlock( "MyGroup", "contentdata/mygroup" );

        ctyconf.addRelatedContentInput( "myMultipleRelatedToBeModifiedGroup", "contentdata/myMultipleRelatedToBeModifiedGroup",
                                        "My related4", false, true );

        ctyconf.endBlock();

        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        final Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save(
            factory.createContentType( "MyRelatedTypes", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.save( factory.createUnit( "UnitForMyRelatedTypes" ) );

        fixture.save(
            factory.createCategory( "MyCategory", null, "MyRelatedTypes", "UnitForMyRelatedTypes", "testuser", "testuser", true ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, admin_browse, create, delete, approve" ) );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();
    }

    @Test
    public void testDeleteSingleRelated()
    {
        // setup some content to relate to
        final ContentKey related1a = createContentWithSingleRelated( "related-1a", null, null );
        final ContentKey related1b = createContentWithSingleRelated( "related-1b", null, null );
        final ContentKey related2a = createContentWithSingleRelated( "related-2a", related1a, related1b );
        final ContentKey related2b = createContentWithSingleRelated( "related-2b", related1a, related1b );
        createContentWithSingleRelated( "related-3a", related2a, related2b );
        createContentWithSingleRelated( "related-3b", related2a, related2b );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();

        assertEquals( 6, hibernateTemplate.find( "from ContentEntity" ).size() );

        assertEquals( 8, hibernateTemplate.find( "from RelatedContentEntity" ).size() );

        final ContentEntity contentEntity2a =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-2a'" ).get( 0 ) );

        assertXPathNotExist( "/contentdata/mySingleRelatedToBeModified/@deleted",
                             contentEntity2a.getVersions().get( 0 ).getContentDataAsJDomDocument() );

        final List refs = hibernateTemplate.find(
            "select rce from RelatedContentEntity rce, ContentEntity ce " + "where rce.key.childContentKey = ce.key " + "and ce = ? ",
            contentEntity2a );
        assertEquals( 2, refs.size() );

        contentService.deleteContent( fixture.findUserByName( "testuser" ), contentEntity2a );

        assertEquals( 4, hibernateTemplate.find( "from RelatedContentEntity" ).size() );

        final ContentEntity contentEntity3a =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-3a'" ).get( 0 ) );

        assertXPathEquals( "/contentdata/mySingleRelatedToBeModified/@deleted",
                           contentEntity3a.getVersions().get( 0 ).getContentDataAsJDomDocument(), "true" );

        // required to produce SQL for model upgrade
        final List empty = hibernateTemplate.find(
            "select rce from RelatedContentEntity rce, ContentEntity ce " + "where rce.key.childContentKey = ce.key " +
                "and ce.deleted = 1 " );
        assertEquals( 0, empty.size() );

    }

    @Test
    public void testDeleteMultipleRelated()
    {
        // setup some content to relate to
        final ContentKey related1a = createContentWithMultipleRelated( "related-1a", null, null );
        final ContentKey related1b = createContentWithMultipleRelated( "related-1b", null, null );
        final ContentKey related2a = createContentWithMultipleRelated( "related-2a", related1a, related1b );
        final ContentKey related2b = createContentWithMultipleRelated( "related-2b", related1a, related1b );
        createContentWithMultipleRelated( "related-3a", related2a, related2b );
        createContentWithMultipleRelated( "related-3b", related2a, related2b );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();

        assertEquals( 6, hibernateTemplate.find( "from ContentEntity" ).size() );

        assertEquals( 8, hibernateTemplate.find( "from RelatedContentEntity" ).size() );

        final ContentEntity contentEntity2a =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-2a'" ).get( 0 ) );

        final ContentEntity contentEntity3a2 =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-3a'" ).get( 0 ) );

        assertXPathEquals( "count(/contentdata/myMultipleRelatedToBeModified/content)",
                           contentEntity3a2.getVersions().get( 0 ).getContentDataAsJDomDocument(), "2" );

        assertXPathNotExist( "/contentdata/myMultipleRelatedToBeModified/content[1]/@deleted",
                             contentEntity3a2.getVersions().get( 0 ).getContentDataAsJDomDocument() );

        contentService.deleteContent( fixture.findUserByName( "testuser" ), contentEntity2a );

        final List refs = hibernateTemplate.find(
            "select rce from RelatedContentEntity rce, ContentEntity ce " + "where rce.key.childContentKey = ce.key " + "and ce = ? ",
            contentEntity2a );
        assertEquals( 0, refs.size() );

        final ContentEntity contentEntity3a1 =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-3a'" ).get( 0 ) );

        assertXPathEquals( "count(/contentdata/myMultipleRelatedToBeModified/content)",
                           contentEntity3a1.getVersions().get( 0 ).getContentDataAsJDomDocument(), "2" );
        ///contentdata/myMultipleRelatedToBeModified/content/@deleted
        assertXPathEquals( "/contentdata/myMultipleRelatedToBeModified/content[1]/@deleted",
                           contentEntity3a1.getVersions().get( 0 ).getContentDataAsJDomDocument(), "true" );

        assertEquals( 4, hibernateTemplate.find( "from RelatedContentEntity" ).size() );
    }


    @Test
    public void testDeleteMultipleRelatedInGroup()
    {
        // setup some content to relate to
        final ContentKey related1a = createContentWithMultipleRelatedInGroup( "related-1a", null, null );
        final ContentKey related1b = createContentWithMultipleRelatedInGroup( "related-1b", null, null );
        final ContentKey related2a = createContentWithMultipleRelatedInGroup( "related-2a", related1a, related1b );
        final ContentKey related2b = createContentWithMultipleRelatedInGroup( "related-2b", related1a, related1b );
        createContentWithMultipleRelatedInGroup( "related-3a", related2a, related2b );
        createContentWithMultipleRelatedInGroup( "related-3b", related2a, related2b );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();

        assertEquals( 6, hibernateTemplate.find( "from ContentEntity" ).size() );

        assertEquals( 8, hibernateTemplate.find( "from RelatedContentEntity" ).size() );

        final ContentEntity contentEntity2a =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-2a'" ).get( 0 ) );

        final ContentEntity contentEntity3a2 =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-3a'" ).get( 0 ) );

        assertXPathEquals( "count(/contentdata/mygroup/contentdata/myMultipleRelatedToBeModifiedGroup/content)",
                           contentEntity3a2.getVersions().get( 0 ).getContentDataAsJDomDocument(), "2" );

        assertXPathNotExist( "/contentdata/mygroup/contentdata/myMultipleRelatedToBeModifiedGroup/content[1]/@deleted",
                             contentEntity3a2.getVersions().get( 0 ).getContentDataAsJDomDocument() );

        contentService.deleteContent( fixture.findUserByName( "testuser" ), contentEntity2a );

        final List refs = hibernateTemplate.find(
            "select rce from RelatedContentEntity rce, ContentEntity ce " + "where rce.key.childContentKey = ce.key " + "and ce = ? ",
            contentEntity2a );
        assertEquals( 0, refs.size() );

        final ContentEntity contentEntity3a1 =
            ContentEntity.class.cast( hibernateTemplate.find( "from ContentEntity where name='testcontentrelated-related-3a'" ).get( 0 ) );
        assertXPathEquals( "count(/contentdata/mygroup/contentdata/myMultipleRelatedToBeModifiedGroup/content)",
                           contentEntity3a1.getVersions().get( 0 ).getContentDataAsJDomDocument(), "2" );

        assertXPathEquals( "/contentdata/mygroup/contentdata/myMultipleRelatedToBeModifiedGroup/content[1]/@deleted",
                           contentEntity3a1.getVersions().get( 0 ).getContentDataAsJDomDocument(), "true" );

        assertEquals( 4, hibernateTemplate.find( "from RelatedContentEntity" ).size() );
    }

    private ContentKey createContentWithSingleRelated( String title, ContentKey related1, ContentKey related2 )
    {
        final CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        createCommand.setCategory( fixture.findCategoryByName( "MyCategory" ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setStatus( ContentStatus.APPROVED );
        createCommand.setPriority( 0 );
        createCommand.setContentName( "testcontentrelated" + "-" + title );

        final CustomContentData contentData =
            new CustomContentData( fixture.findContentTypeByName( "MyRelatedTypes" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "mySingleRelatedToBeModified" ), related1 ) );
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "mySingleRelatedToBeUnmodified" ), related2 ) );
        }

        createCommand.setContentData( contentData );
        return contentService.createContent( createCommand );
    }

    private ContentKey createContentWithMultipleRelated( String title, ContentKey related1, ContentKey related2 )
    {
        final CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        createCommand.setCategory( fixture.findCategoryByName( "MyCategory" ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setStatus( ContentStatus.APPROVED );
        createCommand.setPriority( 0 );
        createCommand.setContentName( "testcontentrelated" + "-" + title );

        final CustomContentData contentData =
            new CustomContentData( fixture.findContentTypeByName( "MyRelatedTypes" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            final DataEntryConfig dataEntryConfig = contentData.getInputConfig( "myMultipleRelatedToBeModified" );
            final RelatedContentsDataEntry contentsDataEntry = new RelatedContentsDataEntry( dataEntryConfig );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related1 ) );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related2 ) );

            contentData.add( contentsDataEntry );
        }

        createCommand.setContentData( contentData );
        return contentService.createContent( createCommand );
    }

    private ContentKey createContentWithMultipleRelatedInGroup( String title, ContentKey related1, ContentKey related2 )
    {
        final CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        createCommand.setCategory( fixture.findCategoryByName( "MyCategory" ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setStatus( ContentStatus.APPROVED );
        createCommand.setPriority( 0 );
        createCommand.setContentName( "testcontentrelated" + "-" + title );

        final CustomContentData contentData =
            new CustomContentData( fixture.findContentTypeByName( "MyRelatedTypes" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );

        if ( related1 != null && related2 != null )
        {
            final GroupDataEntry groupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
            groupDataEntry.setConfig( contentData.getSetConfig( "MyGroup" ) );

            final DataEntryConfig dataEntryConfig = contentData.getInputConfig( "myMultipleRelatedToBeModifiedGroup" );
            final RelatedContentsDataEntry contentsDataEntry = new RelatedContentsDataEntry( dataEntryConfig );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related1 ) );
            contentsDataEntry.add( new RelatedContentDataEntry( dataEntryConfig, related2 ) );

            groupDataEntry.add( contentsDataEntry );

            contentData.add( groupDataEntry );
        }

        createCommand.setContentData( contentData );
        return contentService.createContent( createCommand );
    }


}
