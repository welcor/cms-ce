package com.enonic.cms.web.portal.page;


import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.base.Preconditions;

import com.enonic.esl.util.DigestUtil;

import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.api.plugin.ext.http.HttpProcessor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;
import com.enonic.cms.core.SiteBasePath;
import com.enonic.cms.core.SiteBasePathAndSitePath;
import com.enonic.cms.core.SiteBasePathAndSitePathToStringBuilder;
import com.enonic.cms.core.SiteBasePathResolver;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.PortalRenderingException;
import com.enonic.cms.core.portal.PortalRequest;
import com.enonic.cms.core.portal.PortalResponse;
import com.enonic.cms.core.portal.RedirectInstruction;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.web.portal.SiteRedirectAndForwardHelper;
import com.enonic.cms.web.portal.instanttrace.InstantTraceId;
import com.enonic.cms.web.portal.instanttrace.InstantTraceResponseWriter;
import com.enonic.cms.web.portal.instanttrace.InstantTraceSessionInspector;
import com.enonic.cms.web.portal.instanttrace.InstantTraceSessionObject;

public class PortalResponseProcessor
{
    private final static String EXECUTED_PLUGINS = "EXECUTED_PLUGINS";

    private static final int SECOND_IN_MILLIS = 1000;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private List<HttpResponseFilter> responseFilters;

    private PortalRequest request;

    private PortalResponse response;

    private HttpSession httpSession;

    private HttpServletResponse httpResponse;

    private HttpServletRequest httpRequest;

    private boolean inPreview;

    private boolean renderTraceOn;

    private boolean cacheHeadersEnabledForSite = false;

    private boolean forceNoCacheForSite = false;

    private boolean deviceClassificationEnabled = false;

    private boolean localizationEnabled = false;

    private boolean instantTraceEnabled = false;

    private PortalRequestTrace currentPortalRequestTrace;

    public void serveResponse()
        throws Exception
    {
        if ( response.hasRedirectInstruction() )
        {
            serveRedirect();
        }
        else if ( response.isForwardToSitePath() )
        {
            serveForwardToSitePathResponse();
        }
        else
        {
            servePageResponse();
        }
    }

    private void servePageResponse()
        throws IOException
    {
        HttpServletUtil.setDateHeader( httpResponse, request.getRequestTime().toDate() );

        boolean forceNoCache = false;

        if ( inPreview || renderTraceOn )
        {
            forceNoCache = true;
            final DateTime expirationTime = request.getRequestTime();
            setHttpCacheHeaders( request.getRequestTime(), expirationTime, forceNoCache );
        }
        else if ( cacheHeadersEnabledForSite )
        {
            final DateTime expirationTime = resolveExpirationTime( request.getRequestTime(), response.getExpirationTime() );
            setHttpCacheHeaders( request.getRequestTime(), expirationTime, forceNoCache );
        }

        // filter response with any response plugins
        String content = filterResponseWithPlugins( response.getContent(), response.getHttpContentType() );
        response.setContent( content );

        boolean isHeadRequest = "HEAD".compareToIgnoreCase( httpRequest.getMethod() ) == 0;
        boolean writeContent = !isHeadRequest;
        boolean handleEtagLogic = cacheHeadersEnabledForSite && !forceNoCacheForSite && !instantTraceEnabled;

        if ( handleEtagLogic && !StringUtils.isEmpty( content ) ) // resolveEtag does not like empty strings
        {
            // Handling etag logic if cache headers are enabled
            final String etagFromContent = resolveEtag( content );

            HttpServletUtil.setEtag( httpResponse, etagFromContent );

            if ( !isContentModified( etagFromContent ) )
            {
                httpResponse.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
                writeContent = false;
            }
        }

        if ( instantTraceEnabled && currentPortalRequestTrace != null )
        {
            final InstantTraceSessionObject instantTraceSessionObject =
                InstantTraceSessionInspector.getInstantTraceSessionObject( httpSession );
            final InstantTraceId instantTraceId = new InstantTraceId( currentPortalRequestTrace.getCompletedNumber() );
            instantTraceSessionObject.addTrace( instantTraceId, currentPortalRequestTrace );
            InstantTraceResponseWriter.applyInstantTraceId( httpResponse, instantTraceId );
        }

        httpResponse.setContentType( response.getHttpContentType() );

        if ( isHeadRequest )
        {
            httpResponse.setContentLength( response.getContentAsBytes().length );
        }

        if ( writeContent )
        {
            writeContent( response.getContentAsBytes() );
        }
    }

    private String resolveEtag( String content )
    {
        Preconditions.checkArgument( StringUtils.isNotEmpty( content ) );
        return "content_" + DigestUtil.generateSHA( content );
    }

    private boolean isContentModified( String etagFromContent )
    {
        return HttpServletUtil.isContentModifiedAccordingToIfNoneMatchHeader( httpRequest, etagFromContent );
    }

    private void writeContent( byte[] content )
        throws IOException
    {
        httpResponse.setContentLength( content.length );

        OutputStream out = httpResponse.getOutputStream();
        out.write( content );
    }

    private void setHttpCacheHeaders( final DateTime requestTime, final DateTime expirationTime, final boolean forceNoCache )
    {
        final Interval maxAge = new Interval( requestTime, expirationTime );

        @SuppressWarnings({"UnnecessaryLocalVariable"}) boolean notCachableByClient = forceNoCache;
        if ( notCachableByClient )
        {
            HttpServletUtil.setCacheControlNoCache( httpResponse );
        }
        else
        {
            HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();

            // To eliminate proxy caching of pages (decided by TSI)
            cacheControlSettings.publicAccess = false;

            boolean setCacheTimeToZero = dynamicResolversEnabled();

            if ( setCacheTimeToZero )
            {
                cacheControlSettings.maxAgeSecondsToLive = new Long( 0 );
                HttpServletUtil.setExpiresHeader( httpResponse, requestTime.toDate() );
            }
            else
            {
                cacheControlSettings.maxAgeSecondsToLive = maxAge.toDurationMillis() / SECOND_IN_MILLIS;
                HttpServletUtil.setExpiresHeader( httpResponse, expirationTime.toDate() );
            }

            HttpServletUtil.setCacheControl( httpResponse, cacheControlSettings );
        }
    }

    private boolean dynamicResolversEnabled()
    {
        return deviceClassificationEnabled || localizationEnabled;
    }

    private void serveRedirect()
        throws IOException
    {

        RedirectInstruction redirectInstruction = response.getRedirectInstruction();

        int redirectStatus =
            redirectInstruction.isPermanentRedirect() ? HttpServletResponse.SC_MOVED_PERMANENTLY : HttpServletResponse.SC_MOVED_TEMPORARILY;

        if ( redirectInstruction.hasRedirectSitePath() )
        {
            serveRedirectToSitePath( redirectInstruction.getRedirectSitePath(), redirectStatus );
        }
        else if ( redirectInstruction.hasRedirectUrl() )
        {
            serveRedirectResponse( redirectInstruction.getRedirectUrl(), redirectStatus );
        }
        else
        {
            throw new IllegalStateException( "Redirect must have target url or sitepath set" );
        }
    }

    private void serveRedirectToSitePath( final SitePath toSitePath, final int redirectStatus )
        throws IOException
    {
        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( httpRequest, toSitePath.getSiteKey() );
        SiteBasePathAndSitePath siteBasePathAndSitePath = new SiteBasePathAndSitePath( siteBasePath, toSitePath );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );
        String redirectUrl = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        sendRedirectResponse( redirectUrl, redirectStatus );
    }

    private void sendRedirectResponse( final String redirectUrl, final int redirectStatus )
    {
        String encodedRedirectUrl = httpResponse.encodeRedirectURL( redirectUrl );

        if ( redirectStatus == HttpServletResponse.SC_MOVED_PERMANENTLY )
        {
            httpResponse.setStatus( redirectStatus );
            httpResponse.setHeader( "Location", encodedRedirectUrl );
        }
        else
        {
            httpResponse.setStatus( HttpServletResponse.SC_MOVED_TEMPORARILY );
            httpResponse.setHeader( "Location", encodedRedirectUrl );
        }
    }

    private void serveForwardToSitePathResponse()
        throws Exception
    {
        siteRedirectAndForwardHelper.forward( httpRequest, httpResponse, response.getForwardToSitePath() );
    }

    private void serveRedirectResponse( final String redirectUrl, final int redirectStatus )
    {
        sendRedirectResponse( redirectUrl, redirectStatus );
    }

    private DateTime resolveExpirationTime( final DateTime requestTime, final DateTime expirationTime )
    {
        if ( expirationTime == null )
        {
            return requestTime;
        }

        if ( expirationTime.isBefore( requestTime ) )
        {
            return requestTime;
        }

        return expirationTime;
    }

    private String filterResponseWithPlugins( String response, final String contentType )
    {
        try
        {
            //noinspection unchecked
            Set<HttpProcessor> executedPlugins = (Set<HttpProcessor>) httpRequest.getAttribute( EXECUTED_PLUGINS );
            if ( executedPlugins == null )
            {
                executedPlugins = new HashSet<HttpProcessor>();
                httpRequest.setAttribute( EXECUTED_PLUGINS, executedPlugins );
            }

            for ( HttpResponseFilter plugin : responseFilters )
            {
                if ( !executedPlugins.contains( plugin ) )
                {
                    response = plugin.filterResponse( httpRequest, response, contentType );
                    executedPlugins.add( plugin );
                }

            }

            return response;
        }
        catch ( Exception e )
        {
            throw new PortalRenderingException( "Response filter plugin failed: " + e.getMessage(), e );
        }
    }

    public void setSiteRedirectAndForwardHelper( final SiteRedirectAndForwardHelper siteRedirectAndForwardHelper )
    {
        this.siteRedirectAndForwardHelper = siteRedirectAndForwardHelper;
    }

    public void setRequest( final PortalRequest request )
    {
        this.request = request;
    }

    public void setResponse( final PortalResponse response )
    {
        this.response = response;
    }

    public void setHttpSession( final HttpSession httpSession )
    {
        this.httpSession = httpSession;
    }

    public void setHttpResponse( final HttpServletResponse httpResponse )
    {
        this.httpResponse = httpResponse;
    }

    public void setHttpRequest( final HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public void setInPreview( final boolean inPreview )
    {
        this.inPreview = inPreview;
    }

    public void setRenderTraceOn( final boolean renderTraceOn )
    {
        this.renderTraceOn = renderTraceOn;
    }

    public void setCacheHeadersEnabledForSite( final boolean cacheHeadersEnabledForSite )
    {
        this.cacheHeadersEnabledForSite = cacheHeadersEnabledForSite;
    }

    public void setForceNoCacheForSite( final boolean forceNoCacheForSite )
    {
        this.forceNoCacheForSite = forceNoCacheForSite;
    }

    public void setDeviceClassificationEnabled( final boolean deviceClassificationEnabled )
    {
        this.deviceClassificationEnabled = deviceClassificationEnabled;
    }

    public void setLocalizationEnabled( final boolean localizationEnabled )
    {
        this.localizationEnabled = localizationEnabled;
    }

    public void setResponseFilters( final List<HttpResponseFilter> responseFilters )
    {
        this.responseFilters = responseFilters;
    }

    public void setInstantTraceEnabled( final boolean instantTraceEnabled )
    {
        this.instantTraceEnabled = instantTraceEnabled;
    }

    public void setCurrentPortalRequestTrace( final PortalRequestTrace currentPortalRequestTrace )
    {
        this.currentPortalRequestTrace = currentPortalRequestTrace;
    }
}
