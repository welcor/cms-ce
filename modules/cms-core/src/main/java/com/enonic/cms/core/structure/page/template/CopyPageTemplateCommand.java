package com.enonic.cms.core.structure.page.template;

import com.enonic.cms.core.security.user.UserKey;

public class CopyPageTemplateCommand
{
    private final PageTemplateKey pageTemplateKey ;

    private final UserKey copierKey;

    public CopyPageTemplateCommand( final PageTemplateKey pageTemplateKey, final UserKey copierKey )
    {
        this.pageTemplateKey = pageTemplateKey;
        this.copierKey = copierKey;
    }

    public PageTemplateKey getPageTemplateKey()
    {
        return pageTemplateKey;
    }

    public UserKey getCopierKey()
    {
        return copierKey;
    }
}
