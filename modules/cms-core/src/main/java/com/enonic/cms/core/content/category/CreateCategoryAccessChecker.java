/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

class CreateCategoryAccessChecker
{
    private MemberOfResolver memberOfResolver;

    private CategoryAccessResolver categoryAccessResolver;

    private UserEntity creator;

    CreateCategoryAccessChecker( MemberOfResolver memberOfResolver, CategoryAccessResolver categoryAccessResolver )
    {
        this.memberOfResolver = memberOfResolver;
        this.categoryAccessResolver = categoryAccessResolver;
    }

    CreateCategoryAccessChecker creator( UserEntity creator )
    {
        this.creator = creator;
        return this;
    }

    void checkAccessToCreateContentArchive()
        throws CreateCategoryAccessException
    {
        // needs at least administrator rights
        if ( !memberOfResolver.hasAdministratorPowers( creator ) )
        {
            throw new CreateCategoryAccessException( "To create a content archive the user needs to be an administrator",
                                                     creator.getQualifiedName() );
        }
    }

    void checkAccessToCreateCategory( CategoryEntity parentCategory )
        throws CreateCategoryAccessException
    {

        final boolean noAdministrateAccessByRights =
            !categoryAccessResolver.hasAccess( creator, parentCategory, CategoryAccessType.ADMINISTRATE );

        if ( noAdministrateAccessByRights )
        {
            if ( !memberOfResolver.hasAdministratorPowers( creator ) )
            {
                throw new CreateCategoryAccessException(
                    "To create a category the user needs to have the administrate access on the parent category or be an administrator",
                    creator.getQualifiedName() );
            }
        }
    }
}
