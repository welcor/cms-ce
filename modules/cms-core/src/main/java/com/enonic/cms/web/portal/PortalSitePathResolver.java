package com.enonic.cms.web.portal;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.structure.SitePathResolver;

@Component("sitePathResolver")
public final class PortalSitePathResolver
    extends SitePathResolver
{
    public PortalSitePathResolver()
    {
        setSitePathPrefix( "/site" );
    }
}
