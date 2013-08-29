package com.enonic.cms.core.structure.page.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PageTemplateRegionDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;

@Service("pageTemplateService")
public class PageTemplateServiceImpl
    implements PageTemplateService
{
    private static final String PAT_TABLE = "tPageTemplate";

    private static final String PTP_TABLE = "tPageTemplParam";

    @Autowired
    private KeyService keyService;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private PortletDao portletDao;

    @Autowired
    private PageTemplateRegionDao regionDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AdminConsoleTranslationService languageMap;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePageTemplate( final DeletePageTemplateCommand command )
    {
        final PageTemplateEntity pageTemplateToDelete = pageTemplateDao.findByKey( command.getKey() );

        if ( pageTemplateToDelete == null )
        {
            throw new IllegalArgumentException( "PageTemplate with key=" + command.getKey() + " not found" );
        }

        pageTemplateDao.delete( pageTemplateToDelete );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createPageTemplate( final CreatePageTemplateCommand command )
    {
        final Document doc = XMLTool.domparse( command.getXmlData(), "pagetemplate" );

        final PageTemplateEntity newPageTemplate = createPageTemplate( doc, true );

        pageTemplateDao.storeNew( newPageTemplate );
    }

    private PageTemplateEntity createPageTemplate( final Document doc, final boolean useOldKey )
    {
        final PageTemplateEntity newPageTemplate = new PageTemplateEntity();

        final Element docElem = doc.getDocumentElement();
        final Element[] pagetemplateElems =
            ( "pagetemplate".equals( docElem.getTagName() ) ) ? new Element[]{docElem} : XMLTool.getElements( doc.getDocumentElement() );

        try
        {
            for ( Element root : pagetemplateElems )
            {
                Map<String, Element> subelems = XMLTool.filterElements( root.getChildNodes() );

                // attribute: key
                final String keyStr = root.getAttribute( "key" );
                final int pageTemplateKey =
                    ( !useOldKey || StringUtils.isBlank( keyStr ) ) ? getNextKey( PAT_TABLE ) : Integer.parseInt( keyStr );
                newPageTemplate.setKey( pageTemplateKey );

                // attribute: menukey
                final String menukey = root.getAttribute( "menukey" );
                final int menuKey = Integer.parseInt( menukey );
                final SiteEntity site = siteDao.findByKey( menuKey );
                newPageTemplate.setSite( site );

                // element: stylesheet
                Element stylesheet = subelems.get( "stylesheet" );
                String path = stylesheet.getAttribute( "stylesheetkey" );
                final ResourceKey style = ResourceKey.from( path );
                newPageTemplate.setStyleKey( style );

                // element: name
                Element subelem = subelems.get( "name" );
                final String name = XMLTool.getElementText( subelem );
                newPageTemplate.setName( name );

                // element: description
                subelem = subelems.get( "description" );
                if ( subelem != null )
                {
                    final String description = XMLTool.getElementText( subelem );
                    newPageTemplate.setDescription( description );
                }

                // element: timestamp
                newPageTemplate.setTimestamp( new Date() );

                // element: xmlData
                subelem = subelems.get( "pagetemplatedata" );
                final Document ptdDoc;
                if ( subelem != null )
                {
                    ptdDoc = XMLTool.createDocument();
                    ptdDoc.appendChild( ptdDoc.importNode( subelem, true ) );
                }
                else
                {
                    ptdDoc = XMLTool.createDocument( "pagetemplatedata" );
                }

                final String xmlData = XMLTool.documentToString( ptdDoc );
                newPageTemplate.setXmlData( xmlData );

                // element: CSS
                subelem = subelems.get( "css" );
                ResourceKey css = null;
                if ( subelem != null )
                {
                    path = subelem.getAttribute( "stylesheetkey" );
                    css = ResourceKey.from( path );
                }
                newPageTemplate.setCssKey( css );

                // element: type
                final PageTemplateType type = PageTemplateType.valueOf( root.getAttribute( "type" ).toUpperCase() );
                newPageTemplate.setType( type );

                // element: runAs
                final String runAsStr = root.getAttribute( "runAs" );
                final RunAsType runAs = ( StringUtils.isNotEmpty( runAsStr ) ) ? RunAsType.valueOf( runAsStr ) : RunAsType.INHERIT;
                newPageTemplate.setRunAs( runAs );

                // element: PageTemplateRegion
                Element ptpsElem = XMLTool.getElement( root, "pagetemplateparameters" );
                int[] ptpKeys = null;
                if ( ptpsElem != null )
                {
                    Element[] ptpElems = XMLTool.getElements( ptpsElem );
                    for ( Element ptpElem : ptpElems )
                    {
                        ptpElem.setAttribute( "pagetemplatekey", Integer.toString( pageTemplateKey ) );
                    }

                    Document ptpDoc = XMLTool.createDocument();
                    Node n = ptpDoc.importNode( ptpsElem, true );
                    ptpDoc.appendChild( n );
                    ptpKeys = createPageTemplParam( newPageTemplate, ptpDoc );
                }

                pageTemplateDao.storeNew( newPageTemplate );

                // create all pageconobj entries for page
                Element contentobjectsElem = XMLTool.getElement( root, "contentobjects" );
                if ( contentobjectsElem != null )
                {
                    Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );

                    for ( Element contentobjectElem : contentobjectElems )
                    {
                        contentobjectElem.setAttribute( "pagetemplatekey", Integer.toString( pageTemplateKey ) );

                        int pIndex = Integer.parseInt( contentobjectElem.getAttribute( "parameterkey" ).substring( 1 ) );
                        contentobjectElem.setAttribute( "parameterkey", Integer.toString( ptpKeys[pIndex] ) );
                    }

                    Document coDoc = XMLTool.createDocument();
                    coDoc.appendChild( coDoc.importNode( contentobjectsElem, true ) );
                    updatePageTemplateCOs( coDoc, newPageTemplate, ptpKeys );
                }

                // element: contenttypes
                subelem = subelems.get( "contenttypes" );
                Element[] ctyElems = XMLTool.getElements( subelem );

                setPageTemplateContentTypes( newPageTemplate, ctyElems );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( message, nfe );
        }

        return newPageTemplate;
    }

    private List<ContentTypeEntity> setPageTemplateContentTypes( final PageTemplateEntity pageTemplate, final Element[] ctyElems )
    {
        final List<ContentTypeEntity> contentTypes = new ArrayList<ContentTypeEntity>( ctyElems.length );

        for ( Element ctyElem : ctyElems )
        {
            final int ctyKey = Integer.parseInt( ctyElem.getAttribute( "key" ) );
            final ContentTypeEntity contentType = contentTypeDao.findByKey( new ContentTypeKey( ctyKey ) );
            pageTemplate.addContentType( contentType );
            contentTypes.add( contentType );
        }

        // do the same for all menuitems using this page template
        Collection<MenuItemEntity> menuItems = menuItemDao.findByPageTemplate( pageTemplate.getKey() );

        for ( MenuItemEntity menuItem : menuItems )
        {
            if ( menuItem.isSection() )
            {
                menuItem.clearSectionContentTypes();
                menuItem.addAllowedSectionContentType( contentTypes );
            }
        }

        return contentTypes;
    }

    private int[] createPageTemplParam( final PageTemplateEntity pageTemplate, final Document ptpDoc )
    {
        // XML DOM
        Element root = ptpDoc.getDocumentElement();

        // check: does root element exist?
        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( message, null );
        }

        // check: if root element is not contentrating, throw create exception
        if ( !"pagetemplateparameter".equals( root.getTagName() ) && !"pagetemplateparameters".equals( root.getTagName() ) )
        {
            String message = "Root element is not a pagetemplate or pagetemplates element: {0}";
            VerticalEngineLogger.errorCreate( message, root.getTagName(), null );
        }

        final Node[] nodes;
        if ( "pagetemplateparameters".equals( root.getTagName() ) )
        {
            nodes = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
            if ( nodes == null || nodes.length == 0 )
            {
                String message = "No page template parameters to create";
                VerticalEngineLogger.warn( message );
            }
        }
        else
        {
            nodes = new Node[]{root};
        }
        int[] ptptKeys = new int[nodes.length];

        try
        {
            for ( int i = 0; i < nodes.length; i++ )
            {
                final PageTemplateRegionEntity region = new PageTemplateRegionEntity();

                Element elem = (Element) nodes[i];
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: key (generated in database)
                ptptKeys[i] = getNextKey( PTP_TABLE );
                region.setKey( ptptKeys[i] );

                // element: pagetemplate
                region.setPageTemplate( pageTemplate );
                pageTemplate.addPageTemplateRegion( region );

                // element: multiple
                final String multiple = elem.getAttribute( "multiple" );
                region.setMultiple( "1".equals( multiple ) );

                // element: override
                final String override = elem.getAttribute( "override" );
                region.setOverride( "1".equals( override ) );

                // element: name
                Element subelem = subelems.get( "name" );
                final String name = XMLTool.getElementText( subelem );
                region.setName( name );

                // element: separator
                subelem = subelems.get( "separator" );
                String separator = XMLTool.getElementText( subelem );
                separator = StringUtils.isBlank( separator ) ? "" : separator;
                region.setSeparator( separator );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( message, nfe );
        }

        return ptptKeys;
    }

    private void updatePageTemplateCOs( final Document contentobjectDoc, final PageTemplateEntity pageTemplate, final int[] paramKeys )
    {
        // XML DOM
        Element root = contentobjectDoc.getDocumentElement();

        // check: does root element exist?
        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( message, null );
        }

        // check: if root element is not contentrating, throw create exception
        if ( !"contentobject".equals( root.getTagName() ) && !"contentobjects".equals( root.getTagName() ) )
        {
            String message = "Root element is not a contentobject or contentobjects element: {0}";
            VerticalEngineLogger.errorCreate( message, root.getTagName(), null );
        }

        final Node[] nodes;
        if ( "contentobjects".equals( root.getTagName() ) )
        {
            nodes = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
        }
        else
        {
            nodes = new Node[]{root};
        }

        try
        {
            for ( final Node node : nodes )
            {
                final PageTemplatePortletEntity template = new PageTemplatePortletEntity();
                template.setPageTemplate( pageTemplate );
                pageTemplate.addPageTemplatePortlet( template );

                Element elem = (Element) node;
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: portlet
                String portletkey = elem.getAttribute( "conobjkey" );
                final int portletKey = Integer.parseInt( portletkey );
                final PortletEntity portlet = portletDao.findByKey( portletKey );
                template.setPortlet( portlet );

                // attribute: region
                String regionkey = elem.getAttribute( "parameterkey" );
                final int regionKey = ( regionkey.charAt( 0 ) == '_' )
                    ? paramKeys[Integer.parseInt( regionkey.substring( 1 ) )]
                    : Integer.parseInt( regionkey );

                final PageTemplateRegionEntity region = pageTemplate.findRegionByKey( regionKey );
                template.setPageTemplateRegion( region );
                region.addPortlet( template );

                // attribute: order
                Element subelem = subelems.get( "order" );
                final int order = Integer.parseInt( XMLTool.getElementText( subelem ) );
                template.setOrder( order );

                template.setKey( new PageTemplatePortletKey( pageTemplate.getKey(), portlet.getKey() ) );
                template.setTimestamp( new Date() );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( message, nfe );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copyPageTemplate( final CopyPageTemplateCommand command )
    {
        final PageTemplateEntity pageTemplateToCopy = pageTemplateDao.findByKey( command.getKey() );

        if ( pageTemplateToCopy == null )
        {
            throw new IllegalArgumentException( "PageTemplate with key=" + command.getKey() + " not found" );
        }

        final PageTemplateEntity newPageTemplate = new PageTemplateEntity( pageTemplateToCopy );
        newPageTemplate.setTimestamp( new Date() );
        newPageTemplate.setKey( getNextKey( PAT_TABLE ) );
        final Map translationMap = languageMap.getTranslationMap( command.getUser().getSelectedLanguageCode() );
        newPageTemplate.setName( pageTemplateToCopy.getName() + " (" + translationMap.get( "%txtCopy%" ) + ")" );

        final List<PageTemplatePortletEntity> portlets = newPageTemplate.getPortlets();
        final Set<PageTemplateRegionEntity> regions = newPageTemplate.getPageTemplateRegions();
        newPageTemplate.setPageTemplatePortlets( Lists.<PageTemplatePortletEntity>newArrayList() );
        newPageTemplate.setPageTemplateRegions( Sets.<PageTemplateRegionEntity>newHashSet() );
        // first persist page template copy without references to regions or portlets
        pageTemplateDao.storeNew( newPageTemplate );

        // add references to regions and portlets, persist new regions
        for ( PageTemplateRegionEntity region : regions )
        {
            region.setKey( getNextKey( PTP_TABLE ) );
            regionDao.storeNew( region );
            newPageTemplate.addPageTemplateRegion( region );
        }
        for ( PageTemplatePortletEntity portlet : portlets)
        {
            portlet.setKey( new PageTemplatePortletKey( newPageTemplate.getKey(), portlet.getPortlet().getKey() ) );
            newPageTemplate.addPageTemplatePortlet( portlet );
        }

        pageTemplateDao.store( newPageTemplate );
    }

    private Document createPageTemplatesDocument( Collection<PageTemplateEntity> pageTemplates )
    {
        Document doc = XMLTool.createDocument( "pagetemplates" );
        if ( pageTemplates == null )
        {
            return doc;
        }

        for ( PageTemplateEntity pageTemplate : pageTemplates )
        {
            Element root = doc.getDocumentElement();
            Document ptdDoc = null;

            org.jdom.Document pageTemplateXmlDataAsJdomDoc = pageTemplate.getXmlDataAsDocument();
            if ( pageTemplateXmlDataAsJdomDoc != null )
            {
                ptdDoc = XMLDocumentFactory.create( pageTemplateXmlDataAsJdomDoc ).getAsDOMDocument();
                Element docElem = XMLTool.getElement( ptdDoc.getDocumentElement(), "document" );
                if ( docElem != null )
                {
                    Node firstChild = docElem.getFirstChild();
                    if ( firstChild == null || firstChild.getNodeType() != Node.CDATA_SECTION_NODE )
                    {
                        docElem.setAttribute( "mode", "xhtml" );
                    }
                }
            }

            Element elem = XMLTool.createElement( doc, root, "pagetemplate" );
            elem.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "menukey", String.valueOf( pageTemplate.getSite().getKey() ) );

            // sub-elements
            XMLTool.createElement( doc, elem, "name", pageTemplate.getName() );
            XMLTool.createElement( doc, elem, "description", pageTemplate.getDescription() );
            Element tmp = XMLTool.createElement( doc, elem, "stylesheet" );
            tmp.setAttribute( "stylesheetkey", pageTemplate.getStyleKey().toString() );
            tmp.setAttribute( "exists", resourceService.getResourceFile( pageTemplate.getStyleKey() ) != null ? "true" : "false" );

            // element conobjects for pagetemplate
            Document contentobj = getPageTemplateCO( pageTemplate );
            elem.appendChild( doc.importNode( contentobj.getDocumentElement(), true ) );

            // get page template parameters
            Document ptpDoc = getPageTemplParams( pageTemplate );
            Node ptpNode = doc.importNode( ptpDoc.getDocumentElement(), true );
            elem.appendChild( ptpNode );

            // element timestamp
            XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( pageTemplate.getTimestamp(), true ) );

            // element: pagetemplatedata
            if ( ptdDoc != null )
            {
                elem.appendChild( doc.importNode( ptdDoc.getDocumentElement(), true ) );
            }

            // element: CSS
            ResourceKey cssKey = pageTemplate.getCssKey();
            if ( cssKey != null )
            {
                tmp = XMLTool.createElement( doc, elem, "css" );
                tmp.setAttribute( "stylesheetkey", cssKey.toString() );
                tmp.setAttribute( "exists", resourceService.getResourceFile( cssKey ) != null ? "true" : "false" );
            }

            // attribute: runAs & defaultRunAsUser
            elem.setAttribute( "runAs", pageTemplate.getRunAs().toString() );
            UserEntity defaultRunAsUser = pageTemplate.getSite().resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            elem.setAttribute( "defaultRunAsUser", defaultRunAsUserName );

            // attribute: type
            elem.setAttribute( "type", pageTemplate.getType().getName() );

            // contenttypes
            Document contentTypesDoc = getContentTypesDocument( pageTemplate.getContentTypes() );
            XMLTool.mergeDocuments( elem, contentTypesDoc, true );
        }

        return doc;
    }

    private Document getContentTypesDocument( final Set<ContentTypeEntity> ctys )
    {
        Document doc = XMLTool.createDocument( "contenttypes" );
        for ( ContentTypeEntity contentType : ctys )
        {
            Element contentTypeElem = XMLTool.createElement( doc, doc.getDocumentElement(), "contenttype" );
            contentTypeElem.setAttribute( "key", Integer.toString( contentType.getKey() ) );
            XMLTool.createElement( doc, contentTypeElem, "name", contentType.getName() );
        }
        return doc;
    }

    private Document getPageTemplParams( final PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "pagetemplateparameters" );

        if ( pageTemplate != null )
        {
            for ( PageTemplateRegionEntity entity : pageTemplate.getPageTemplateRegions() )
            {
                Element elem = XMLTool.createElement( doc, root, "pagetemplateparameter" );
                elem.setAttribute( "key", String.valueOf( entity.getKey() ) );
                elem.setAttribute( "pagetemplatekey", String.valueOf( entity.getPageTemplate().getKey() ) );
                elem.setAttribute( "multiple", entity.isMultiple() ? "1" : "0" );
                elem.setAttribute( "override", entity.isOverride() ? "1" : "0" );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", entity.getName() );
                XMLTool.createElement( doc, elem, "separator", entity.getSeparator() );
            }
        }

        return doc;
    }

    private Document getPageTemplateCO( final PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "contentobjects" );

        final List<PageTemplatePortletEntity> objects = pageTemplate.getPortlets();
        for ( PageTemplatePortletEntity pageTemplateObject : objects )
        {
            final PortletEntity portlet = pageTemplateObject.getPortlet();
            final PageTemplateRegionEntity pageTemplateParam = pageTemplateObject.getPageTemplateRegion();

            Element elem = XMLTool.createElement( doc, root, "contentobject" );
            elem.setAttribute( "pagetemplatekey", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "conobjkey", String.valueOf( portlet.getKey() ) );

            elem.setAttribute( "parameterkey", String.valueOf( pageTemplateParam.getKey() ) );

            // element: contentobjectdata
            Document contentdata = XMLDocumentFactory.create( portlet.getXmlDataAsJDOMDocument() ).getAsDOMDocument();
            Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
            elem.appendChild( xmldata_root );

            // sub-elements
            XMLTool.createElement( doc, elem, "order", String.valueOf( pageTemplateObject.getOrder() ) );
            XMLTool.createElement( doc, elem, "name", portlet.getName() );
            XMLTool.createElement( doc, elem, "separator", pageTemplateParam.getSeparator() );
            elem = XMLTool.createElement( doc, elem, "parametername", pageTemplateParam.getName() );
            elem.setAttribute( "multiple", String.valueOf( pageTemplateParam.isMultiple() ? "1" : "0" ) );
            elem.setAttribute( "override", String.valueOf( pageTemplateParam.isOverride() ? "1" : "0" ) );
        }

        return doc;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePageTemplate( final UpdatePageTemplateCommand command )
    {
        final Document doc = XMLTool.domparse( command.getXmlData(), "pagetemplate" );

        final PageTemplateEntity pageTemplate = updatePageTemplate( doc );

        pageTemplateDao.updateExisting( pageTemplate );
    }

    private PageTemplateEntity updatePageTemplate( Document doc )
    {
        Element docElem = doc.getDocumentElement();
        Element[] pagetemplateElems;
        if ( "pagetemplate".equals( docElem.getTagName() ) )
        {
            pagetemplateElems = new Element[]{docElem};
        }
        else
        {
            pagetemplateElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        PageTemplateEntity pageTemplate = null;

        try
        {
            for ( Element root : pagetemplateElems )
            {
                Map<String, Element> subelems = XMLTool.filterElements( root.getChildNodes() );

                // attribute: key
                String keyStr = root.getAttribute( "key" );
                final int pageTemplateKey = Integer.parseInt( keyStr );

                pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );

                if ( pageTemplate == null )
                {
                    throw new IllegalArgumentException( "PageTemplate with key=" + pageTemplateKey + " not found" );
                }

                // attribute: type
                final PageTemplateType pageTemplateType = PageTemplateType.valueOf( root.getAttribute( "type" ).toUpperCase() );
                pageTemplate.setType( pageTemplateType );

                // attribute: runAs
                final String runAsStr = root.getAttribute( "runAs" );
                final RunAsType runAs = ( StringUtils.isNotEmpty( runAsStr ) ) ? RunAsType.valueOf( runAsStr ) : RunAsType.INHERIT;
                pageTemplate.setRunAs( runAs );

                // attribute: menukey
                final String menukey = root.getAttribute( "menukey" );
                final int menuKey = Integer.parseInt( menukey );
                final SiteEntity site = siteDao.findByKey( menuKey );
                pageTemplate.setSite( site );

                // element: stylesheet
                Element stylesheet = subelems.get( "stylesheet" );
                String path = stylesheet.getAttribute( "stylesheetkey" );
                final ResourceKey style = ResourceKey.from( path );
                pageTemplate.setStyleKey( style );

                // element: name
                Element subelem = subelems.get( "name" );
                final String name = XMLTool.getElementText( subelem );
                pageTemplate.setName( name );

                // element: description
                String description = null;
                subelem = subelems.get( "description" );
                if ( subelem != null )
                {
                    description = XMLTool.getElementText( subelem );
                }
                pageTemplate.setDescription( description );

                // element: timestamp
                pageTemplate.setTimestamp( new Date() );

                // element: xmlData
                subelem = subelems.get( "pagetemplatedata" );
                final Document ptdDoc;
                if ( subelem != null )
                {
                    ptdDoc = XMLTool.createDocument();
                    ptdDoc.appendChild( ptdDoc.importNode( subelem, true ) );
                }
                else
                {
                    ptdDoc = XMLTool.createDocument( "pagetemplatedata" );
                }

                final String xmlData = XMLTool.documentToString( ptdDoc );
                pageTemplate.setXmlData( xmlData );

                // element: CSS
                subelem = subelems.get( "css" );
                ResourceKey css = null;
                if ( subelem != null )
                {
                    path = subelem.getAttribute( "stylesheetkey" );
                    css = ResourceKey.from( path );
                }
                pageTemplate.setCssKey( css );

                // store relations in old schema
                Element ptpsElem = XMLTool.getElement( root, "pagetemplateparameters" );
                Element contentobjectsElem = XMLTool.getElement( root, "contentobjects" );

                if ( contentobjectsElem != null )
                {
                    Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );

                    for ( Element contentobjectElem : contentobjectElems )
                    {
                        String regionKey = contentobjectElem.getAttribute( "parameterkey" );
                        String destination = getDestinationByRegionKey( ptpsElem, regionKey );

                        contentobjectElem.setAttribute( "destination", destination );
                    }
                }

                // Clear all old relationships
                pageTemplate.clearPageTemplateRegions();
                pageTemplate.clearPageTemplatePortlets();
                pageTemplate.clearContentTypes();

                // element: PageTemplateRegion
                int[] ptpKeys = null;
                if ( ptpsElem != null )
                {
                    Element[] ptpElems = XMLTool.getElements( ptpsElem );
                    for ( Element ptp : ptpElems )
                    {
                        ptp.setAttribute( "pagetemplatekey", Integer.toString( pageTemplateKey ) );
                    }

                    Document ptpDoc = XMLTool.createDocument();
                    Node n = ptpDoc.importNode( ptpsElem, true );
                    ptpDoc.appendChild( n );
                    ptpKeys = createPageTemplParam( pageTemplate, ptpDoc );
                }

                pageTemplateDao.updateExisting( pageTemplate );
                pageTemplateDao.getHibernateTemplate().flush();

                // create all pageconobj entries for page
                if ( contentobjectsElem != null )
                {
                    Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );

                    for ( Element contentobjectElem : contentobjectElems )
                    {
                        contentobjectElem.setAttribute( "pagetemplatekey", Integer.toString( pageTemplateKey ) );

                        String destination = contentobjectElem.getAttribute( "destination" );
                        PageTemplateRegionEntity region = pageTemplate.findRegionByName( destination );

                        contentobjectElem.setAttribute( "parameterkey", Integer.toString( region.getKey() ) );
                    }

                    Document coDoc = XMLTool.createDocument();
                    coDoc.appendChild( coDoc.importNode( contentobjectsElem, true ) );
                    updatePageTemplateCOs( coDoc, pageTemplate, ptpKeys );
                }

                // element: contenttypes
                subelem = subelems.get( "contenttypes" );
                Element[] ctyElems = XMLTool.getElements( subelem );

                final List<ContentTypeEntity> contentTypes = setPageTemplateContentTypes( pageTemplate, ctyElems );

                // If page template is of type "section", we need to create sections for menuitems
                // that does not have one
                if ( pageTemplateType == PageTemplateType.SECTIONPAGE )
                {
                    Collection<MenuItemEntity> menuItems = menuItemDao.findByPageTemplate( pageTemplate.getKey() );

                    for ( MenuItemEntity menuItem : menuItems )
                    {
                        if ( !menuItem.isSection() )
                        {
                            menuItem.setOrderedSection( true );
                            menuItem.setSection( true );

                            menuItem.clearSectionContentTypes();
                            menuItem.addAllowedSectionContentType( contentTypes );

                            menuItemDao.updateExisting( menuItem );
                        }
                    }
                }
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( message, nfe );
        }

        return pageTemplate;
    }

    private String getDestinationByRegionKey( final Element ptpsElem, final String regionKey )
    {
        if ( ptpsElem != null )
        {
            Element[] ptpElems = XMLTool.getElements( ptpsElem );
            for ( Element ptp : ptpElems )
            {
                if ( regionKey.equals( ptp.getAttribute( "key" ) ) )
                {
                    Map<String, Element> subelems = XMLTool.filterElements( ptp.getChildNodes() );

                    Element subelem = subelems.get( "name" );
                    final String name = XMLTool.getElementText( subelem );
                    return name;
                }
            }
        }
        return null;
    }

    private int getNextKey( final String tableName )
    {
        return keyService.generateNextKeySafe( tableName );
    }
}
