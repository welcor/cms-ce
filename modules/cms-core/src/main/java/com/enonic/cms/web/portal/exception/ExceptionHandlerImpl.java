package com.enonic.cms.web.portal.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.BadRequestErrorType;
import com.enonic.cms.core.InvalidKeyException;
import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.portal.AbstractBaseError;
import com.enonic.cms.core.portal.ClientError;
import com.enonic.cms.core.portal.ContentNameMismatchClientError;
import com.enonic.cms.core.portal.ContentNameMismatchException;
import com.enonic.cms.core.portal.ForbiddenErrorType;
import com.enonic.cms.core.portal.PathRequiresAuthenticationException;
import com.enonic.cms.core.portal.ResourceNotFoundException;
import com.enonic.cms.core.portal.ServerError;
import com.enonic.cms.core.portal.SiteErrorDetails;
import com.enonic.cms.core.portal.UnauthorizedErrorType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.SiteRedirectAndForwardHelper;
import com.enonic.cms.web.portal.attachment.AttachmentRequestException;
import com.enonic.cms.web.portal.image.ImageRequestException;
import com.enonic.cms.web.portal.page.DefaultRequestException;

@Component
public final class ExceptionHandlerImpl
    implements ExceptionHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( ExceptionHandlerImpl.class );

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private SiteURLResolver siteURLResolver;

    private MenuItemDao menuItemDao;

    private TemplateProcessor templateProcessor;

    private boolean detailInformation;

    private boolean logRequestInfoOnException;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper value )
    {
        this.siteRedirectAndForwardHelper = value;
    }

    @Autowired
    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    @Autowired
    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    @Override
    public void handle( final PortalWebContext context, final Exception outerException )
        throws ServletException, IOException
    {
        outerException.printStackTrace();
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        final Throwable causingExeption;
        if ( isExceptionAnyOfThose( outerException, new Class[]{DefaultRequestException.class, AttachmentRequestException.class,
            ImageRequestException.class} ) )
        {
            // Have to unwrap these exceptions to get the causing exception
            causingExeption = outerException.getCause();
        }
        else
        {
            causingExeption = outerException;
        }

        logException( outerException, causingExeption, request );
        AbstractBaseError error = getError( causingExeption );

        try
        {
            handleExceptions( context, causingExeption, error );
        }
        finally
        {
            response.setStatus( error.getStatusCode() );
        }
    }

    private void logException( Throwable outerException, Throwable causingException, HttpServletRequest request )
    {

        if ( isExceptionAnyOfThose( causingException, new Class[]{ResourceNotFoundException.class} ) )
        {
            ResourceNotFoundException resourceNotFoundException = (ResourceNotFoundException) causingException;
            boolean ignore = resourceNotFoundException.endsWithIgnoreCase( "favicon.ico" ) ||
                resourceNotFoundException.endsWithIgnoreCase( "robots.txt" );
            if ( ignore )
            {
                // skipping logging
                return;
            }
        }

        if ( isExceptionAnyOfThose( causingException, new Class[]{PathRequiresAuthenticationException.class} ) )
        {
            // skipping logging
            return;
        }

        final boolean outerExceptionIsPortalRequestException = isExceptionAnyOfThose( outerException,
                                                                                      new Class[]{DefaultRequestException.class,
                                                                                          AttachmentRequestException.class,
                                                                                          ImageRequestException.class} );
        final boolean innerExceptionIsQuietException =
            isExceptionAnyOfThose( causingException, new Class[]{StacktraceLoggingUnrequired.class} );

        if ( outerExceptionIsPortalRequestException && innerExceptionIsQuietException )
        {
            LOG.info( outerException.getMessage() );
        }
        else if ( isExceptionAnyOfThose( causingException, new Class[]{ForbiddenErrorType.class, UnauthorizedErrorType.class} ) )
        {
            LOG.debug( causingException.getMessage() );
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append( causingException.getMessage() ).append( "\n" );
            if ( this.logRequestInfoOnException )
            {
                message.append( buildRequestInfo( request ) );
            }
            LOG.error( message.toString(), causingException );
        }
    }

    private String buildRequestInfo( HttpServletRequest request )
    {

        StringBuffer s = new StringBuffer();
        s.append( "Request information:\n" );
        s.append( " - cms.originalURL: " ).append( request.getAttribute( Attribute.ORIGINAL_URL ) ).append( "\n" );
        s.append( " - cms.originalSitePath: " ).append( request.getAttribute( Attribute.ORIGINAL_SITEPATH ) ).append( "\n" );
        s.append( " - http.queryString: " ).append( request.getQueryString() ).append( "\n" );
        s.append( " - http.requestURI: " ).append( request.getRequestURI() ).append( "\n" );
        s.append( " - http.remoteAddress: " ).append( request.getRemoteAddr() ).append( "\n" );
        s.append( " - http.remoteHost: " ).append( request.getRemoteHost() ).append( "\n" );
        s.append( " - http.characterEncoding: " ).append( request.getCharacterEncoding() ).append( "\n" );
        s.append( " - http.header.User-Agent: " ).append( request.getHeader( "User-Agent" ) ).append( "\n" );
        s.append( " - http.header.Referer: " ).append( request.getHeader( "Referer" ) ).append( "\n" );
        return s.toString();
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    private AbstractBaseError getError( Throwable exception )
    {

        if ( exception instanceof BadRequestErrorType )
        {
            return new ClientError( HttpServletResponse.SC_BAD_REQUEST, exception.getMessage(), exception );
        }
        else if ( exception instanceof NotFoundErrorType )
        {
            if ( exception instanceof ContentNameMismatchException )
            {
                ContentNameMismatchException contentNameMismatchException = (ContentNameMismatchException) exception;

                return new ContentNameMismatchClientError( HttpServletResponse.SC_NOT_FOUND, exception.getMessage(), exception,
                                                           contentNameMismatchException.getContentKey(),
                                                           contentNameMismatchException.getRequestedContentName() );
            }

            return new ClientError( HttpServletResponse.SC_NOT_FOUND, exception.getMessage(), exception );
        }
        else if ( exception instanceof ForbiddenErrorType )
        {
            return new ClientError( HttpServletResponse.SC_FORBIDDEN, exception.getMessage(), exception );
        }
        else if ( exception instanceof UnauthorizedErrorType )
        {
            return new ClientError( HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage(), exception );
        }
        else
        {
            return new ServerError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(), exception );
        }
    }

    private void handleExceptions( PortalWebContext context, Throwable exception, AbstractBaseError error )
        throws ServletException, IOException
    {
        if ( isExceptionAnyOfThose( exception, new Class[]{InvalidKeyException.class} ) &&
            ( (InvalidKeyException) exception ).forClass( SiteKey.class ) )
        {
            serveExceptionPage( context, error );
            return;
        }
        else if ( exception instanceof UnauthorizedErrorType )
        {
            serveLoginPage( context );
            return;
        }

        try
        {
            if ( serveErrorPage( context, error ) )
            {
                return;
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get error page: " + e.getMessage(), e );
            serveExceptionPage( context, error );
            return;
        }

        serveExceptionPage( context, error );
    }

    private boolean serveErrorPage( PortalWebContext context, AbstractBaseError error )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SitePath sitePath = context.getSitePath();

        boolean siteExists = siteExists( sitePath.getSiteKey() );
        if ( siteExists && hasErrorPage( sitePath.getSiteKey().toInt() ) )
        {
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );
            int errorPageKey = getErrorPage( sitePath.getSiteKey().toInt() );
            SitePath errorPagePath = new SitePath( sitePath.getSiteKey(), new Path( resolveMenuItemPath( errorPageKey ) ) );
            final String statusCodeString = String.valueOf( error.getStatusCode() );
            errorPagePath.addParam( "http_status_code", statusCodeString );
            errorPagePath.addParam( "exception_message", error.getMessage() );

            if ( error instanceof ContentNameMismatchClientError )
            {
                ContentNameMismatchClientError contentNameMismatchClientError = (ContentNameMismatchClientError) error;
                errorPagePath.addParam( "content_key", contentNameMismatchClientError.getContentKey().toString() );
            }
            siteRedirectAndForwardHelper.forward( request, response, errorPagePath );
            return true;
        }

        return false;
    }

    private String resolveMenuItemPath( int menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        if ( menuItem == null )
        {
            return "";
        }
        return menuItem.getPathAsString();
    }

    private void serveExceptionPage( PortalWebContext context, AbstractBaseError e )
        throws IOException
    {
        if ( this.detailInformation )
        {
            serveFullExceptionPage( context, e );
            return;
        }

        serveMinimalExceptionPage( context, e );
    }

    private void serveMinimalExceptionPage( PortalWebContext context, AbstractBaseError e )
        throws IOException
    {
        serveExceptionPage( "errorPageMinimal.ftl", context, e );
    }

    private void serveFullExceptionPage( PortalWebContext context, AbstractBaseError e )
        throws IOException
    {
        serveExceptionPage( "errorPage.ftl", context, e );
    }

    private void serveExceptionPage( String templateName, PortalWebContext context, AbstractBaseError e )
        throws IOException
    {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put( "details", new SiteErrorDetails( context.getRequest(), e.getCause(), e.getStatusCode() ) );

        final String result = this.templateProcessor.process( templateName, model );
        context.getResponse().getWriter().println( result );
    }

    private void serveLoginPage( PortalWebContext context )
        throws ServletException, IOException
    {
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final SitePath unauthPageSitePath = context.getSitePath();

        SiteKey siteKey = unauthPageSitePath.getSiteKey();
        int menuItemKey = getLoginPage( siteKey.toInt() );

        if ( menuItemKey >= 0 )
        {

            Path loginPageLocalPath = new Path( resolveMenuItemPath( menuItemKey ) );
            SitePath loginPageSitePath = unauthPageSitePath.createNewInSameSite( loginPageLocalPath, unauthPageSitePath.getParams() );
            // remove the id param, because we may have got that from the unauthPageSitePath
            loginPageSitePath.removeParam( "id" );

            // we dont want the error code in the referer, so remove it
            unauthPageSitePath.removeParam( "error_user_login" );
            String referer = siteURLResolver.createUrl( request, unauthPageSitePath, true );
            loginPageSitePath.addParam( "referer", referer );

            siteRedirectAndForwardHelper.forward( request, response, loginPageSitePath );
        }
        else
        {
            response.setHeader( "WWW-Authenticate", "Basic" );
        }
    }

    private boolean isExceptionAnyOfThose( Throwable e, Class[] classes )
    {

        for ( Class cls : classes )
        {
            if ( cls.isInstance( e ) )
            {
                return true;
            }
        }
        return false;
    }

    private int getErrorPage( final int siteKey )
    {
        final SiteEntity entity = this.siteDao.findByKey( siteKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey().toInt();
        }
    }

    private boolean hasErrorPage( final int siteKey )
    {
        return getErrorPage( siteKey ) >= 0;
    }

    private int getLoginPage( final int siteKey )
    {
        final SiteEntity entity = this.siteDao.findByKey( siteKey );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey().toInt();
        }
    }

    private boolean siteExists( final SiteKey siteKey )
    {
        final SiteEntity site = this.siteDao.findByKey( siteKey.toInt() );
        return site != null;
    }

    @Autowired
    public void setTemplateProcessor( final TemplateProcessor templateProcessor )
    {
        this.templateProcessor = templateProcessor;
    }

    @Value("${cms.error.page.detailInformation}")
    public void setDetailInformation( final boolean value )
    {
        this.detailInformation = value;
    }

    @Value("${cms.render.logRequestInfoOnException}")
    public void setLogRequestInfoOnException( final boolean value )
    {
        this.logRequestInfoOnException = value;
    }
}
