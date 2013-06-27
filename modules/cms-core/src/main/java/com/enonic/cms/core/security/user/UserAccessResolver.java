/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.userstore.UserStoreAccessResolver;

public class UserAccessResolver
{
    private UserStoreAccessResolver userStoreAccessResolver;

    public UserAccessResolver( UserStoreAccessResolver userStoreAccessResolver )
    {
        this.userStoreAccessResolver = userStoreAccessResolver;
    }

    public boolean hasReadUserAccess( UserEntity reader, UserEntity user )
    {
        if ( userStoreAccessResolver.hasReadUserAccess( reader, user.getUserStore() ) )
        {
            return true;
        }

        if ( reader.equals( user ) )
        {
            return true;
        }

        return false;
    }
}
