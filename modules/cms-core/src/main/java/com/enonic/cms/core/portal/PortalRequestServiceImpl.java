/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.processor.PageRequestProcessorFactory;
import com.enonic.cms.core.portal.rendering.PageRendererFactory;
import com.enonic.cms.core.portal.rendering.WindowRendererFactory;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

@Service
public final class PortalRequestServiceImpl
    implements PortalRequestService
{
    private SiteDao siteDao;

    private PortletDao portletDao;

    private UserDao userDao;

    private ContentDao contentDao;

    private LanguageDao languageDao;

    private PageRendererFactory pageRendererFactory;

    private WindowRendererFactory windowRendererFactory;

    private PortalAccessService portalAccessService;

    private PageRequestProcessorFactory pageRequestProcessorFactory;

    private LivePortalTraceService liveTraceService;

    public PortalResponse processRequest( final PortalRequest request )
    {
        PortalRequestProcessor portalRequestProcessor = new PortalRequestProcessor( request );
        portalRequestProcessor.setContentDao( contentDao );
        portalRequestProcessor.setLanguageDao( languageDao );
        portalRequestProcessor.setLiveTraceService( liveTraceService );
        portalRequestProcessor.setPageRendererFactory( pageRendererFactory );
        portalRequestProcessor.setPageRequestProcessorFactory( pageRequestProcessorFactory );
        portalRequestProcessor.setPortalAccessService( portalAccessService );
        portalRequestProcessor.setPortletDao( portletDao );
        portalRequestProcessor.setSiteDao( siteDao );
        portalRequestProcessor.setUserDao( userDao );
        portalRequestProcessor.setWindowRendererFactory( windowRendererFactory );

        return portalRequestProcessor.processRequest();
    }

    @Autowired
    public void setPageRendererFactory( PageRendererFactory value )
    {
        this.pageRendererFactory = value;
    }

    @Autowired
    public void setWindowRendererFactory( WindowRendererFactory windowRendererFactory )
    {
        this.windowRendererFactory = windowRendererFactory;
    }

    @Autowired
    public void setLanguageDao( LanguageDao languageDao )
    {
        this.languageDao = languageDao;
    }

    @Autowired
    public void setPortalAccessService( PortalAccessService portalAccessService )
    {
        this.portalAccessService = portalAccessService;
    }

    @Autowired
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setPortletDao( PortletDao portletDao )
    {
        this.portletDao = portletDao;
    }

    @Autowired
    public void setPageRequestProcessorFactory( PageRequestProcessorFactory value )
    {
        this.pageRequestProcessorFactory = value;
    }

    @Autowired
    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setLivePortalTraceService( LivePortalTraceService liveTraceService )
    {
        this.liveTraceService = liveTraceService;
    }
}
