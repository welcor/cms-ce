/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public final class UserAccessor
{
    private final UserEntity user;

    public UserAccessor( final UserEntity user )
    {
        this.user = user;
    }

    public String getQualifiedName()
    {
        final String uid = getUid();
        final String userStoreName = getUserStore();
        return userStoreName.length() > 0 ? userStoreName + "\\" + uid : uid;
    }

    public String getUserStore()
    {
        final UserStoreEntity userStore = user.getUserStore();
        return userStore == null ? "" : userStore.getName();
    }

    public String getKey()
    {
        return user.getKey().toString();
    }

    public String getUid()
    {
        return user.getName();
    }

    public String getFullName()
    {
        return user.getDisplayName();
    }

    public String getEmail()
    {
        return user.getEmail();
    }

}
