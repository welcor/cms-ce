/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

class UpdateCategoryAccessChecker
{
    private MemberOfResolver memberOfResolver;

    private CategoryAccessResolver categoryAccessResolver;

    private UserEntity updater;

    UpdateCategoryAccessChecker( MemberOfResolver memberOfResolver, CategoryAccessResolver categoryAccessResolver )
    {
        this.memberOfResolver = memberOfResolver;
        this.categoryAccessResolver = categoryAccessResolver;
    }

    UpdateCategoryAccessChecker updater( UserEntity updater )
    {
        this.updater = updater;
        return this;
    }

    void checkAccessToUpdateCategory( CategoryEntity category )
        throws UpdateCategoryAccessException
    {
        if ( memberOfResolver.hasAdministratorPowers( updater ) )
        {
            return;
        }

        if ( !categoryAccessResolver.hasAccess( updater, category, CategoryAccessType.ADMINISTRATE ) )
        {
            throw new UpdateCategoryAccessException(
                "To update category the user needs to have the administrate access on the category or be an administrator",
                updater.getQualifiedName() );

        }
    }
}
