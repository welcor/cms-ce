package com.enonic.cms.core.xslt.functions.portal;

import org.springframework.stereotype.Component;
import com.enonic.cms.core.xslt.lib.PortalFunctions;

@Component
public final class PortalFunctionsMediatorImpl
    implements PortalFunctionsMediator
{
    @Override
    public String getInstanceKey()
    {
        return PortalFunctions.getInstanceKey();
    }

    @Override
    public boolean isWindowEmpty( final String windowKey, final String[] params )
    {
        return PortalFunctions.isWindowEmpty( windowKey, params );
    }

    @Override
    public boolean isWindowInline()
    {
        return PortalFunctions.isWindowInline();
    }

    @Override
    public String getPageKey()
    {
        return PortalFunctions.getPageKey();
    }

    @Override
    public String getWindowKey()
    {
        return PortalFunctions.getWindowKey();
    }

    @Override
    public String createWindowPlaceholder( final String windowKey, final String[] params )
    {
        return PortalFunctions.createWindowPlaceholder( windowKey, params );
    }

    @Override
    public String createUrl( final String local, final String[] params )
    {
        return PortalFunctions.createUrl( local, params );
    }

    @Override
    public String createWindowUrl( final String windowKey, final String[] params, final String outputFormat )
    {
        return PortalFunctions.createWindowUrl( windowKey, params, outputFormat );
    }

    @Override
    public String createPageUrl( final String menuItemKey, final String[] params )
    {
        return PortalFunctions.createPageUrl( menuItemKey, params );
    }

    @Override
    public String createContentUrl( final String contentKey, final String[] params )
    {
        return PortalFunctions.createContentUrl( contentKey, params );
    }

    @Override
    public String createPermalink( final String contentKey, final String[] params )
    {
        return PortalFunctions.createPermalink( contentKey, params );
    }

    @Override
    public String createServicesUrl( final String handler, final String operation, final String[] params, final String redirect )
    {
        return PortalFunctions.createServicesUrl( handler, operation, redirect, params );
    }

    @Override
    public String createBinaryUrl( final String binaryKey, final String[] params )
    {
        return PortalFunctions.createBinaryUrl( binaryKey, params );
    }

    @Override
    public String createAttachmentUrl( final String nativeLinkKey, final String[] params )
    {
        return PortalFunctions.createAttachmentUrl( nativeLinkKey, params );
    }

    @Override
    public String createResourceUrl( final String resourcePath, final String[] params )
    {
        return PortalFunctions.createResourceUrl( resourcePath, params );
    }

    @Override
    public String createCaptchaImageUrl()
    {
        return PortalFunctions.createCaptchaImageUrl();
    }

    @Override
    public String createCaptchaFormInputName()
    {
        return PortalFunctions.createCaptchaFormInputName();
    }

    @Override
    public boolean isCaptchaEnabled( final String handler, final String operation )
    {
        return PortalFunctions.isCaptchaEnabled( handler, operation ).equals( "true" );
    }

    @Override
    public String localize( final String phrase, final String[] params, final String locale )
    {
        return PortalFunctions.localize( phrase, params, locale );
    }

    @Override
    public String getLocale()
    {
        return PortalFunctions.getLocale();
    }

    @Override
    public String createImageUrl( final String key, final String filter, final String background, final String format,
                                  final String quality )
    {
        return PortalFunctions.createImageUrl( key, filter, background, format, quality );
    }

    @Override
    public boolean imageExists( final String key )
    {
        return PortalFunctions.imageExists( key );
    }

    @Override
    public String md5( final String value )
    {
        return PortalFunctions.md5( value );
    }

    @Override
    public String sha( final String value )
    {
        return PortalFunctions.sha( value );
    }
}
