package com.enonic.cms.web.portal;

import com.enonic.cms.core.SitePathResolver;

public final class PortalSitePathResolver
    extends SitePathResolver
{
    public PortalSitePathResolver()
    {
        setSitePathPrefix( "/site" );
    }
}
