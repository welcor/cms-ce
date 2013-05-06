package com.enonic.vertical.adminweb.handlers.preview;


import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.servlet.http.HttpServletRequestWrapper;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.language.LanguageResolver;
import com.enonic.cms.core.portal.PageRequestType;
import com.enonic.cms.core.portal.PrettyPathNameCreator;
import com.enonic.cms.core.portal.rendering.PageRenderer;
import com.enonic.cms.core.portal.rendering.PageRendererContext;
import com.enonic.cms.core.portal.rendering.PageRendererFactory;
import com.enonic.cms.core.portal.rendering.RegionsResolver;
import com.enonic.cms.core.portal.rendering.RenderedPageResult;
import com.enonic.cms.core.preview.MenuItemPreviewContext;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemRequestParameter;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.PageWindowEntity;
import com.enonic.cms.core.structure.page.PageWindowKey;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;

public class PreviewPageHandler
{
    private static final String FORM_ITEM_DISPLAY_NAME = "displayname";

    private static final String FORM_ITEM_MENU_NAME = "menu-name";

    private HttpServletRequest httpRequest;

    private SiteDao siteDao;

    private PortletDao portletDao;

    private PageTemplateDao pageTemplateDao;

    private MenuItemDao menuItemDao;

    private ContentDao contentDao;

    private LanguageDao languageDao;

    private TimeService timeService;

    private LocaleResolverService localeResolverService;

    private DeviceClassResolverService deviceClassResolverService;

    private PreviewService previewService;

    private PageRendererFactory pageRendererFactory;

    private ExtendedMap formItems;

    private String ticketId;

    private UserEntity previewer;

    private boolean transliterate;

    public RenderedPageResult renderPreview( final SiteKey siteKey, final MenuItemKey parentKey, final MenuItemKey menuItemKey )
    {
        final MenuItemEntity menuItem = resolveModifiedMenuItem( siteKey, parentKey, menuItemKey );
        final PageTemplateEntity pageTemplate = menuItem.getPage().getTemplate();
        final Regions regionsInPage = RegionsResolver.resolveRegionsForPageRequest( menuItem, pageTemplate, PageRequestType.MENUITEM );

        final SitePath sitePath = new SitePath( menuItem.getSite().getKey(), menuItem.getPath() );
        sitePath.addParam( "id", menuItemKey.toString() );

        httpRequest.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );
        httpRequest.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        final HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( httpRequest );
        wrappedRequest.setServletPath( "/site" );
        wrappedRequest.setParameter( "id", menuItemKey.toString() );
        for ( MenuItemRequestParameter menuItemRequestParameter : menuItem.getRequestParameters().values() )
        {
            wrappedRequest.setParameter( menuItemRequestParameter.getName(), menuItemRequestParameter.getValue() );
        }
        ServletRequestAccessor.setRequest( wrappedRequest );

        final PreviewContext previewContext = new PreviewContext( new MenuItemPreviewContext( menuItem ) );
        previewService.setPreviewContext( previewContext );

        final UserEntity runAsUser = resolveRunAsUser( menuItem );
        final LanguageEntity language = LanguageResolver.resolve( menuItem.getSite(), menuItem );
        final ResolverContext resolverContext = new ResolverContext( wrappedRequest, menuItem.getSite(), menuItem, language );
        resolverContext.setUser( previewer );

        final Locale locale = localeResolverService.getLocale( resolverContext );
        final String deviceClass = deviceClassResolverService.getDeviceClass( resolverContext );

        // render page
        PageRendererContext pageRendererContext = new PageRendererContext();
        pageRendererContext.setDeviceClass( deviceClass );
        pageRendererContext.setForceNoCacheUsage( true );
        pageRendererContext.setHttpRequest( wrappedRequest );
        pageRendererContext.setLanguage( language );
        pageRendererContext.setLocale( locale );
        pageRendererContext.setMenuItem( menuItem );
        pageRendererContext.setOriginalSitePath( sitePath );
        pageRendererContext.setPageRequestType( PageRequestType.MENUITEM );
        pageRendererContext.setPreviewContext( previewContext );
        pageRendererContext.setRegionsInPage( regionsInPage );
        pageRendererContext.setRenderer( previewer );
        pageRendererContext.setRequestTime( new DateTime() );
        pageRendererContext.setRunAsUser( runAsUser );
        pageRendererContext.setTicketId( ticketId );
        pageRendererContext.setSite( menuItem.getSite() );
        pageRendererContext.setSitePath( sitePath );

        final PageRenderer renderer = pageRendererFactory.createPageRenderer( pageRendererContext );
        return renderer.renderPage( pageTemplate );
    }

    private MenuItemEntity resolveModifiedMenuItem( final SiteKey siteKey, final MenuItemKey parentKey, final MenuItemKey menuItemKey )
    {
        final MenuItemEntity persistedMenuItem = menuItemDao.findByKey( menuItemKey );
        MenuItemEntity modifiedMenuItem;
        if ( persistedMenuItem == null )
        {
            modifiedMenuItem = new MenuItemEntity();
            PageEntity newPage = new PageEntity();
            int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
            PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );
            newPage.setTemplate( pageTemplate );
            modifiedMenuItem.setPage( newPage );
            SiteEntity site = siteDao.findByKey( siteKey );
            modifiedMenuItem.setSite( site );
            modifiedMenuItem.setKey( new MenuItemKey( -1 ) );

            modifiedMenuItem.setParent( menuItemDao.findByKey( parentKey ) );
        }
        else
        {
            modifiedMenuItem = new MenuItemEntity( persistedMenuItem );
        }

        // create new menu-item with same values and modify with values from request
        modifiedMenuItem = modifyMenuItemForPreview( formItems, modifiedMenuItem );
        return modifiedMenuItem;
    }

    private MenuItemEntity modifyMenuItemForPreview( ExtendedMap formItems, MenuItemEntity menuItem )
        throws VerticalAdminException
    {

        menuItem.removeRequestParameters();

        if ( isArrayFormItem( formItems, "paramname" ) )
        {
            // there are multiple parameters
            String[] paramNames = (String[]) formItems.get( "paramname" );
            String[] paramVals = (String[]) formItems.get( "paramval" );
            String[] paramOverrides = (String[]) formItems.get( "paramoverride" );

            for ( int i = 0; i < paramNames.length; i++ )
            {
                String paramName = paramNames[i];
                String paramValue = paramVals[i];
                if ( paramName.length() == 0 || paramValue.length() == 0 )
                {
                    continue;
                }

                String paramOverride = paramOverrides[i];
                menuItem.addRequestParameter( paramName, paramValue, paramOverride );
            }
        }
        else
        {
            // there is only one (or zero) parameter
            String paramName = formItems.getString( "paramname", null );
            String paramVal = formItems.getString( "paramval", null );
            String paramOverride = formItems.getString( "paramoverride", null );

            if ( paramName != null || paramVal != null )
            {
                menuItem.addRequestParameter( paramName, paramVal, paramOverride );
            }
        }

        menuItem.setXmlData( menuItem.getMenuDataJDOMDocument() );

        // name
        String menuItemName = formItems.getString( "name", null ) != null ? formItems.getString( "name" ) : menuItem.getName();

        // display-name
        String displayName = formItems.getString( FORM_ITEM_DISPLAY_NAME, null );

        String menuName = formItems.getString( FORM_ITEM_MENU_NAME, null );

        menuItemName = ensureOrGenerateMenuItemName( menuItemName, displayName, menuName );

        menuItem.setName( menuItemName );

        menuItem.setDisplayName( displayName != null ? formItems.getString( FORM_ITEM_DISPLAY_NAME ) : menuItem.getDisplayName() );

        menuItem.setMenuName( menuName != null ? formItems.getString( FORM_ITEM_MENU_NAME ) : menuItem.getMenuName() );

        // type
        String type = formItems.getString( "type", "" );
        MenuItemType menuItemType = null;
        if ( type.equals( "form" ) )
        {
            menuItemType = MenuItemType.CONTENT;
        }
        else if ( type.equals( "section" ) )
        {
            menuItemType = MenuItemType.SECTION;
        }
        else if ( type.equals( "page" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "sectionpage" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "content" ) )
        {
            menuItemType = MenuItemType.CONTENT;
        }
        else if ( type.equals( "newsletter" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "label" ) )
        {
            menuItemType = MenuItemType.LABEL;
        }
        else if ( type.equals( "shortcut" ) )
        {
            menuItemType = MenuItemType.SHORTCUT;
        }
        else if ( "localurl".equals( type ) || "externalurl".equals( type ) )
        {
            menuItemType = MenuItemType.URL;
        }
        if ( menuItemType != null )
        {
            menuItem.setType( menuItemType );
        }

        // set runAs
        String runAs = formItems.getString( "runAs", "" );
        RunAsType runAsType = RunAsType.INHERIT;
        // automatically treat a form as a page
        if ( runAs.equals( "DEFAULT_USER" ) )
        {
            runAsType = RunAsType.DEFAULT_USER;
        }
        else if ( runAs.equals( "INHERIT" ) )
        {
            runAsType = RunAsType.INHERIT;
        }
        else if ( runAs.equals( "PERSONALIZED" ) )
        {
            runAsType = RunAsType.PERSONALIZED;
        }
        menuItem.setRunAs( runAsType );

        // set description
        menuItem.setDescription(
            formItems.getString( "description", null ) != null ? formItems.getString( "description" ) : menuItem.getDescription() );

        // set keywords
        menuItem.setKeywords(
            formItems.getString( "keywords", null ) != null ? formItems.getString( "keywords" ) : menuItem.getKeywords() );

        // set language
        if ( formItems.getString( "languagekey", null ) != null )
        {
            String languageKeyStr = formItems.getString( "languagekey" );
            LanguageKey languageKey = new LanguageKey( languageKeyStr );
            LanguageEntity language = languageDao.findByKey( languageKey );
            menuItem.setLanguage( language );
        }

        // set visibility:
        if ( "on".equals( formItems.getString( "visibility", null ) ) )
        {
            menuItem.setHidden( false );
        }
        else
        {
            menuItem.setHidden( true );
        }

        // timestamp
        menuItem.setTimestamp( timeService.getNowAsDateTime().toDate() );

        // content
        if ( formItems.containsKey( "_selected_content" ) && menuItem.getRequestParameterValue( "key" ) == null )
        {
            int contentKey = formItems.getInt( "_selected_content" );
            ContentEntity contentEntity = contentDao.findByKey( new ContentKey( contentKey ) );
            menuItem.setContent( contentEntity );
        }
        else if ( menuItem.getRequestParameterValue( "key" ) != null )
        {
            int contentKey = Integer.valueOf( menuItem.getRequestParameterValue( "key" ) );
            ContentEntity contentEntity = contentDao.findByKey( new ContentKey( contentKey ) );
            menuItem.setContent( contentEntity );
        }

        //page windows
        if ( menuItem.getPage() != null )
        {
            PageEntity modifiedPage = new PageEntity( menuItem.getPage() );
            menuItem.setPage( modifiedPage );
            modifiedPage.removeAllPortletPlacements();

            PageTemplateEntity pageTemplate = modifiedPage.getTemplate();
            Set<PageTemplateRegionEntity> pageTemplateRegions = pageTemplate.getPageTemplateRegions();
            for ( PageTemplateRegionEntity pageTemplateRegion : pageTemplateRegions )
            {
                String[] portletKeys = formItems.getStringArray( pageTemplateRegion.getName() + "_portlet" );
                if ( portletKeys.length > 0 )
                {
                    for ( String portletKeyStr : portletKeys )
                    {
                        if ( StringUtils.isBlank( portletKeyStr ) )
                        {
                            continue;
                        }

                        PortletEntity portlet = portletDao.findByKey( Integer.valueOf( portletKeyStr ) );

                        PageWindowEntity pageWindow = new PageWindowEntity();
                        pageWindow.setKey( new PageWindowKey( modifiedPage.getKey(), portlet.getKey() ) );
                        pageWindow.setPage( modifiedPage );
                        pageWindow.setPageTemplateRegion( pageTemplateRegion );
                        pageWindow.setTimestamp( timeService.getNowAsDateTime().toDate() );
                        pageWindow.setPortlet( portlet );
                        modifiedPage.addPortletPlacement( pageWindow );
                    }
                }
            }
        }

        return menuItem;
    }

    private String ensureOrGenerateMenuItemName( String menuItemName, String displayName, String menuName )
    {
        // Generate name for preview if none given
        if ( StringUtils.isEmpty( menuItemName ) )
        {
            String suggestedName = menuName;
            if ( StringUtils.isEmpty( suggestedName ) )
            {
                suggestedName = displayName;
            }

            menuItemName = new PrettyPathNameCreator( transliterate ).generatePrettyPathName( suggestedName );
        }
        return menuItemName;
    }

    private UserEntity resolveRunAsUser( final MenuItemEntity modifiedMenuItem )
    {
        UserEntity runAsUser = modifiedMenuItem.resolveRunAsUser( previewer, true );
        if ( runAsUser == null )
        {
            runAsUser = previewer;
        }
        return runAsUser;
    }

    private static boolean isArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return false;
        }

        if ( formItems.get( string ) == null )
        {
            return false;
        }

        return formItems.get( string ).getClass() == String[].class;
    }

    public void setPreviewService( final PreviewService previewService )
    {
        this.previewService = previewService;
    }

    public void setTicketId( final String ticketId )
    {
        this.ticketId = ticketId;
    }

    public void setHttpRequest( final HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setLocaleResolverService( final LocaleResolverService localeResolverService )
    {
        this.localeResolverService = localeResolverService;
    }

    public void setDeviceClassResolverService( final DeviceClassResolverService deviceClassResolverService )
    {
        this.deviceClassResolverService = deviceClassResolverService;
    }

    public void setPageRendererFactory( final PageRendererFactory pageRendererFactory )
    {
        this.pageRendererFactory = pageRendererFactory;
    }

    public void setFormItems( final ExtendedMap formItems )
    {
        this.formItems = formItems;
    }

    public void setMenuItemDao( final MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    public void setPortletDao( final PortletDao portletDao )
    {
        this.portletDao = portletDao;
    }

    public void setSiteDao( final SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setPageTemplateDao( final PageTemplateDao pageTemplateDao )
    {
        this.pageTemplateDao = pageTemplateDao;
    }

    public void setLanguageDao( final LanguageDao languageDao )
    {
        this.languageDao = languageDao;
    }

    public void setPreviewer( final UserEntity previewer )
    {
        this.previewer = previewer;
    }

    public void setTransliterate( final boolean transliterate )
    {
        this.transliterate = transliterate;
    }
}
