package com.enonic.cms.web.portal.attachment;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.util.HttpServletRangeUtil;
import com.enonic.cms.framework.util.HttpServletUtil;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.Path;
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
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SitePropertyNames;
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
    protected boolean canHandle( final Path localPath )
    {
        return localPath.containsSubPath( "_attachment" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SitePath sitePath = context.getSitePath();
        final boolean downloadRequested;
        final UserEntity loggedInUser;
        final BlobRecord blob;
        final BinaryDataEntity binaryData;

        final PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( context.getOriginalUrl(), livePortalTraceService );
        try
        {
            try
            {
                PortalRequestTracer.traceMode( portalRequestTrace, previewService );
                PortalRequestTracer.traceHttpRequest( portalRequestTrace, request );
                PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, sitePath );
                PortalRequestTracer.traceRequestedSite( portalRequestTrace, siteDao.findByKey( sitePath.getSiteKey() ) );

                loggedInUser = resolveLoggedInUser( request, response, sitePath );
                PortalRequestTracer.traceRequester( portalRequestTrace, loggedInUser );

                final AttachmentRequestTrace attachmentRequestTrace = AttachmentRequestTracer.startTracing( livePortalTraceService );
                try
                {
                    verifyValidMenuItemInPath( sitePath );

                    final AttachmentRequest attachmentRequest =
                        attachmentRequestResolver.resolveBinaryDataKey( sitePath.getPathAndParams() );
                    AttachmentRequestTracer.traceAttachmentRequest( attachmentRequestTrace, attachmentRequest );

                    downloadRequested = resolveDownloadRequested( request );
                    final ContentEntity content = resolveContent( attachmentRequest, sitePath );
                    checkContentAccess( loggedInUser, content, downloadRequested, sitePath );
                    checkContentIsOnline( content, sitePath );

                    binaryData = resolveBinaryData( sitePath, attachmentRequest, content );
                    blob = binaryDataDao.getBlob( binaryData.getBinaryDataKey() );
                    if ( blob == null )
                    {
                        throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
                    }
                    AttachmentRequestTracer.traceSize( attachmentRequestTrace, blob.getLength() );
                }
                finally
                {
                    AttachmentRequestTracer.stopTracing( attachmentRequestTrace, livePortalTraceService );
                }
            }
            finally
            {
                PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
            }

            // serve response ...
            setHttpHeaders( request, response, sitePath, loggedInUser );
            putBinaryOnResponse( context, downloadRequested, binaryData, blob );
        }
        catch ( Exception e )
        {
            throw new AttachmentRequestException( context.getOriginalSitePath(), context.getReferrerHeader(), e );
        }
    }


    private UserEntity resolveLoggedInUser( final HttpServletRequest request, final HttpServletResponse response, final SitePath sitePath )
    {
        UserEntity loggedInUser = securityService.getLoggedInPortalUserAsEntity();
        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( sitePath.getSiteKey() );
        if ( loggedInUser.isAnonymous() )
        {
            if ( siteProperties.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED ) )
            {
                loggedInUser = autoLoginService.autologinWithRemoteUser( request );
            }
        }
        if ( loggedInUser.isAnonymous() )
        {
            if ( siteProperties.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_REMEMBER_ME_COOKIE_ENABLED ) )
            {
                loggedInUser = autoLoginService.autologinWithCookie( sitePath.getSiteKey(), request, response );
            }
        }
        return loggedInUser;
    }

    private BinaryDataEntity resolveBinaryData( final SitePath sitePath, final AttachmentRequest attachmentRequest,
                                                final ContentEntity content )
    {
        final ContentVersionEntity contentVersion = resolveContentVersion( content );
        final ContentBinaryDataEntity contentBinaryData = resolveContentBinaryData( contentVersion, attachmentRequest, sitePath );
        return contentBinaryData.getBinaryData();
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

        final SiteProperties siteProperties = sitePropertiesService.getSiteProperties( sitePath.getSiteKey() );
        final boolean cacheHeadersEnabled = siteProperties.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_ENABLED );

        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache = siteProperties.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_FORCENOCACHE );

            if ( forceNoCache || isInPreviewMode( request ) )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge = siteProperties.getPropertyAsInteger( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_MAXAGE );
                boolean publicAccess = requester.isAnonymous();
                enableHttpCacheHeaders( response, sitePath, now, siteCacheSettingsMaxAge, publicAccess );
            }
        }
    }

    private void putBinaryOnResponse( final PortalWebContext context, final boolean download, final BinaryDataEntity binaryData,
                                      final BlobRecord blob )
        throws IOException
    {
        final File file = blob.getAsFile();

        if ( file != null )
        {
            HttpServletRangeUtil.processRequest( context.getRequest(), context.getResponse(),
                                                 this.mimeTypeResolver.getMimeType( binaryData.getName() ), file );
        }
        else
        {
            final HttpServletResponse response = context.getResponse();
            HttpServletUtil.setContentDisposition( response, download, binaryData.getName() );

            response.setContentType( this.mimeTypeResolver.getMimeType( binaryData.getName() ) );
            response.setContentLength( (int) blob.getLength() );

            HttpServletUtil.copyNoCloseOut( blob.getStream(), response.getOutputStream() );
        }
    }

    private boolean isInPreviewMode( final HttpServletRequest httpRequest )
    {
        String previewEnabled = (String) httpRequest.getAttribute( Attribute.PREVIEW_ENABLED );
        return "true".equals( previewEnabled );
    }

    private ContentEntity resolveContent( final AttachmentRequest attachmentRequest, final SitePath sitePath )
    {
        final ContentEntity content = contentDao.findByKey( attachmentRequest.getContentKey() );
        if ( content == null || content.isDeleted() )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
        return content;
    }

    private ContentVersionEntity resolveContentVersion( final ContentEntity content )
    {
        return content.getMainVersion();
    }

    private ContentBinaryDataEntity resolveContentBinaryData( final ContentVersionEntity contentVersion,
                                                              final AttachmentRequest attachmentRequest, final SitePath sitePath )
    {
        final ContentBinaryDataEntity contentBinaryData = contentVersion.getContentBinaryData( attachmentRequest.getBinaryDataKey() );
        if ( contentBinaryData == null )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
        return contentBinaryData;
    }

    private boolean resolveDownloadRequested( final HttpServletRequest request )
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
