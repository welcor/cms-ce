package com.enonic.cms.core.product;

import org.joda.time.DateTime;

public final class Product
{
    private static ProductInfo INFO = new ProductInfoCE();

    public static String getTitle()
    {
        return INFO.getTitle();
    }

    public static String getFullTitle()
    {
        return getTitle() + " " + INFO.getEdition();
    }

    public static String getFullTitleAndVersion()
    {
        return getFullTitle() + " " + INFO.getVersion();
    }
    
    public static String getVersion()
    {
        return INFO.getVersion();
    }
    
    public static String getCopyright()
    {
        return INFO.getCopyright();
    }

    public static boolean isLicenseValid()
    {
        return INFO.getLicense().isValid();
    }

    public static DateTime getLicenseExpireDate()
    {
        return INFO.getLicense().getExpireDate();
    }

    public static ProductInfo getInfo()
    {
        return INFO;
    }

    public static void setInfo( final ProductInfo info )
    {
        INFO = info;
    }
}
