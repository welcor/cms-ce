package com.enonic.cms.core.structure.page.template;

public class DeletePageTemplateCommand
{
    private PageTemplateKey pageTemplateKey;

    public DeletePageTemplateCommand( final PageTemplateKey pageTemplateKey )
    {
        this.pageTemplateKey = pageTemplateKey;
    }

    public PageTemplateKey getPageTemplateKey()
    {
        return pageTemplateKey;
    }
}
