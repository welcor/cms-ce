/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.cache;

import com.enonic.cms.core.structure.SiteKey;

public interface PageCacheService
{

    void setUpPageCache( SiteKey siteKey );

    void reloadPageCacheConfig( SiteKey siteKey );

    void tearDownPageCache( SiteKey siteKey );

    PageCache getPageCacheService( SiteKey siteKey );

}
