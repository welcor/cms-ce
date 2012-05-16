package com.enonic.cms.core.admin;

import com.enonic.cms.core.SitePathResolver;

public class PreviewSitePathResolver
    extends SitePathResolver
{
    public PreviewSitePathResolver()
    {
        setSitePathPrefix( "/preview" );
    }
}
