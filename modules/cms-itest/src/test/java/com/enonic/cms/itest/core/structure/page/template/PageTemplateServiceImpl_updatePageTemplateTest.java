package com.enonic.cms.itest.core.structure.page.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import junit.framework.Assert;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.AdminSecurityHolder;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentTypeFilterEntity;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.template.CreatePageTemplateCommand;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletKey;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateService;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.structure.page.template.UpdatePageTemplateCommand;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;

import static org.junit.Assert.*;

@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class PageTemplateServiceImpl_updatePageTemplateTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private PageTemplateService pageTemplateService;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        loginUserInAdmin( fixture.findUserByName( "testuser" ).getKey() );
        loginUserInPortal( fixture.findUserByName( "testuser" ).getKey() );

        final SiteEntity site = factory.createSite( "The Newspaper", new Date(), null, "en" );
        fixture.save( site );

        MenuItemEntity section = createSection( "Section#1", "The Newspaper", "admin", false );
        section.setKey( new MenuItemKey( 4040 ) );
        fixture.save( section );

        section = createSection( "Section#2", "The Newspaper", "admin", false );
        section.setKey( new MenuItemKey( 4041 ) );
        fixture.save( section );

        MenuItemEntity page = createPage( "Page#3", null, "The Newspaper" );
        page.setKey( new MenuItemKey( 3030 ) );
        fixture.save( page );

        fixture.flushAndClearHibernateSession();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Skole", "tittel" );
        ctyconf.startBlock( "Skole" );
        ctyconf.addInput( "tittel", "text", "contentdata/tittel", "Tittel", true );
        ctyconf.endBlock();
        ctyconf.startBlock( "Elever", "contentdata/elever" );
        ctyconf.addInput( "elev-navn", "text", "navn", "Navn" );
        ctyconf.addInput( "elev-karakter", "text", "karakter", "Karakter" );
        ctyconf.endBlock();
        ctyconf.startBlock( "Laerere", "contentdata/laerere" );
        ctyconf.addInput( "laerer-navn", "text", "navn", "Navn" );
        ctyconf.addInput( "laerer-karakter", "text", "karakter", "Karakter" );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        ContentTypeEntity contenType = factory.createContentType( "article", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                                  configAsXmlBytes );
        contenType.setKey( 1001 );
        fixture.save( contenType );

        contenType = factory.createContentType( "document", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes );
        contenType.setKey( 1002 );
        fixture.save( contenType );

        contenType = factory.createContentType( "person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes );
        contenType.setKey( 1003 );
        fixture.save( contenType );

        final PortletEntity portlet = createPortlet( 1, "Article-Show" );
        portlet.setSite( site );
        portlet.setStyleKey( ResourceKey.from( "CONTENT_OBJECT_STYLE" ) );
        portlet.setBorderKey( ResourceKey.from( "CONTENT_OBJECT_BORDER" ) );
        fixture.save( portlet );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();
    }

    @Test
    public void update_page_template_with_relations()
    {
        SiteEntity site = fixture.findSiteByName( "The Newspaper" );

        final String xmdData = "<pagetemplate menukey=\"" + site.getKey().toInt() + "\" runAs=\"DEFAULT_USER\" type=\"content\">" +
            "<css stylesheetkey=\"/sites/advanced/dav/_public/sites/advanced/all.css\"/>" +
            "<name>Bublik</name>" +
            "<description>video subject</description>" +
            "<stylesheet stylesheetkey=\"/sites/advanced/dav/sites/advanced/page.xsl\"/>" +
            "<pagetemplateparameters>" +
            "   <pagetemplateparameter key=\"\" multiple=\"0\" override=\"0\"><name>center</name><separator>&lt;br /&gt;</separator></pagetemplateparameter>" +
            "   <pagetemplateparameter key=\"\" multiple=\"0\" override=\"0\"><name>east</name><separator>&lt;br /&gt;</separator></pagetemplateparameter>" +
            "   <pagetemplateparameter key=\"\" multiple=\"0\" override=\"0\"><name>north</name><separator>&lt;br /&gt;</separator></pagetemplateparameter>" +
            "   <pagetemplateparameter key=\"\" multiple=\"0\" override=\"0\"><name>south</name><separator>&lt;br /&gt;</separator></pagetemplateparameter>" +
            "   <pagetemplateparameter key=\"\" multiple=\"0\" override=\"0\"><name>west</name><separator>&lt;br /&gt;</separator></pagetemplateparameter>" +
            "</pagetemplateparameters>" +
            "<contentobjects>" +
            "   <contentobject conobjkey=\"1\" pagetemplatekey=\"\" parameterkey=\"_0\">" +
            "       <order>0</order>" +
            "       <name>Article-Show</name>" +
            "   </contentobject>" +
            "</contentobjects><pagetemplatedata><datasources>\n" +
            "  <datasource name=\"getPreferences\">\n" +
            "    <parameter name=\"scope\">*</parameter>\n" +
            "    <parameter name=\"keyPattern\">*</parameter>\n" +
            "  </datasource>\n" +
            "</datasources><document mode=\"xhtml\"/></pagetemplatedata>" +
            "   <contenttypes>" +
            "       <contenttype key=\"1001\"/>" +
            "       <contenttype key=\"1002\"/>" +
            "   </contenttypes>" +
            "</pagetemplate>";

        CreatePageTemplateCommand command = new CreatePageTemplateCommand( xmdData );
        pageTemplateService.createPageTemplate( command );
        fixture.flushAndClearHibernateSession();

        PageTemplateEntity persisted = pageTemplateDao.findByName( "Bublik" ).get( 0 );

        assertEquals( "Bublik", persisted.getName() );
        assertEquals( "video subject", persisted.getDescription() );
        assertEquals( "<pagetemplatedata><datasources>\n" +
                          "  <datasource name=\"getPreferences\">\n" +
                          "    <parameter name=\"scope\">*</parameter>\n" +
                          "    <parameter name=\"keyPattern\">*</parameter>\n" +
                          "  </datasource>\n" +
                          "</datasources><document mode=\"xhtml\"/></pagetemplatedata>", persisted.getXmlData().getDocumentAsString() );

        assertEquals( ResourceKey.from( "/sites/advanced/dav/sites/advanced/page.xsl" ), persisted.getStyleKey() );
        assertEquals( ResourceKey.from( "/sites/advanced/dav/_public/sites/advanced/all.css" ), persisted.getCssKey() );

        assertEquals( site, persisted.getSite() );

        assertEquals( PageTemplateType.CONTENT, persisted.getType() );
        assertEquals( RunAsType.DEFAULT_USER, persisted.getRunAs() );

        Set<PageTemplateRegionEntity> regions = persisted.getPageTemplateRegions();
        assertEquals( 5, regions.size() );

        PageTemplateRegionEntity centerRegion = null;

        for ( PageTemplateRegionEntity region : regions )
        {
            if ( "center".equals( region.getName() ) )
            {
                centerRegion = region;
                break;
            }
        }

        List<PageTemplatePortletEntity> portlets = persisted.getPortlets();
        assertEquals( 1, portlets.size() );

        PageTemplatePortletEntity templatePortlet = portlets.get( 0 );
        int pteKey = persisted.getKey();

        assertEquals( new PageTemplatePortletKey( pteKey, 1 ), templatePortlet.getKey() );
        assertEquals( 0, templatePortlet.getOrder() );
        assertEquals( persisted, templatePortlet.getPageTemplate() );
        assertEquals( 1, templatePortlet.getPortlet().getKey() );
        assertEquals( "Article-Show", templatePortlet.getPortlet().getName() );
        assertEquals( centerRegion, templatePortlet.getPageTemplateRegion() );


        assertEquals( false, centerRegion.isMultiple() );
        assertEquals( false, centerRegion.isOverride() );
        assertEquals( "<br />", centerRegion.getSeparator() );
        assertEquals( persisted, centerRegion.getPageTemplate() );

        Set<PageTemplatePortletEntity> ptpEntities = centerRegion.getPortlets();
        assertEquals( 1, ptpEntities.size() );
        assertEquals( new PageTemplatePortletKey( pteKey, 1 ), ptpEntities.iterator().next().getKey() );

        assertEquals( persisted, centerRegion.getPageTemplate() );

        Set<ContentTypeEntity> contentTypes = persisted.getContentTypes();
        assertEquals( 2, contentTypes.size() );

        checkContentTypes( new String[]{"article", "document"}, contentTypes );

        PageEntity page = new PageEntity();
        page.setKey( 2020 );
        page.setTemplate( persisted );
        page.setXmlData( "</document>" );
        menuItemDao.getHibernateTemplate().save( page );
        menuItemDao.getHibernateTemplate().flush();

        MenuItemEntity menuItem = menuItemDao.findByKey( new MenuItemKey( 3030 ) );
        menuItem.setPage( page );
        fixture.save( menuItem );

        page = new PageEntity();
        page.setKey( 2021 );
        page.setTemplate( persisted );
        page.setXmlData( "</document>" );
        menuItemDao.getHibernateTemplate().save( page );
        menuItemDao.getHibernateTemplate().flush();

        menuItem = menuItemDao.findByKey( new MenuItemKey( 4040 ) );
        menuItem.setPage( page );
        fixture.save( menuItem );

        page = new PageEntity();
        page.setKey( 2022 );
        page.setTemplate( persisted );
        page.setXmlData( "</document>" );
        menuItemDao.getHibernateTemplate().save( page );
        menuItemDao.getHibernateTemplate().flush();

        menuItem = menuItemDao.findByKey( new MenuItemKey( 4041 ) );
        menuItem.setPage( page );
        fixture.save( menuItem );

        doUpdatePagetemplate();
    }

    private void doUpdatePagetemplate()
    {
        PageTemplateEntity persisted = pageTemplateDao.findByName( "Bublik" ).get( 0 );
        final int pageTemplateKey = persisted.getKey();
        Set<PageTemplateRegionEntity> regions = persisted.getPageTemplateRegions();

        int northKey = getRegionKeyByName( regions, "north" );

        SiteEntity site = fixture.findSiteByName( "The Newspaper" );

        final String updXmdData = "<pagetemplate key=\"" + pageTemplateKey + "\" menukey=\"" + site.getKey().toInt() + "\" runAs=\"INHERIT\" type=\"content\">\n" +
            "  <css stylesheetkey=\"/sites/advanced/dav/_public/sites/advanced/all.css\"/>\n" +
            "  <name>Yuppi</name>\n" +
            "  <description>qwerty</description>\n" +
            "  <stylesheet stylesheetkey=\"/sites/advanced/dav/sites/advanced/page.xsl\"/>\n" +
            "  <pagetemplateparameters>\n" +
            "    <pagetemplateparameter key=\"" + getRegionKeyByName( regions, "center" ) + "\" multiple=\"0\" override=\"0\" pagetemplatekey=\"" + pageTemplateKey + "\">\n" +
            "      <name>center</name>\n" +
            "      <separator>&lt;br /&gt;</separator>\n" +
            "    </pagetemplateparameter>\n" +
            "    <pagetemplateparameter key=\"" + getRegionKeyByName( regions, "east" ) + "\" multiple=\"0\" override=\"0\" pagetemplatekey=\"" + pageTemplateKey + "\">\n" +
            "      <name>east</name>\n" +
            "      <separator>&lt;br /&gt;</separator>\n" +
            "    </pagetemplateparameter>\n" +
            "    <pagetemplateparameter key=\"" + northKey + "\" multiple=\"0\" override=\"0\" pagetemplatekey=\"" + pageTemplateKey + "\">\n" +
            "      <name>north</name>\n" +
            "      <separator>&lt;br /&gt;</separator>\n" +
            "    </pagetemplateparameter>\n" +
            "    <pagetemplateparameter key=\"" + getRegionKeyByName( regions, "south" ) + "\" multiple=\"0\" override=\"0\" pagetemplatekey=\"" + pageTemplateKey + "\">\n" +
            "      <name>south</name>\n" +
            "      <separator>&lt;br /&gt;</separator>\n" +
            "    </pagetemplateparameter>\n" +
            "    <pagetemplateparameter key=\"" + getRegionKeyByName( regions, "west" ) + "\" multiple=\"0\" override=\"0\" pagetemplatekey=\"" + pageTemplateKey + "\">\n" +
            "      <name>west</name>\n" +
            "      <separator>&lt;br /&gt;</separator>\n" +
            "    </pagetemplateparameter>\n" +
            "  </pagetemplateparameters>\n" +
            "  <contentobjects>\n" +
            "    <contentobject conobjkey=\"1\" pagetemplatekey=\"" + pageTemplateKey + "\" parameterkey=\"" + northKey + "\">\n" +
            "      <order>0</order>\n" +
            "      <name>Article-Show</name>\n" +
            "    </contentobject>\n" +
            "  </contentobjects>\n" +
            "  <pagetemplatedata>\n" +
            "    <datasources>\n" +
            "      <datasource name=\"getContent\">\n" +
            "        <parameter name=\"contentKeys\">${select(param.key,0)}</parameter>\n" +
            "        <parameter name=\"query\"/>\n" +
            "        <parameter name=\"orderBy\"/>\n" +
            "        <parameter name=\"index\">0</parameter>\n" +
            "        <parameter name=\"count\">1</parameter>\n" +
            "        <parameter name=\"includeData\">true</parameter>\n" +
            "        <parameter name=\"childrenLevel\">0</parameter>\n" +
            "        <parameter name=\"parentLevel\">0</parameter>\n" +
            "      </datasource>\n" +
            "      <datasource name=\"getPreferences\">\n" +
            "        <parameter name=\"scope\">*</parameter>\n" +
            "        <parameter name=\"keyPattern\">*</parameter>\n" +
            "      </datasource>\n" +
            "    </datasources>\n" +
            "    <document mode=\"xhtml\"/>\n" +
            "  </pagetemplatedata>\n" +
            "  <contenttypes>\n" +
            "    <contenttype key=\"1001\"/>\n" +
            "    <contenttype key=\"1002\"/>\n" +
            "    <contenttype key=\"1003\"/>\n" +
            "  </contenttypes>\n" +
            "</pagetemplate>\n";

        UpdatePageTemplateCommand updCommand = new UpdatePageTemplateCommand( updXmdData );
        pageTemplateService.updatePageTemplate( updCommand );
        fixture.flushAndClearHibernateSession();

        persisted = pageTemplateDao.findByName( "Bublik" ).get( 0 );

        Set<ContentTypeEntity> contentTypes = persisted.getContentTypes();
        assertEquals( 3, contentTypes.size() );

        checkContentTypes( new String[]{"article", "document", "person"}, contentTypes );

        regions = persisted.getPageTemplateRegions();
        assertEquals( 5, regions.size() );

        PageTemplateRegionEntity northRegion = null;

        for ( PageTemplateRegionEntity region : regions )
        {
            if ( "north".equals( region.getName() ) )
            {
                northRegion = region;
                break;
            } else
            {
                assertEquals( 0, region.getPortlets().size() );
            }
        }

        List<PageTemplatePortletEntity> portlets = persisted.getPortlets();
        assertEquals( 1, portlets.size() );

        PageTemplatePortletEntity templatePortlet = portlets.get( 0 );
        int pteKey = persisted.getKey();

        assertEquals( new PageTemplatePortletKey( pteKey, 1 ), templatePortlet.getKey() );
        assertEquals( 0, templatePortlet.getOrder() );
        assertEquals( persisted, templatePortlet.getPageTemplate() );
        assertEquals( 1, templatePortlet.getPortlet().getKey() );
        assertEquals( "Article-Show", templatePortlet.getPortlet().getName() );
        assertEquals( northRegion, templatePortlet.getPageTemplateRegion() );
        assertEquals( templatePortlet, northRegion.getPortlets().iterator().next() );


        MenuItemEntity menuItem4040 = menuItemDao.findByKey( new MenuItemKey( 4040 ) );
        Set<ContentTypeEntity> ctys = menuItem4040.getAllowedSectionContentTypes();
        assertEquals( 3, ctys.size() );
        checkContentTypes( new String[]{"article", "document", "person"}, ctys );

        MenuItemEntity menuItem4041 = menuItemDao.findByKey( new MenuItemKey( 4041 ) );
        ctys = menuItem4041.getAllowedSectionContentTypes();
        assertEquals( 3, ctys.size() );
        checkContentTypes( new String[]{"article", "document", "person"}, ctys );

        MenuItemEntity menuItem3030 = menuItemDao.findByKey( new MenuItemKey( 3030 ) );
        ctys = menuItem3030.getAllowedSectionContentTypes();
        assertEquals( 0, ctys.size() );
    }

    private void checkContentTypes( final String[] expected, final Collection<ContentTypeEntity> contentTypes )
    {
        List<String> names = new ArrayList<String>( contentTypes.size() );
        for ( ContentTypeEntity contentType : contentTypes )
        {
            names.add( contentType.getName() );
        }

        assertUnorderedArrayArrayEquals( expected, names.toArray() );
    }

    private int getRegionKeyByName( final Set<PageTemplateRegionEntity> regions, final String name )
    {
        for ( PageTemplateRegionEntity region : regions )
        {
            if ( name.equals( region.getName() ) )
            {
                return region.getKey();
            }
        }
        return -1;
    }

    private static void assertUnorderedArrayArrayEquals(Object[] a1, Object[] a2) {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort(b1);
        Arrays.sort(b2);

        assertArrayEquals( b1, b2 );
    }

    private static void assertArrayEquals( final Object[] a1, final Object[] a2 ) {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static String arrayToString( final Object[] a ) {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ ) {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
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

    private void loginUserInAdmin( UserKey userKey )
    {
        AdminSecurityHolder.setUser( userKey );
    }

    private void loginUserInPortal( UserKey userKey )
    {
        PortalSecurityHolder.setImpersonatedUser( userKey );
        PortalSecurityHolder.setLoggedInUser( userKey );
    }

    private MenuItemEntity createPage( String name, String parentName, String siteName )
    {
        return factory.createPageMenuItem( name, 0, name, name, siteName, "testuser", "testuser", false, false, "en", parentName, 0,
                                           new Date(), false, null );
    }

    private MenuItemEntity createSection( String name, String siteName, String username, boolean isOrdered )
    {
        return factory.createSectionMenuItem( name, 0, null, name, siteName, username, username, "en", null, null, isOrdered, null, false,
                                              null );
    }
}
