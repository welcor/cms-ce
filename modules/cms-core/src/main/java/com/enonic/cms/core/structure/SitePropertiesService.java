/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

public interface SitePropertiesService
{
    void registerSitePropertiesListener( SitePropertiesListener listener );

    void reloadSiteProperties( SiteKey siteKey );

    SiteProperties getSiteProperties( SiteKey siteKey );

}
