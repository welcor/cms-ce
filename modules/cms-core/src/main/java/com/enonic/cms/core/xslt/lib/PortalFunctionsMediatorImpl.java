package com.enonic.cms.core.xslt.lib;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionException;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctions;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.core.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.WindowKey;

@Component
public final class PortalFunctionsMediatorImpl
    implements PortalFunctionsMediator
{
    public static final String UNDEFINED = "[undefined]";

    private static final Logger LOG = LoggerFactory.getLogger( PortalFunctionsMediatorImpl.class );

    private PortalFunctionsFactory portalFunctionsFactory;

    @Autowired
    public void setPortalFunctionsFactory( final PortalFunctionsFactory portalFunctionsFactory )
    {
        this.portalFunctionsFactory = portalFunctionsFactory;
    }

    private PortalFunctions createPortalFunctions()
    {
        return this.portalFunctionsFactory.createPortalFunctions();
    }

    @Override
    public String getInstanceKey()
    {
        try
        {
            return createPortalFunctions().getInstanceKey();
        }
        catch ( final Exception e )
        {
            return handleException( "getInstanceKey", e );
        }
    }

    @Override
    public boolean isWindowEmpty( final String windowKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().isWindowEmpty( windowKey, params );
        }
        catch ( final Exception e )
        {
            handleException( "isWindowEmpty", e );
            return false;
        }
    }

    @Override
    public boolean isWindowInline()
    {
        try
        {
            return createPortalFunctions().isWindowInline();
        }
        catch ( final Exception e )
        {
            handleException( "isWindowInline", e );
            return false;
        }
    }

    @Override
    public String getPageKey()
    {
        try
        {
            return createPortalFunctions().getPageKey();
        }
        catch ( final Exception e )
        {
            return handleException( "getPageKey", e );
        }
    }

    @Override
    public String getWindowKey()
    {
        try
        {
            return createPortalFunctions().getWindowKey();
        }
        catch ( final Exception e )
        {
            return handleException( "getWindowKey", e );
        }
    }

    @Override
    public String createWindowPlaceholder( final String windowKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().createWindowPlaceholder( windowKey, params );
        }
        catch ( final Exception e )
        {
            return handleException( "createWindowPlaceholder", e );
        }
    }

    @Override
    public String createUrl( final String local, final String[] params )
    {
        try
        {
            return createPortalFunctions().createUrl( local, params );
        }
        catch ( final Exception e )
        {
            return handleException( "createUrl", e );
        }
    }

    @Override
    public String createWindowUrl( final String windowKey, final String[] params, final String outputFormat )
    {
        final boolean windowKeyGiven = !Strings.isNullOrEmpty( windowKey );
        final boolean paramsGiven = params != null && params.length > 0;
        final boolean outputFormatGiven = !Strings.isNullOrEmpty( outputFormat );

        try
        {
            if ( !windowKeyGiven )
            {
                if ( !paramsGiven )
                {
                    return createPortalFunctions().createWindowUrl();
                }
                else
                {
                    return createPortalFunctions().createWindowUrl( params );
                }
            }
            else
            {
                if ( outputFormatGiven )
                {
                    return createPortalFunctions().createWindowUrl( new WindowKey( windowKey ), params, outputFormat );
                }
                else
                {
                    return createPortalFunctions().createWindowUrl( new WindowKey( windowKey ), params );
                }

            }
        }

        catch ( final Exception e )
        {
            return handleException( "createWindowUrl", e );
        }
    }

    @Override
    public String createPageUrl( final String menuItemKey, final String[] params )
    {
        try
        {
            if ( menuItemKey == null )
            {
                return createPortalFunctions().createPageUrl( params );
            }

            return createPortalFunctions().createPageUrl( new MenuItemKey( menuItemKey ), params );
        }
        catch ( final Exception e )
        {
            return handleException( "createPageUrl", e );
        }
    }

    @Override
    public String createContentUrl( final String contentKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().createContentUrl( new ContentKey( contentKey ), params );
        }
        catch ( final Exception e )
        {
            return handleException( "createContentUrl", e );
        }
    }

    @Override
    public String createPermalink( final String contentKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().createPermalink( new ContentKey( contentKey ), params );
        }
        catch ( final Exception e )
        {
            return handleException( "createPermalink", e );
        }
    }

    @Override
    public String createServicesUrl( final String handler, final String operation, final String[] params, final String redirect )
    {
        try
        {
            return createPortalFunctions().createServicesUrl( handler, operation, redirect, params );
        }
        catch ( final Exception e )
        {
            return handleException( "createServicesUrl", e );
        }
    }

    @Override
    public String createBinaryUrl( final String binaryKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().createBinaryUrl( new BinaryDataKey( binaryKey ), params );
        }
        catch ( final Exception e )
        {
            return handleException( "createBinaryUrl", e );
        }
    }

    @Override
    public String createAttachmentUrl( final String nativeLinkKey, final String[] params )
    {
        try
        {
            return createPortalFunctions().createAttachmentUrl( nativeLinkKey, params );
        }
        catch ( final Exception e )
        {
            return handleException( "createAttachmentUrl", e );
        }
    }

    @Override
    public String createResourceUrl( final String resourcePath, final String[] params )
    {
        try
        {
            return createPortalFunctions().createResourceUrl( resourcePath, params );
        }
        catch ( final Exception e )
        {
            return handleException( "createResourceUrl", e );
        }
    }

    @Override
    public String createCaptchaImageUrl()
    {
        try
        {
            return createPortalFunctions().createCaptchaImageUrl();
        }
        catch ( final Exception e )
        {
            return handleException( "createCaptchaImageUrl", e );
        }
    }

    @Override
    public String createCaptchaFormInputName()
    {
        try
        {
            return createPortalFunctions().createCaptchaFormInputName();
        }
        catch ( final Exception e )
        {
            return handleException( "createCaptchaFormInputName", e );
        }
    }

    @Override
    public boolean isCaptchaEnabled( final String handler, final String operation )
    {
        try
        {
            return createPortalFunctions().isCaptchaEnabled( handler, operation );
        }
        catch ( final Exception e )
        {
            handleException( "isCaptchaEnabled", e );
            return false;
        }
    }

    @Override
    public String localize( final String phrase, final String[] params, final String locale )
    {
        try
        {
            if ( ( params == null ) && ( locale == null ) )
            {
                return createPortalFunctions().localize( phrase );
            }
            else if ( locale != null )
            {
                return createPortalFunctions().localize( phrase, params, locale );
            }
            else
            {
                return createPortalFunctions().localize( phrase, params );
            }
        }
        catch ( final Exception e )
        {
            return handleException( "localize", e );
        }
    }

    @Override
    public String getLocale()
    {
        try
        {
            return createPortalFunctions().getLocale();
        }
        catch ( final Exception e )
        {
            return handleException( "getLocale", e );
        }
    }

    @Override
    public String createImageUrl( final String key, final String filter, final String background, final String format,
                                  final String quality )
    {
        try
        {
            return createPortalFunctions().createImageUrl( key, filter, background, format, quality );
        }
        catch ( final Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    @Override
    public boolean imageExists( final String key )
    {
        try
        {
            return createPortalFunctions().imageExists( key );
        }
        catch ( final Exception e )
        {
            handleException( "imageExists", e );
            return false;
        }
    }

    @Override
    public String md5( final String value )
    {
        return DigestUtils.md5Hex( value );
    }

    @Override
    public String sha( final String value )
    {
        return DigestUtils.shaHex( value );
    }

    private static String buildFailureMessage( String functionName, String failureReason )
    {
        final PortalFunctionsContext portalFunctionsContext = PortalFunctionsFactory.get().getContext();

        final StringBuilder message = new StringBuilder();
        message.append( "Failure calling function " ).append( functionName );
        MenuItemEntity menuItem = portalFunctionsContext.getMenuItem();
        if ( menuItem != null )
        {
            message.append( " during request to [" );
            message.append( menuItem.getPathAsString() ).append( "]" );
        }
        SiteEntity site = portalFunctionsContext.getSite();
        if ( site != null )
        {
            message.append( " in site [" );
            message.append( site.getName() ).append( "]" );
        }

        message.append( ". Reason: " );
        message.append( failureReason );
        return message.toString();
    }

    private static String handleException( final String functionName, final Exception e )
    {
        final String failureReason = resolveFailureReason( e );
        final String failureMessage = buildFailureMessage( functionName, failureReason );

        LOG.warn( failureMessage );
        return UNDEFINED + ": " + failureReason;
    }

    private static String resolveFailureReason( final Exception e )
    {
        final String failureReason;

        if ( e instanceof PortalFunctionException )
        {
            PortalFunctionException pfe = (PortalFunctionException) e;
            failureReason = pfe.getFailureReason();
        }
        else
        {
            failureReason = e.getMessage();
        }
        return failureReason;
    }
}
