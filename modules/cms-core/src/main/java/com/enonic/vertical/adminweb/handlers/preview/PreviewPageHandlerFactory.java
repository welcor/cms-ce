package com.enonic.vertical.adminweb.handlers.preview;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.core.portal.rendering.PageRendererFactory;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;

@Component
public class PreviewPageHandlerFactory
{

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private PortletDao portletDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private TimeService timeService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PageRendererFactory pageRendererFactory;

    @Autowired
    private LocaleResolverService localeResolverService;

    @Autowired
    private DeviceClassResolverService deviceClassResolverService;

    @Value("${cms.name.transliterate}")
    private boolean transliterate;

    @Autowired
    private PreviewService previewService;

    public PreviewPageHandler create( final HttpServletRequest httpRequest, final ExtendedMap formItems )
    {
        PreviewPageHandler previewPageHandler = new PreviewPageHandler();
        previewPageHandler.setPreviewService( previewService );
        previewPageHandler.setTicketId( httpRequest.getSession().getId() );
        previewPageHandler.setTransliterate( transliterate );
        previewPageHandler.setHttpRequest( httpRequest );
        previewPageHandler.setContentDao( contentDao );
        previewPageHandler.setLanguageDao( languageDao );
        previewPageHandler.setMenuItemDao( menuItemDao );
        previewPageHandler.setPageTemplateDao( pageTemplateDao );
        previewPageHandler.setPortletDao( portletDao );
        previewPageHandler.setSiteDao( siteDao );
        previewPageHandler.setTimeService( timeService );
        previewPageHandler.setPreviewer( securityService.getLoggedInAdminConsoleUserAsEntity() );
        previewPageHandler.setLocaleResolverService( localeResolverService );
        previewPageHandler.setDeviceClassResolverService( deviceClassResolverService );
        previewPageHandler.setPageRendererFactory( pageRendererFactory );
        previewPageHandler.setFormItems( formItems );
        return previewPageHandler;
    }
}
