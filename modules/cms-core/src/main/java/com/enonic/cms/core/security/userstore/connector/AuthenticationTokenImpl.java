package com.enonic.cms.core.security.userstore.connector;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationToken;
import com.enonic.cms.core.security.userstore.UserStoreKey;

final class AuthenticationTokenImpl
    implements AuthenticationToken
{
    private final String userStore;

    private final String userName;

    private final String password;

    public AuthenticationTokenImpl( final String userStore, final String userName, final String password )
    {
        this.userStore = userStore;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getUserStore()
    {
        return this.userStore;
    }

    @Override
    public String getUserName()
    {
        return this.userName;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }
}
