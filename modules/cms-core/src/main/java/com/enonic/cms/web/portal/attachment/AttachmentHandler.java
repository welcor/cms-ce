package com.enonic.cms.web.portal.attachment;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.io.ByteStreams;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.util.HttpServletUtil;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.binary.AttachmentNotFoundException;
import com.enonic.cms.core.content.binary.AttachmentRequest;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.portal.PathRequiresAuthenticationException;
import com.enonic.cms.core.portal.ReservedLocalPaths;
import com.enonic.cms.core.portal.livetrace.AttachmentRequestTrace;
import com.enonic.cms.core.portal.livetrace.AttachmentRequestTracer;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.core.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class AttachmentHandler
    extends WebHandlerBase
{
    private BinaryDataDao binaryDataDao;

    private AttachmentRequestResolverImpl attachmentRequestResolver;

    private MimeTypeResolver mimeTypeResolver;

    @PostConstruct
    public void init()
    {
        this.attachmentRequestResolver = new AttachmentRequestResolverImpl( this.contentDao );
    }

    @Override
    protected boolean canHandle( final String localPath )
    {
        return localPath.contains( "/_attachment/" ) || localPath.endsWith( "/_attachment" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SitePath currentSitePath = context.getSitePath();

        PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( context.getOriginalUrl(), livePortalTraceService );
        try
        {
            PortalRequestTracer.traceMode( portalRequestTrace, previewService );
            PortalRequestTracer.traceHttpRequest( portalRequestTrace, request );

            PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, currentSitePath );
            PortalRequestTracer.traceRequestedSite( portalRequestTrace, siteDao.findByKey( currentSitePath.getSiteKey() ) );

            try
            {
                handleRequestInternal( request, response, currentSitePath, portalRequestTrace );
            }
            catch ( Exception e )
            {
                throw new AttachmentRequestException( context.getOriginalSitePath(), context.getReferrerHeader(), e );
            }
        }
        finally
        {
            PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
        }
    }

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath,
                                                  PortalRequestTrace portalRequestTrace )
        throws Exception
    {

        UserEntity loggedInUser = userDao.findByKey( securityService.getLoggedInPortalUser().getKey() );
        if ( loggedInUser.isAnonymous() )
        {
            if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED, sitePath.getSiteKey() ) )
            {
                loggedInUser = autoLoginService.autologinWithRemoteUser( request );
            }
        }
        if ( loggedInUser.isAnonymous() )
        {
            if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_REMEMBER_ME_COOKIE_ENABLED,
                                                             sitePath.getSiteKey() ) )
            {
                loggedInUser = autoLoginService.autologinWithCookie( sitePath.getSiteKey(), request, response );
            }
        }

        PortalRequestTracer.traceRequester( portalRequestTrace, loggedInUser );

        final AttachmentRequestTrace attachmentRequestTrace = AttachmentRequestTracer.startTracing( livePortalTraceService );

        try
        {
            final AttachmentRequest attachmentRequest = attachmentRequestResolver.resolveBinaryDataKey( sitePath.getPathAndParams() );
            AttachmentRequestTracer.traceAttachmentRequest( attachmentRequestTrace, attachmentRequest );

            verifyValidMenuItemInPath( sitePath );

            boolean downloadRequested = resolveDownloadRequested( request );

            final ContentEntity content = resolveContent( attachmentRequest, sitePath );
            checkContentAccess( loggedInUser, content, downloadRequested, sitePath );
            checkContentIsOnline( content, sitePath );

            final ContentVersionEntity contentVersion = resolveContentVersion( content );
            final ContentBinaryDataEntity contentBinaryData = resolveContentBinaryData( contentVersion, attachmentRequest, sitePath );
            final BinaryDataEntity binaryData = contentBinaryData.getBinaryData();

            setHttpHeaders( request, response, sitePath, loggedInUser );

            final BlobRecord blob = binaryDataDao.getBlob( binaryData.getBinaryDataKey() );
            if ( blob == null )
            {
                throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
            }

            putBinaryOnResponse( downloadRequested, response, binaryData, blob, attachmentRequestTrace );
            return null;
        }
        finally
        {
            AttachmentRequestTracer.stopTracing( attachmentRequestTrace, livePortalTraceService );
        }
    }

    private void verifyValidMenuItemInPath( SitePath sitePath )
    {
        SiteEntity site = siteDao.findByKey( sitePath.getSiteKey() );

        Path menuItemPath = getAttachmentMenuItemPath( sitePath );

        MenuItemEntity menuItem = site.resolveMenuItemByPath( menuItemPath );

        if ( menuItem == null )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
    }


    private Path getAttachmentMenuItemPath( SitePath sitePath )
    {
        String pathAsString = sitePath.getLocalPath().toString();

        if ( !pathAsString.contains( ReservedLocalPaths.PATH_ATTACHMENT.toString() ) )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }

        int i = pathAsString.lastIndexOf( ReservedLocalPaths.PATH_ATTACHMENT.toString() );

        String menuItemPathAsString = pathAsString.substring( 0, i );

        return new Path( menuItemPathAsString );
    }

    private void setHttpHeaders( final HttpServletRequest request, final HttpServletResponse response, final SitePath sitePath,
                                 final UserEntity requester )
    {
        final DateTime now = new DateTime();
        HttpServletUtil.setDateHeader( response, now.toDate() );

        final boolean cacheHeadersEnabled =
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_ENABLED, sitePath.getSiteKey() );

        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache =
                sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_FORCENOCACHE,
                                                            sitePath.getSiteKey() );

            if ( forceNoCache || isInPreviewMode( request ) )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge =
                    sitePropertiesService.getPropertyAsInteger( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_MAXAGE, sitePath.getSiteKey() );
                boolean publicAccess = requester.isAnonymous();
                enableHttpCacheHeaders( response, sitePath, now, siteCacheSettingsMaxAge, publicAccess );
            }
        }
    }

    private void putBinaryOnResponse( boolean download, HttpServletResponse response, BinaryDataEntity binaryData, final BlobRecord blob,
                                      final AttachmentRequestTrace trace )
        throws IOException
    {
        AttachmentRequestTracer.traceSize( trace, blob.getLength() );
        HttpServletUtil.setContentDisposition( response, download, binaryData.getName() );

        response.setContentType( this.mimeTypeResolver.getMimeType( binaryData.getName() ) );
        response.setContentLength( (int) blob.getLength() );

        ByteStreams.copy( blob.getStream(), response.getOutputStream() );
    }

    private boolean isInPreviewMode( HttpServletRequest httpRequest )
    {
        String previewEnabled = (String) httpRequest.getAttribute( Attribute.PREVIEW_ENABLED );
        return "true".equals( previewEnabled );
    }

    private ContentEntity resolveContent( AttachmentRequest attachmentRequest, SitePath sitePath )
    {
        final ContentEntity content = contentDao.findByKey( attachmentRequest.getContentKey() );
        if ( content == null || content.isDeleted() )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
        return content;
    }

    private ContentVersionEntity resolveContentVersion( ContentEntity content )
    {
        return content.getMainVersion();
    }

    private ContentBinaryDataEntity resolveContentBinaryData( ContentVersionEntity contentVersion, AttachmentRequest attachmentRequest,
                                                              SitePath sitePath )
    {
        final ContentBinaryDataEntity contentBinaryData = contentVersion.getContentBinaryData( attachmentRequest.getBinaryDataKey() );
        if ( contentBinaryData == null )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
        return contentBinaryData;
    }

    private boolean resolveDownloadRequested( HttpServletRequest request )
    {
        boolean downloadRequested = "true".equals( request.getParameter( "download" ) );
        downloadRequested = downloadRequested || "true".equals( request.getParameter( "_download" ) );
        return downloadRequested;
    }

    private void checkContentIsOnline( final ContentEntity content, final SitePath sitePath )
    {
        if ( previewService.isInPreview() )
        {
            PreviewContext previewContext = previewService.getPreviewContext();
            if ( previewContext.isPreviewingContent() &&
                previewContext.getContentPreviewContext().treatContentAsAvailableEvenIfOffline( content.getKey() ) )
            {
                // when in preview, the content doesn't need to be online
                return;
            }
        }

        if ( !content.isOnline( timeService.getNowAsDateTime() ) )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
    }

    private void checkContentAccess( final UserEntity loggedInUser, final ContentEntity content, final boolean downloadRequested,
                                     final SitePath sitePath )
    {
        boolean noAccessToContent = !new ContentAccessResolver( groupDao ).hasReadContentAccess( loggedInUser, content );
        if ( noAccessToContent )
        {
            if ( loggedInUser.isAnonymous() && downloadRequested )
            {
                throw new PathRequiresAuthenticationException( sitePath );
            }
            else
            {
                throw AttachmentNotFoundException.noAccess( sitePath.getLocalPath().toString() );
            }
        }
    }

    @Autowired
    public void setBinaryDataDao( final BinaryDataDao binaryDataDao )
    {
        this.binaryDataDao = binaryDataDao;
    }

    @Autowired
    public void setMimeTypeResolver( final MimeTypeResolver mimeTypeResolver )
    {
        this.mimeTypeResolver = mimeTypeResolver;
    }
}
