/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource.access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

@Component
public final class ResourceAccessResolver
{
    @Autowired
    private MemberOfResolver memberOfResolver;

    public boolean hasAccessToResourceTree( final UserEntity executor )
    {
        return memberOfResolver.hasDeveloperPowers( executor );
    }
}
