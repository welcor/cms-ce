package com.enonic.cms.core.structure.page.template;

public class DeletePageTemplateCommand
{
    private int key;

    public DeletePageTemplateCommand( final int key )
    {
        this.key = key;
    }

    public int getKey()
    {
        return key;
    }
}
