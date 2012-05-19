package com.enonic.cms.web.portal.render;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalRequestService;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.portal.RedirectInstruction;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.core.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.server.service.servlet.OriginalUrlResolver;
import com.enonic.cms.web.portal.handler.WebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class DefaultHandler
    extends WebHandlerBase
{
    private PortalRequestService portalRequestService;

    private PortalRenderResponseServer portalRenderResultServer;

    @Override
    protected boolean canHandle( final String localPath )
    {
        return true;
    }

    @Override
    protected void doHandle( final WebContext context )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SitePath sitePath = context.getSitePath();

        try
        {
            handleDefaultRequest( request, response, sitePath );
        }
        catch ( Exception e )
        {
            SitePath originalSitePath = context.getOriginalSitePath();
            throw new DefaultRequestException( originalSitePath, context.getReferrerHeader(), e );
        }
    }

    private ModelAndView handleDefaultRequest( HttpServletRequest httpRequest, HttpServletResponse httpResponse, SitePath sitePath )
        throws Exception
    {
        HttpSession httpSession = httpRequest.getSession( true );

        if ( !sitePath.getLocalPath().startsWithSlash() )
        {
            return redirectToRoot( httpRequest, httpResponse, sitePath );
        }

        String originalUrl = OriginalUrlResolver.get().resolveOriginalUrl( httpRequest );
        SitePath originalSitePath = (SitePath) httpRequest.getAttribute( Attribute.ORIGINAL_SITEPATH );

        final PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( originalUrl, livePortalTraceService );

        try
        {
            PortalRequestTracer.traceMode( portalRequestTrace, previewService );
            PortalRequestTracer.traceHttpRequest( portalRequestTrace, httpRequest );
            PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, sitePath );

            PortalRequest request = new PortalRequest();
            request.setRequestTime( timeService.getNowAsDateTime() );
            request.setSitePath( sitePath );
            request.setRequestParams( getRequestParameters( httpRequest ) );
            request.setTicketId( httpSession.getId() );
            request.setOriginalSitePath( originalSitePath );
            request.setVerticalSession( getAndEnsureVerticalSessionOnHttpSession( httpSession ) );
            request.setHttpReferer( httpRequest.getHeader( "referer" ) );
            request.setOriginalUrl( originalUrl );

            User loggedInPortalUser = securityService.getLoggedInPortalUser();
            if ( loggedInPortalUser.isAnonymous() )
            {
                if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED,
                                                                 sitePath.getSiteKey() ) )
                {
                    loggedInPortalUser = autoLoginService.autologinWithRemoteUser( httpRequest );
                }
            }
            if ( loggedInPortalUser.isAnonymous() )
            {
                if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_REMEMBER_ME_COOKIE_ENABLED,
                                                                 sitePath.getSiteKey() ) )
                {
                    loggedInPortalUser = autoLoginService.autologinWithCookie( sitePath.getSiteKey(), httpRequest, httpResponse );
                }
            }
            request.setRequester( loggedInPortalUser.getKey() );
            request.setPreviewContext( previewService.getPreviewContext() );

            PortalResponse response = portalRequestService.processRequest( request );

            return portalRenderResultServer.serveResponse( request, response, httpResponse, httpRequest );

        }
        finally
        {
            PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
        }
    }

    private ModelAndView redirectToRoot( HttpServletRequest httpRequest, HttpServletResponse httpResponse, SitePath sitePath )
        throws Exception
    {
        sitePath = sitePath.createNewInSameSite( Path.ROOT, sitePath.getParams() );
        PortalRequest request = new PortalRequest();
        request.setRequestTime( new DateTime() );
        request.setSitePath( sitePath );
        request.setRequestParams( getRequestParameters( httpRequest ) );

        RedirectInstruction redirectInstruction = new RedirectInstruction( sitePath );
        redirectInstruction.setPermanentRedirect( true );

        PortalResponse response = PortalResponse.createRedirect( redirectInstruction );
        return portalRenderResultServer.serveResponse( request, response, httpResponse, httpRequest );
    }

    private HashMap<String, Object> getRequestParameters( HttpServletRequest request )
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        Enumeration parameterNames = request.getParameterNames();
        while ( parameterNames.hasMoreElements() )
        {

            String name = (String) parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues( name );

            if ( parameterValues.length == 1 )
            {
                String parameter = parameterValues[0];
                parameters.put( name, parameter );
            }
            else
            {
                parameters.put( name, parameterValues );
            }
        }

        return parameters;
    }

    private VerticalSession getAndEnsureVerticalSessionOnHttpSession( HttpSession httpSession )
    {
        VerticalSession vsession = (VerticalSession) httpSession.getAttribute( VerticalSession.VERTICAL_SESSION_OBJECT );
        if ( vsession == null )
        {
            vsession = new VerticalSession();
            httpSession.setAttribute( VerticalSession.VERTICAL_SESSION_OBJECT, vsession );
        }
        return vsession;
    }

    @Autowired
    public void setPortalRequestService( final PortalRequestService portalRequestService )
    {
        this.portalRequestService = portalRequestService;
    }

    @Autowired
    public void setPortalRenderResultServer( final PortalRenderResponseServer portalRenderResultServer )
    {
        this.portalRenderResultServer = portalRenderResultServer;
    }
}
