package com.enonic.cms.itest.core.structure.page.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.CreatePageTemplateCommand;
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
import com.enonic.cms.store.dao.PageTemplateDao;

import static org.junit.Assert.*;

@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class PageTemplateServiceImpl_createPageTemplateTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private PageTemplateService pageTemplateService;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    private int siteKey;

    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        final SiteEntity site = factory.createSite( "The Newspaper", new Date(), null, "en" );
        siteKey = site.getKey().toInt();
        fixture.save( site );

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

        final PortletEntity portlet = createPortlet( 1, "Article-Show" );
        portlet.setSite( site );
        portlet.setStyleKey( ResourceKey.from( "CONTENT_OBJECT_STYLE" ) );
        portlet.setBorderKey( ResourceKey.from( "CONTENT_OBJECT_BORDER" ) );
        fixture.save( portlet );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();
    }

    @Test
    public void create_page_template_with_relations()
    {
        final String xmdData = "<pagetemplate menukey=\"" + siteKey + "\" runAs=\"DEFAULT_USER\" type=\"content\">" +
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

        SiteEntity site = fixture.findSiteByName( "The Newspaper" );
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

        List<String> names = new ArrayList<String>( contentTypes.size() );
        for ( ContentTypeEntity contentType : contentTypes )
        {
            names.add( contentType.getName() );
        }

        assertUnorderedArrayArrayEquals( new String[]{"article", "document"}, names.toArray() );
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
