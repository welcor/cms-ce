package com.enonic.cms.itest.core.structure.page.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import junit.framework.Assert;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.CopyPageTemplateCommand;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletKey;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateService;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PageTemplatePortletDao;
import com.enonic.cms.store.dao.PageTemplateRegionDao;
import com.enonic.cms.store.dao.PortletDao;

import static org.junit.Assert.*;

@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class PageTemplateServiceImpl_copyPageTemplateTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private PageTemplateService pageTemplateService;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private PageTemplateRegionDao pageTemplateRegionDao;

    @Autowired
    private PageTemplatePortletDao pageTemplatePortletDao;

    @Autowired
    private PortletDao portletDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createSite( "The Newspaper", new Date(), null, "en" ) );
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

        ContentTypeEntity contenType =
            factory.createContentType( "article", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes );
        contenType.setKey( 1001 );
        fixture.save( contenType );

        contenType = factory.createContentType( "document", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes );
        contenType.setKey( 1002 );
        fixture.save( contenType );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();
    }

    @Test
    @Ignore
    public void copy_page_template_with_relations()
    {
        save( factory.createContentType( "just-another-cty", ContentHandlerName.CUSTOM.getHandlerClassShortName(), null ) );

        PageTemplateEntity pageTemplate =
            createPageTemplate( "my-template", PageTemplateType.SECTIONPAGE, "The Newspaper", "just-another-cty" );

        // PageTemplateRegion
        PageTemplateRegionEntity region_leftColumn = createPageTemplateRegion( 20, "leftColumn", pageTemplate );
        PageTemplateRegionEntity region_mainColumn = createPageTemplateRegion( 21, "mainColumn", pageTemplate );

        pageTemplate.addPageTemplateRegion( region_leftColumn );
        pageTemplate.addPageTemplateRegion( region_mainColumn );

        pageTemplate.setStyleKey( ResourceKey.from( "/sites/advanced/dav/sites/advanced/page.xsl" ) );
        pageTemplate.setCssKey( ResourceKey.from( "/sites/advanced/dav/_public/sites/advanced/all.css" ) );

        ContentTypeEntity contentType = contentTypeDao.findByKey( new ContentTypeKey( 1001 ) );
        pageTemplate.addContentType( contentType );
        contentType = contentTypeDao.findByKey( new ContentTypeKey( 1002 ) );
        pageTemplate.addContentType( contentType );

        save( pageTemplate );

        PageTemplateRegionEntity pRegion1 = pageTemplateRegionDao.findByKey( 20 );
        assertEquals( 20, pRegion1.getKey() );

        PageTemplateRegionEntity pRegion2 = pageTemplateRegionDao.findByKey( 21 );
        assertEquals( 21, pRegion2.getKey() );

        List<PageTemplateEntity> templates = pageTemplateDao.findByName( "my-template" );
        assertEquals( 1, templates.size() );

        PageTemplateEntity pPageTemplate = templates.get( 0 );
        assertNotNull( pPageTemplate );

        // Portlet
        final String xml = "<menudata>" +
            "<defaultcss key=\"DEFAULT_CSS\"/>" +
            "<default-localization-resource>DEFAULT_LOCALIZATION_RESOURCE</default-localization-resource>" +
            "<device-class-resolver>DEVICE_CLASS_RESOLVER</device-class-resolver>" +
            "<locale-resolver>LOCALE_RESOLVER</locale-resolver>" +
            "</menudata>";
        final Document document = XMLDocumentFactory.create( xml ).getAsJDOMDocument();

        final SiteEntity site = factory.createSite( "Times, The", new Date(), document, "en" );
        save( site );

        PortletEntity portlet = createPortlet( 11, "Portlet-1" );
        portlet.setSite( site );
        portlet.setStyleKey( ResourceKey.from( "CONTENT_OBJECT_STYLE" ) );
        portlet.setBorderKey( ResourceKey.from( "CONTENT_OBJECT_BORDER" ) );
        portlet.setXmlData( "<portlet/>" );

        save( portlet );

        PortletEntity pPortlet = portletDao.findByKey( 11 );

        // PageTemplatePortlet
        PageTemplatePortletEntity templatePortlet = new PageTemplatePortletEntity();
        PageTemplatePortletKey templatePortletKey = new PageTemplatePortletKey( pPageTemplate.getKey(), pPortlet.getKey() );
        templatePortlet.setKey( templatePortletKey );
        templatePortlet.setPortlet( pPortlet );
        templatePortlet.setPageTemplate( pPageTemplate );
        templatePortlet.setPageTemplateRegion( pRegion1 );
        templatePortlet.setTimestamp( new Date() );

        save( templatePortlet );

        PageTemplatePortletEntity pTemplatePortlet = pageTemplatePortletDao.findByKey( templatePortletKey );

        pPageTemplate.addPagetTemplatePortlet( pTemplatePortlet );

        pPageTemplate.setDescription( "crocs" );

        save( pPageTemplate );

        User adminUser = fixture.findUserByName( User.ROOT_UID );

        CopyPageTemplateCommand command = new CopyPageTemplateCommand( 0, adminUser );
        pageTemplateService.copyPageTemplate( command );

        List<PageTemplateEntity> list = pageTemplateDao.findByName( "my-template (copy)" );
        PageTemplateEntity persisted = list.get( 0 );

        assertNotNull( persisted );

        assertEquals( 1, persisted.getKey() );
        assertEquals( "my-template (copy)", persisted.getName() );
        assertEquals( "crocs", persisted.getDescription() );
        assertEquals( PageTemplateType.SECTIONPAGE, persisted.getType() );
        assertEquals( RunAsType.DEFAULT_USER, persisted.getRunAs() );
        assertEquals( ResourceKey.from( "/sites/advanced/dav/sites/advanced/page.xsl" ), persisted.getStyleKey() );
        assertEquals( ResourceKey.from( "/sites/advanced/dav/_public/sites/advanced/all.css" ), persisted.getCssKey() );

        SiteEntity pSite = fixture.findSiteByName( "The Newspaper" );
        assertEquals( pSite, persisted.getSite() );

        assertEquals( "<pagetemplatedata/>", persisted.getXmlData().getDocumentAsString() );

        Set<PageTemplateRegionEntity> regions = persisted.getPageTemplateRegions();
        assertEquals( 2, regions.size() );

        Iterator<PageTemplateRegionEntity> iterator = regions.iterator();
        assertEquals( persisted, iterator.next().getPageTemplate() );
        assertEquals( persisted, iterator.next().getPageTemplate() );

        List<PageTemplatePortletEntity> portlets = persisted.getPortlets();
        assertEquals( 1, portlets.size() );

        PageTemplatePortletEntity portletEntity = portlets.get( 0 );
        int pteKey = persisted.getKey();

        assertEquals( new PageTemplatePortletKey( pteKey, 11 ), portletEntity.getKey() );
        assertEquals( persisted, portletEntity.getPageTemplate() );
        assertEquals( 11, portletEntity.getPortlet().getKey() );
        assertEquals( "Portlet-1", portletEntity.getPortlet().getName() );
        assertEquals( "leftColumn", portletEntity.getPageTemplateRegion().getName() );

        Set<ContentTypeEntity> contentTypes = persisted.getContentTypes();
        assertEquals( 3, contentTypes.size() );

        List<String> names = new ArrayList<String>( contentTypes.size() );
        for ( ContentTypeEntity cType : contentTypes )
        {
            names.add( cType.getName() );
        }

        assertUnorderedArrayArrayEquals( new String[]{"article", "document", "just-another-cty"}, names.toArray() );
    }

    private PageTemplateEntity createPageTemplate( String name, PageTemplateType type, String siteName, String... contentTypeNames )
    {
        PageTemplateEntity pageTemplate = factory.createPageTemplate( name, type, siteName, ResourceKey.from( "DUMMYKEY" ) );
        Set<ContentTypeEntity> supportedContentTypes = new HashSet<ContentTypeEntity>();
        for ( String contentTypeName : contentTypeNames )
        {
            supportedContentTypes.add( fixture.findContentTypeByName( contentTypeName ) );
        }
        pageTemplate.setContentTypes( supportedContentTypes );
        return pageTemplate;
    }

    private PageTemplateRegionEntity createPageTemplateRegion( int key, String name, PageTemplateEntity pageTemplate )
    {
        PageTemplateRegionEntity i = new PageTemplateRegionEntity();
        i.setKey( key );
        i.setName( name );
        i.setMultiple( true );
        i.setOverride( true );
        i.setSeparator( "dummySeparator" );
        i.setPageTemplate( pageTemplate );
        return i;
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

    private void save( Object object )
    {
        hibernateTemplate.save( object );
        hibernateTemplate.flush();
    }

    private static void assertUnorderedArrayArrayEquals( Object[] a1, Object[] a2 )
    {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort( b1 );
        Arrays.sort( b2 );

        assertArrayEquals( b1, b2 );
    }

    private static void assertArrayEquals( final Object[] a1, final Object[] a2 )
    {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static String arrayToString( final Object[] a )
    {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }
}
