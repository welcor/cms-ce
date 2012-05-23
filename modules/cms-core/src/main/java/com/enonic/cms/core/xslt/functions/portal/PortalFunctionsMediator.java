package com.enonic.cms.core.xslt.functions.portal;

public interface PortalFunctionsMediator
{
    public String getInstanceKey();

    public boolean isWindowEmpty( String windowKey, String[] params );

    public boolean isWindowInline();

    public String getPageKey();

    public String getWindowKey();

    public String createWindowPlaceholder( String windowKey, String[] params );

    public String createUrl( String local, String[] params );

    public String createWindowUrl( String windowKey, String[] params, String outputFormat );

    public String createPageUrl( String menuItemKey, String[] params );

    public String createContentUrl( String contentKey, String[] params );

    public String createPermalink( String contentKey, String[] params );

    public String createServicesUrl( String handler, String operation, String[] params, String redirect );

    public String createBinaryUrl( String binaryKey, String[] params );

    public String createAttachmentUrl( String nativeLinkKey, String[] params );

    public String createResourceUrl( String resourcePath, String[] params );

    public String createCaptchaImageUrl();

    public String createCaptchaFormInputName();

    public boolean isCaptchaEnabled( String handler, String operation );

    public String localize( String phrase, String[] params, String locale );

    public String getLocale();

    public String createImageUrl( String key, String filter, String background, String format, String quality );

    public boolean imageExists( String key );

    public String md5( String value );

    public String sha( String value );
}
