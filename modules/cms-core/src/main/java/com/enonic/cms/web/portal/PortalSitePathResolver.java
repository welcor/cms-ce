package com.enonic.cms.web.portal;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.SitePathResolver;

@Component
public final class PortalSitePathResolver
    extends SitePathResolver
{
    public PortalSitePathResolver()
    {
        setSitePathPrefix( "/site" );
    }
}
