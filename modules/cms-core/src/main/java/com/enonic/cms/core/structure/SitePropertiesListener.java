/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure;


public interface SitePropertiesListener
{
    public void sitePropertiesLoaded( SiteProperties siteProperties );

    public void sitePropertiesReloaded( SiteProperties siteProperties );
}
