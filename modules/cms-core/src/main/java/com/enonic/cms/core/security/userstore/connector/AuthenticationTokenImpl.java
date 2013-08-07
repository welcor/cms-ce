package com.enonic.cms.core.security.userstore.connector;

import com.enonic.cms.api.plugin.ext.auth.AuthenticationToken;
import com.enonic.cms.core.security.userstore.UserStoreKey;

final class AuthenticationTokenImpl
    implements AuthenticationToken
{
    private final UserStoreKey userStore;

    private final String userName;

    private final String password;

    public AuthenticationTokenImpl( final UserStoreKey userStore, final String userName, final String password )
    {
        this.userStore = userStore;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String getUserStore()
    {
        return this.userStore != null ? userStore.toString() : null;
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
