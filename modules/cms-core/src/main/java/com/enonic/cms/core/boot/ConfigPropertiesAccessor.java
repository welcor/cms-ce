package com.enonic.cms.core.boot;

final class ConfigPropertiesAccessor
{
    private static ConfigProperties INSTANCE;

    public static ConfigProperties get()
    {
        return INSTANCE;
    }

    public static void set( final ConfigProperties props )
    {
        INSTANCE = props;
    }
}
