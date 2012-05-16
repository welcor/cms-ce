package com.enonic.cms.core.admin;

import com.enonic.cms.core.SitePathResolver;

public class DebugSitePathResolver
    extends SitePathResolver
{
    public DebugSitePathResolver()
    {
        setSitePathPrefix( "/site" );
    }
}
