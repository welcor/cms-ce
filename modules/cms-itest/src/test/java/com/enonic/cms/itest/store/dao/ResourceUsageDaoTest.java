/*
 * Copyright 2000-2012 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.store.dao;

import java.util.Date;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ResourceUsageDao;

import static org.junit.Assert.*;

public class ResourceUsageDaoTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private ResourceUsageDao resourceUsageDao;

    @Autowired
    private ContentService contentService;

    @Before
    public void setUp()
        throws Exception
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSesssion();

        // Create an article conent type that will be used in the section:
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "article", "heading" );
        ctyconf.startBlock( "intro" );
        ctyconf.addInput( "heading", "text", "contentdata/intro/heading", "heading", true );
        ctyconf.addInput( "teaser", "text", "contentdata/intro/teaser", "teaser" );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        final ContentTypeEntity contentTypeEntity =
            factory.createContentType( "MenuItem", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes );
        contentTypeEntity.setDefaultCssKey( ResourceKey.parse( "CONTENT_TYPE_CSS" ) );
        fixture.save( contentTypeEntity );

        fixture.flushAndClearHibernateSesssion();

        // Create users that have all and no rights to work with the sections.
        fixture.createAndStoreNormalUserWithUserGroup( "aru", "All rights user", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "nru", "No rights user", "testuserstore" );

        fixture.flushAndClearHibernateSesssion();

        // Create a unit and a category in the archive to store the articles in, including access rights on the category.
        fixture.save( factory.createUnit( "Archive" ) );
        final CategoryEntity category = factory.createCategory( "Articles", null, "article", "Archive", "aru", "aru" );
        category.setContentType( fixture.findContentTypeByName( "MenuItem" ) );
        fixture.save( category );
        fixture.save( factory.createCategoryAccessForUser( "Articles", "aru", "read, admin_browse, create, delete, approve" ) );
        fixture.save( factory.createCategoryAccessForUser( "Articles", "nru", "read" ) );
    }


    @Test
    public void testGetUsageCountMap()
        throws Exception
    {
        // only CONTENT_TYPE_CSS , that is not associated with sites
        assertEquals( 1, resourceUsageDao.getUsageCountMap().size() );

        createSite( 1 );
        assertEquals( 9, resourceUsageDao.getUsageCountMap().size() );
        assertEquals( 1, resourceUsageDao.getUsageCountMap().get( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) ).intValue() );

        createSite( 2 );
        assertEquals( 9, resourceUsageDao.getUsageCountMap().size() );
        assertEquals( 2, resourceUsageDao.getUsageCountMap().get( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) ).intValue() );
    }

    @Test
    public void testGetUsedBy()
        throws Exception
    {
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_BORDER" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_TYPE_CSS" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_STYLE" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_CSS" ) ).size() );

        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_CSS" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_LOCALIZATION_RESOURCE" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEVICE_CLASS_RESOLVER" ) ).size() );
        assertEquals( 0, resourceUsageDao.getUsedBy( ResourceKey.parse( "LOCALE_RESOLVER" ) ).size() );

        createSite( 1 );

        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_BORDER" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_TYPE_CSS" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_STYLE" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_CSS" ) ).size() );

        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_CSS" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_LOCALIZATION_RESOURCE" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEVICE_CLASS_RESOLVER" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "LOCALE_RESOLVER" ) ).size() );

        createSite( 2 );

        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_OBJECT_BORDER" ) ).size() );
        assertEquals( 1, resourceUsageDao.getUsedBy( ResourceKey.parse( "CONTENT_TYPE_CSS" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_STYLE" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "PAGE_TEMPLATE_CSS" ) ).size() );

        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_CSS" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEFAULT_LOCALIZATION_RESOURCE" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "DEVICE_CLASS_RESOLVER" ) ).size() );
        assertEquals( 2, resourceUsageDao.getUsedBy( ResourceKey.parse( "LOCALE_RESOLVER" ) ).size() );
    }

    private void createSite( int n )
    {
        final Document xmlData = XMLDocumentFactory.create(
            "<menudata>" +
                "<defaultcss key=\"DEFAULT_CSS\"/>" +
                "<default-localization-resource>DEFAULT_LOCALIZATION_RESOURCE</default-localization-resource>" +
                "<device-class-resolver>DEVICE_CLASS_RESOLVER</device-class-resolver>" +
                "<locale-resolver>LOCALE_RESOLVER</locale-resolver>" +
            "</menudata>"
        ).getAsJDOMDocument();

        // Create a site and a section page for testing working with sections.
        final SiteEntity site = factory.createSite( "The Newspaper " + n, new Date(), xmlData, "en" );
        fixture.save( site );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( createSection( "News " + n, "The Newspaper " + n, true ) );
        fixture.save( factory.createMenuItemAccess( fixture.findMenuItemByName( "News " + n ), fixture.findUserByName( "aru" ),
                                                    "read, create, update, delete, add, publish" ) );
        createContent( "c-1", "Articles" );
        final PageTemplateEntity pageTemplateEntity =
            factory.createPageTemplate( "my-page-template", PageTemplateType.CONTENT, "The Newspaper " + n,
                                        ResourceKey.parse( "PAGE_TEMPLATE_CSS" ) );
        pageTemplateEntity.setCssKey( ResourceKey.parse( "PAGE_TEMPLATE_STYLE" ) );
        fixture.save( pageTemplateEntity );

        fixture.flushAndClearHibernateSesssion();

        final PortletEntity portlet = createPortlet( n, "name " + n );
        portlet.setSite( site );
        portlet.setStyleKey( ResourceKey.parse( "CONTENT_OBJECT_STYLE" ) );
        portlet.setBorderKey( ResourceKey.parse( "CONTENT_OBJECT_BORDER" ) );
        fixture.save( portlet );

        fixture.flushAndClearHibernateSesssion();
    }

    private MenuItemEntity createSection( String name, String siteName, boolean isOrdered )
    {
        return factory.createSectionMenuItem( name, 0, null, name, siteName, "aru", "aru", "en", null, null, isOrdered,
                                              null, false, null );
    }


    private ContentKey createContent( String contentName, String categoryName )
    {
        final UserKey user = fixture.findUserByName( "aru" ).getKey();
        CreateContentCommand createCommand = createCreateContentCommand( contentName, categoryName, ContentStatus.APPROVED, user );
        return contentService.createContent( createCommand );
    }


    private CreateContentCommand createCreateContentCommand( String contentName, String categoryName, ContentStatus status,
                                                             UserKey creator )
    {
        ContentTypeEntity contentType = fixture.findContentTypeByName( "MenuItem" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        TextDataEntryConfig headingConfig = new TextDataEntryConfig( "heading", true, "Tittel", "contentdata/intro/heading" );
        contentData.add( new TextDataEntry( headingConfig, "test title" ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( creator );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( status );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( contentName );

        return createContentCommand;
    }

    private PortletEntity createPortlet( int key, String name )
    {
        PortletEntity portlet = new PortletEntity();
        portlet.setKey( key );
        portlet.setName( name );
        portlet.setCreated( new Date() );
        portlet.setRunAs( RunAsType.PERSONALIZED );
        return portlet;
    }
}
