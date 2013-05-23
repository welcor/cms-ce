package com.enonic.cms.core.admin;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.structure.SitePathResolver;

@Component
public class PreviewSitePathResolver
    extends SitePathResolver
{
    public PreviewSitePathResolver()
    {
        setSitePathPrefix( "/preview" );
    }
}
