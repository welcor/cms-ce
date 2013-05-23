package com.enonic.cms.web.portal.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.portal.RedirectInstruction;
import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.SiteRedirectAndForwardHelper;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class PageRedirectHandler
    extends WebHandlerBase
{
    private PortalRenderResponseService portalRenderResponseService;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private MenuItemDao menuItemDao;

    @Override
    protected boolean canHandle( final Path localPath )
    {
        return localPath.endsWith( "/page" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest httpRequest = context.getRequest();
        final HttpServletResponse httpResponse = context.getResponse();
        final SitePath sitePath = context.getSitePath();

        String id = sitePath.getParam( "id" );

        // redirect to new path or forward to old page servlet
        if ( id == null )
        {
            // site/x/[...]/page shall show the front page
            SitePath indexPageSitePath = new SitePath( sitePath.getSiteKey(), Path.ROOT, sitePath.getParams() );
            siteRedirectAndForwardHelper.forward( httpRequest, httpResponse, indexPageSitePath );
            return;
        }

        MenuItemKey menuItemKey = new MenuItemKey( id );
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }

        SitePath newPagePath = new SitePath( sitePath.getSiteKey(), menuItem.getPath(), sitePath.getParams() );

        // Remove id-parameter since this is not valid in the redirect
        newPagePath.removeParam( "id" );

        PortalRequest request = new PortalRequest();
        request.setSitePath( newPagePath );

        RedirectInstruction redirectInstruction = new RedirectInstruction( newPagePath );
        redirectInstruction.setPermanentRedirect( true );

        PortalResponse response = PortalResponse.createRedirect( redirectInstruction );

        portalRenderResponseService.serveResponse( request, response, httpResponse, httpRequest, null );
    }

    @Autowired
    public void setPortalRenderResponseService( final PortalRenderResponseService portalRenderResponseService )
    {
        this.portalRenderResponseService = portalRenderResponseService;
    }

    @Autowired
    public void setSiteRedirectAndForwardHelper( final SiteRedirectAndForwardHelper siteRedirectAndForwardHelper )
    {
        this.siteRedirectAndForwardHelper = siteRedirectAndForwardHelper;
    }

    @Autowired
    public void setMenuItemDao( final MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }
}
