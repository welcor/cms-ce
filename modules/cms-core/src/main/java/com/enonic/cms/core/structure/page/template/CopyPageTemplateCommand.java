package com.enonic.cms.core.structure.page.template;

import com.enonic.cms.core.security.user.User;

public class CopyPageTemplateCommand
{
    private int key;

    private User user;

    public CopyPageTemplateCommand( final int key, final User user )
    {
        this.key = key;
        this.user = user;
    }

    public int getKey()
    {
        return key;
    }

    public User getUser()
    {
        return user;
    }
}
