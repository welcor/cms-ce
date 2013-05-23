package com.enonic.cms.core.structure;


public interface SitePropertiesListener
{
    public void sitePropertiesLoaded( SiteProperties siteProperties );

    public void sitePropertiesReloaded( SiteProperties siteProperties );
}
