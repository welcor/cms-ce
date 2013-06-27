/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.product;

import com.enonic.cms.api.Version;

public final class ProductVersion
{
    private final static boolean ENTERPRISE = checkForEnterprise();

    public static String getTitle()
    {
        return Version.getTitle();
    }

    public static boolean isEnterprise()
    {
        return ENTERPRISE;
    }

    public static String getEdition()
    {
        return isEnterprise() ? "Enterprise" : "Community";
    }

    public static String getFullTitle()
    {
        return getTitle() + " " + getEdition();
    }

    public static String getFullTitleAndVersion()
    {
        return getFullTitle() + " " + getVersion();
    }

    public static String getVersion()
    {
        return Version.getVersion();
    }

    public static String getCopyright()
    {
        return Version.getCopyright();
    }

    private static boolean checkForEnterprise()
    {
        try
        {
            Class.forName( "com.enonic.cms.ee.Bootstrap" );
            return true;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }
}
