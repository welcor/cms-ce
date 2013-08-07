/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el.accessors;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

/**
 * used for performance tests
 */
public final class UserAccessorNoReflection
   implements Accessor<String>
{
    private final UserAccessor user;

    public UserAccessorNoReflection( final UserEntity user )
    {
        this.user = new UserAccessor( user );
    }

    public String getValue( final String name )
    {
        if ( "qualifiedName".equals( name ) )
        {
            return user.getQualifiedName();
        }
        if ( "userStore".equals( name ) )
        {
            return user.getUserStore();
        }
        if ( "key".equals( name ) )
        {
            return user.getKey();
        }
        if ( "uid".equals( name ) )
        {
            return user.getUid();
        }
        if ( "fullName".equals( name ) )
        {
            return user.getFullName();
        }
        if ( "email".equals( name ) )
        {
            return user.getEmail();
        }

        return null;
    }
}
