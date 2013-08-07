package com.enonic.cms.itest.core.structure.page.template;

import java.util.Date;
import java.util.HashSet;
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

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.DeletePageTemplateCommand;
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
import com.enonic.cms.store.dao.PageTemplatePortletDao;
import com.enonic.cms.store.dao.PageTemplateRegionDao;
import com.enonic.cms.store.dao.PortletEntityDao;

import static org.junit.Assert.*;

@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class PageTemplateServiceImpl_deletePageTemplateTest
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
    private PortletEntityDao portletEntityDao;

    @Before
    public void setUp()
    {
        factory = fixture.getFactory();

        // setup needed common data for each test
        fixture.initSystemData();

        save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSession();
        fixture.flushIndexTransaction();
    }

    @Test
    @Ignore
    public void remove_page_template_with_relations()
    {
        save( factory.createContentType( "just-another-cty", ContentHandlerName.CUSTOM.getHandlerClassShortName(), null ) );

        PageTemplateEntity pageTemplate =
            createPageTemplate( "my-template", PageTemplateType.SECTIONPAGE, "The Newspaper", "just-another-cty" );

        // PageTemplateRegion
        PageTemplateRegionEntity region_leftColumn = createPageTemplateRegion( 1, "leftColumn", pageTemplate );
        PageTemplateRegionEntity region_mainColumn = createPageTemplateRegion( 2, "mainColumn", pageTemplate );

        pageTemplate.addPageTemplateRegion( region_leftColumn );
        pageTemplate.addPageTemplateRegion( region_mainColumn );

        save( pageTemplate );

        PageTemplateRegionEntity pRegion1 = pageTemplateRegionDao.findByKey( 1 );
        assertEquals( 1, pRegion1.getKey() );

        PageTemplateRegionEntity pRegion2 = pageTemplateRegionDao.findByKey( 2 );
        assertEquals( 2, pRegion2.getKey() );

        PageTemplateEntity pPageTemplate = pageTemplateDao.findByKey( 0 );
        assertEquals( 0, pPageTemplate.getKey() );

        // Portlet
        final Document xmlData = XMLDocumentFactory.create( "<menudata>" +
                                                                "<defaultcss key=\"DEFAULT_CSS\"/>" +
                                                                "<default-localization-resource>DEFAULT_LOCALIZATION_RESOURCE</default-localization-resource>" +
                                                                "<device-class-resolver>DEVICE_CLASS_RESOLVER</device-class-resolver>" +
                                                                "<locale-resolver>LOCALE_RESOLVER</locale-resolver>" +
                                                                "</menudata>" ).getAsJDOMDocument();

        final SiteEntity site = factory.createSite( "The Newspaper", new Date(), xmlData, "en" );
        save( site );

        PortletEntity portlet = createPortlet( 11, "Portlet-1" );
        portlet.setSite( site );
        portlet.setStyleKey( ResourceKey.from( "CONTENT_OBJECT_STYLE" ) );
        portlet.setBorderKey( ResourceKey.from( "CONTENT_OBJECT_BORDER" ) );

        save( portlet );

        PortletEntity pPortlet = portletEntityDao.findByKey( 11 );

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
        pPageTemplate.getPageTemplateRegions().iterator().next().addPortlet( pTemplatePortlet );
        save( pPageTemplate );

        DeletePageTemplateCommand command = new DeletePageTemplateCommand( 0 );
        pageTemplateService.deletePageTemplate( command );

        PageTemplateEntity persisted = pageTemplateDao.findByKey( 0 );
        assertNull( persisted );

        PageTemplateRegionEntity region = pageTemplateRegionDao.findByKey( 1 );
        assertNull( region );

        region = pageTemplateRegionDao.findByKey( 2 );
        assertNull( region );

        PageTemplatePortletEntity templPortlet = pageTemplatePortletDao.findByKey( templatePortletKey );
        assertNull( templPortlet );
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
}
