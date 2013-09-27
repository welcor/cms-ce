package com.enonic.cms.core.structure.page.template;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;

import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PageTemplatePortletDao;
import com.enonic.cms.store.dao.PageTemplateRegionDao;
import com.enonic.cms.store.dao.PageWindowDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

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
    private PageTemplatePortletDao pageTemplatePortletDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private PortletDao portletDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private PageWindowDao pageWindowDao;

    @Autowired
    private PageTemplateRegionDao regionDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private AdminConsoleTranslationService languageMap;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deletePageTemplate( final DeletePageTemplateCommand command )
    {
        final PageTemplateEntity pageTemplateToDelete = pageTemplateDao.findByKey( command.getPageTemplateKey().toInt() );

        if ( pageTemplateToDelete == null )
        {
            throw new IllegalArgumentException( "PageTemplate with key=" + command.getPageTemplateKey() + " not found" );
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
        Element root = ptpDoc.getDocumentElement();

        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( message, null );
        }

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

                populatePageTemplateRegion( region, elem, subelems );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( message, nfe );
        }

        return ptptKeys;
    }

    private void updatePageTemplParam( final PageTemplateEntity pageTemplate, final Document ptpDoc )
    {
        Element root = ptpDoc.getDocumentElement();

        if ( root == null )
        {
            String message = "Root element does not exist";
            VerticalEngineLogger.errorUpdate( message, null );
        }

        if ( !"pagetemplateparameter".equals( root.getTagName() ) && !"pagetemplateparameters".equals( root.getTagName() ) )
        {
            String message = "Root element is not the \"pagetemplateparameter\" or \"pagetemplateparameters\" element: {0}";
            VerticalEngineLogger.errorUpdate( message, root.getTagName(), null );
        }

        Node[] node;
        if ( "pagetemplateparameters".equals( root.getTagName() ) )
        {
            node = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
            if ( node == null || node.length == 0 )
            {
                return;
                //String message = "No page template parameters to create.";
                //VerticalEngineLogger.warn(2, message, null);
            }
        }
        else
        {
            node = new Node[]{root};
        }

        try
        {
            for ( Node aNode : node )
            {
                Element elem = (Element) aNode;
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: key
                String keyStr = elem.getAttribute( "key" );
                int pageTemplParamKey = Integer.parseInt( keyStr );
                final PageTemplateRegionEntity region = pageTemplate.findRegionByKey( pageTemplParamKey );

                populatePageTemplateRegion( region, elem, subelems );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( message, nfe );
        }
    }

    private void populatePageTemplateRegion( final PageTemplateRegionEntity region, final Element elem,
                                             final Map<String, Element> subelems )
    {
        // element: name
        final Element nameElement = subelems.get( "name" );
        final String name = XMLTool.getElementText( nameElement );
        region.setName( name );

        // element: multiple
        final String multiple = elem.getAttribute( "multiple" );
        region.setMultiple( "1".equals( multiple ) );

        // element: override
        final String override = elem.getAttribute( "override" );
        region.setOverride( "1".equals( override ) );

        // element: separator
        final Element separatorElement = subelems.get( "separator" );
        String separator = XMLTool.getElementText( separatorElement );
        separator = StringUtils.isBlank( separator ) ? "" : separator;
        region.setSeparator( separator );
    }

    private void updatePageTemplateCOs( final Document contentobjectDoc, final PageTemplateEntity pageTemplate, final int[] ptpKeys )
    {
        Element root = contentobjectDoc.getDocumentElement();

        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( message, null );
        }

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
                Element elem = (Element) node;
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: portlet
                String portletkey = elem.getAttribute( "conobjkey" );
                final int portletKey = Integer.parseInt( portletkey );
                final PortletEntity portlet = portletDao.findByKey( portletKey );

                final PageTemplatePortletKey key = new PageTemplatePortletKey( pageTemplate.getKey(), portlet.getKey() );

                // create or update PageTemplatePortletEntity
                final PageTemplatePortletEntity template = createOrLoadPageTemplatePortletEntity( key );

                template.setPageTemplate( pageTemplate );
                pageTemplate.addPageTemplatePortlet( template );

                template.setPortlet( portlet );

                // attribute: region
                String regionKeyStr = elem.getAttribute( "parameterkey" );
                final int regionKey = ( regionKeyStr.charAt( 0 ) == '_' )
                    ? ptpKeys[Integer.parseInt( regionKeyStr.substring( 1 ) )]
                    : Integer.parseInt( regionKeyStr );

                final PageTemplateRegionEntity region = pageTemplate.findRegionByKey( regionKey );
                template.setPageTemplateRegion( region );
                region.addPortlet( template );

                // attribute: order
                Element subelem = subelems.get( "order" );
                final int order = Integer.parseInt( XMLTool.getElementText( subelem ) );
                template.setOrder( order );
            }
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( message, nfe );
        }
    }

    private PageTemplatePortletEntity createOrLoadPageTemplatePortletEntity( final PageTemplatePortletKey key )
    {
        final PageTemplatePortletEntity template = pageTemplatePortletDao.findByKey( key );

        if ( template == null )
        {
            final PageTemplatePortletEntity newTemplate = new PageTemplatePortletEntity();

            newTemplate.setKey( key );
            newTemplate.setTimestamp( new Date() );

            return newTemplate;
        }

        return template;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copyPageTemplate( final CopyPageTemplateCommand command )
    {
        final PageTemplateEntity pageTemplateToCopy = pageTemplateDao.findByKey( command.getPageTemplateKey().toInt() );

        if ( pageTemplateToCopy == null )
        {
            throw new IllegalArgumentException( "PageTemplate with key=" + command.getPageTemplateKey() + " not found" );
        }

        final PageTemplateEntity newPageTemplate = new PageTemplateEntity( pageTemplateToCopy );
        newPageTemplate.setTimestamp( new Date() );
        newPageTemplate.setKey( getNextKey( PAT_TABLE ) );

        final User copier = userDao.findByKey( command.getCopierKey() );
        final Map translationMap = languageMap.getTranslationMap( copier.getSelectedLanguageCode() );
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePageTemplate( final UpdatePageTemplateCommand command )
    {
        final Document doc = XMLTool.domparse( command.getXmlData(), "pagetemplate" );

        final Element docElem = doc.getDocumentElement();
        final Element[] pagetemplateElems;

        if ( "pagetemplate".equals( docElem.getTagName() ) )
        {
            pagetemplateElems = new Element[]{docElem};
        }
        else
        {
            pagetemplateElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        final Element pageTemplateElement = pagetemplateElems[0];
        final Map<String, Element> subelems = XMLTool.filterElements( pageTemplateElement.getChildNodes() );

        final int pageTemplateKey = getPageTemplateKey( pageTemplateElement );

        final PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( getPageTemplateKey( pageTemplateElement ) );

        if ( pageTemplate == null )
        {
            throw new IllegalArgumentException( "PageTemplate with key=" + pageTemplateKey + " not found" );
        }

        populatePageTemplate( pageTemplateElement, subelems, pageTemplate );

        final Element contentobjectsElem = XMLTool.getElement( pageTemplateElement, "contentobjects" );
        Element ptpsElem = XMLTool.getElement( pageTemplateElement, "pagetemplateparameters" );

        if ( ptpsElem != null )
        {
            // update all ptp entries for page
            final Node[] ptpNode = XMLTool.filterNodes( ptpsElem.getChildNodes(), Node.ELEMENT_NODE );
            final Document updatedPTPDoc = XMLTool.createDocument( "pagetemplateparameters" );
            final Element updatedPTP = updatedPTPDoc.getDocumentElement();
            final Document newPTPDoc = XMLTool.createDocument( "pagetemplateparameters" );
            final Element newPTP = newPTPDoc.getDocumentElement();

            int[] oldPTPKey = getPageTemplParamKeys( pageTemplate );

            int updatedPTPs = 0, newPTPs = 0;
            int[] updatedPTPKey = new int[ptpNode.length];

            for ( final Node aPtpNode : ptpNode )
            {
                ptpsElem = (Element) aPtpNode;
                final String attribute = ptpsElem.getAttribute( "key" );
                int key;
                if ( attribute != null && attribute.length() > 0 )
                {
                    key = Integer.parseInt( attribute );
                }
                else
                {
                    key = -1;
                }
                if ( key >= 0 )
                {
                    updatedPTP.appendChild( updatedPTPDoc.importNode( ptpsElem, true ) );
                    updatedPTPKey[updatedPTPs++] = key;
                }
                else
                {
                    newPTP.appendChild( newPTPDoc.importNode( ptpsElem, true ) );
                    newPTPs++;
                }
            }

            // remove old
            if ( updatedPTPs == 0 )
            {
                Integer[] pageKeys = getPageKeysByPageTemplateKey( pageTemplateKey );
                if ( pageKeys.length != 0 )
                {
                    pageWindowDao.deleteByPageKeys( pageKeys );
                }

                pageTemplate.clearPageTemplateRegions();
            }
            else if ( updatedPTPs < oldPTPKey.length )
            {
                final int sortResult[] = new int[updatedPTPs];
                System.arraycopy( updatedPTPKey, 0, sortResult, 0, updatedPTPs );
                updatedPTPKey = sortResult;

                Arrays.sort( oldPTPKey );
                oldPTPKey = ArrayUtil.removeDuplicates( oldPTPKey );
                Arrays.sort( updatedPTPKey );
                updatedPTPKey = ArrayUtil.removeDuplicates( updatedPTPKey );
                final int diff[][] = ArrayUtil.diff( oldPTPKey, updatedPTPKey );

                final int[] regionKeys = diff[0];
                final Integer[] pageKeys = getPageKeysByPageTemplateKey( pageTemplateKey );

                if ( pageKeys.length != 0 )
                {
                    pageWindowDao.deleteByPageKeyAndTemplateRegionKey( pageKeys, regionKeys );
                }

                pageTemplate.removePageTemplParams( regionKeys );
            }

            updatePageTemplParam( pageTemplate, updatedPTPDoc );

            final int[] ptpKeys = newPTPs > 0 ? createPageTemplParam( pageTemplate, newPTPDoc ) : new int[0];

            pageTemplate.clearPageTemplatePortlets();
            pageTemplate.clearContentTypes();

            if ( contentobjectsElem != null )
            {
                // update all pageconobj entries for page
                final Document cobsDoc = XMLTool.createDocument();
                cobsDoc.appendChild( cobsDoc.importNode( contentobjectsElem, true ) );
                updatePageTemplateCOs( cobsDoc, pageTemplate, ptpKeys );
            }
        }

        // element: contenttypes
        final Element contenttypes = subelems.get( "contenttypes" );
        final Element[] ctyElems = XMLTool.getElements( contenttypes );
        final List<ContentTypeEntity> contentTypes = setPageTemplateContentTypes( pageTemplate, ctyElems );
        createSectionsForMenuItems( pageTemplate, contentTypes );

        pageTemplateDao.updateExisting( pageTemplate );
    }

    private void populatePageTemplate( final Element pageTemplateElement, final Map<String, Element> subelems,
                                       final PageTemplateEntity pageTemplate )
    {
        // attribute: type
        final PageTemplateType pageTemplateType = PageTemplateType.valueOf( pageTemplateElement.getAttribute( "type" ).toUpperCase() );

        pageTemplate.setType( pageTemplateType );

        // attribute: runAs
        final String runAsStr = pageTemplateElement.getAttribute( "runAs" );
        final RunAsType runAs = ( StringUtils.isNotEmpty( runAsStr ) ) ? RunAsType.valueOf( runAsStr ) : RunAsType.INHERIT;
        pageTemplate.setRunAs( runAs );

        // attribute: menukey
        final String menukey = pageTemplateElement.getAttribute( "menukey" );
        final int menuKey = Integer.parseInt( menukey );
        final SiteEntity site = siteDao.findByKey( menuKey );
        pageTemplate.setSite( site );

        // element: stylesheet
        final Element stylesheet = subelems.get( "stylesheet" );
        final String stylesheetkey = stylesheet.getAttribute( "stylesheetkey" );
        final ResourceKey style = ResourceKey.from( stylesheetkey );
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
            final String patstylesheetkey = subelem.getAttribute( "stylesheetkey" );
            css = ResourceKey.from( patstylesheetkey );
        }
        pageTemplate.setCssKey( css );
    }

    private void createSectionsForMenuItems( final PageTemplateEntity pageTemplate,
                                             final List<ContentTypeEntity> contentTypes )
    {
        // If page template is of type "section", we need to create sections for menuitems
        // that does not have one
        if ( pageTemplate.getType() == PageTemplateType.SECTIONPAGE )
        {
            final Collection<MenuItemEntity> menuItems = menuItemDao.findByPageTemplate( pageTemplate.getKey() );

            for ( final MenuItemEntity menuItem : menuItems )
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

    private int getPageTemplateKey( final Element root )
    {
        final String keyStr = root.getAttribute( "key" );

        try
        {
            return Integer.parseInt( keyStr );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( message, nfe );
            return -1;
        }
    }

    private int[] getPageTemplParamKeys( final PageTemplateEntity pageTemplate )
    {
        final Set<PageTemplateRegionEntity> regions = pageTemplate.getPageTemplateRegions();

        final int[] keys = new int[regions.size()];
        int i = 0;

        for ( PageTemplateRegionEntity region : regions )
        {
            keys[i++] = region.getKey();
        }

        return keys;
    }

    private Integer[] getPageKeysByPageTemplateKey( final int pageTemplateKey )
    {
        final List<PageEntity> pages = pageDao.findByTemplateKeys( Arrays.asList( pageTemplateKey ) );
        final Integer[] keys = new Integer[pages.size()];
        int i = 0;

        for ( PageEntity page : pages )
        {
            keys[i++] = page.getKey();
        }

        return keys;
    }

    private int getNextKey( final String tableName )
    {
        return keyService.generateNextKeySafe( tableName );
    }
}
