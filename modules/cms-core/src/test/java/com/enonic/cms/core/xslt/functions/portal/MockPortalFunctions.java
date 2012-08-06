package com.enonic.cms.core.xslt.functions.portal;

import com.google.common.base.Joiner;

import com.enonic.cms.core.xslt.lib.PortalFunctionsMediator;

public class MockPortalFunctions
    implements PortalFunctionsMediator
{
    @Override
    public String getInstanceKey()
    {
        return "instance-key";
    }

    @Override
    public boolean isWindowEmpty( final String windowKey, final String[] params )
    {
        return true;
    }

    @Override
    public boolean isWindowInline()
    {
        return true;
    }

    @Override
    public String getPageKey()
    {
        return "page-key";
    }

    @Override
    public String getWindowKey()
    {
        return "window-key";
    }

    private String toRepresentation( final String[] array )
    {
        return "(" + Joiner.on( ',' ).join( array ) + ")";
    }

    @Override
    public String createWindowPlaceholder( final String windowKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createWindowPlaceholder," );
        str.append( windowKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createUrl( final String local, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createUrl," );
        str.append( local ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createWindowUrl( final String windowKey, final String[] params, final String outputFormat )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createWindowUrl," );
        str.append( windowKey ).append( "," ).append( toRepresentation( params ) );
        str.append( "," ).append( outputFormat );
        return str.toString();
    }

    @Override
    public String createPageUrl( final String menuItemKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createPageUrl," );
        str.append( menuItemKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createContentUrl( final String contentKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createContentUrl," );
        str.append( contentKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createPermalink( final String contentKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createPermalink," );
        str.append( contentKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createServicesUrl( final String handler, final String operation, final String[] params, final String redirect )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createServicesUrl," );
        str.append( handler ).append( "," ).append( operation );
        str.append( "," ).append( toRepresentation( params ) ).append( "," ).append( redirect );
        return str.toString();
    }

    @Override
    public String createBinaryUrl( final String binaryKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createBinaryUrl," );
        str.append( binaryKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createAttachmentUrl( final String nativeLinkKey, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createAttachmentUrl," );
        str.append( nativeLinkKey ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createResourceUrl( final String resourcePath, final String[] params )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createResourceUrl," );
        str.append( resourcePath ).append( "," ).append( toRepresentation( params ) );
        return str.toString();
    }

    @Override
    public String createCaptchaImageUrl()
    {
        return "captcha-image-url";
    }

    @Override
    public String createCaptchaFormInputName()
    {
        return "captcha-form-input-name";
    }

    @Override
    public boolean isCaptchaEnabled( final String handler, final String operation )
    {
        return true;
    }

    @Override
    public String localize( final String phrase, final String[] params, final String locale )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "localize," );
        str.append( phrase ).append( "," ).append( toRepresentation( params ) );
        str.append( "," ).append( locale );
        return str.toString();
    }

    @Override
    public String getLocale()
    {
        return "locale";
    }

    @Override
    public String createImageUrl( final String key, final String filter, final String background, final String format,
                                  final String quality )
    {
        final StringBuilder str = new StringBuilder();
        str.append( "createImageUrl," );
        str.append( key ).append( "," ).append( filter ).append( "," ).append( background );
        str.append( "," ).append( format ).append( "," ).append( quality );
        return str.toString();
    }

    @Override
    public boolean imageExists( final String key )
    {
        return true;
    }

    @Override
    public String md5( final String value )
    {
        return "md5," + value;
    }

    @Override
    public String sha( final String value )
    {
        return "sha," + value;
    }
}
