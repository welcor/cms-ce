package com.enonic.cms.core.portal.livetrace;

import com.enonic.cms.core.security.user.QualifiedUsername;

public class User
{
    private String userStoreName;

    private String userName;

    static User createUser( QualifiedUsername qualifiedUsername )
    {
        final User user = new User();
        if ( qualifiedUsername.getUserStoreName() != null )
        {
            user.userStoreName = qualifiedUsername.getUserStoreName();
        }
        else if ( qualifiedUsername.getUserStoreKey() != null )
        {
            user.userStoreName = qualifiedUsername.getUserStoreKey().toString();
        }
        user.userName = qualifiedUsername.getUsername();
        return user;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUserStoreName()
    {
        return userStoreName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getUserName()
    {
        return userName;
    }
}
