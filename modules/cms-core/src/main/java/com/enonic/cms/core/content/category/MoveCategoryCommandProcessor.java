package com.enonic.cms.core.content.category;


import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;

class MoveCategoryCommandProcessor
{
    private UserEntity user;

    private CategoryEntity categoryToMove;

    private CategoryEntity destinationCategory;

    private CategoryAccessResolver accessResolver;

    MoveCategoryCommandProcessor( GroupDao groupDao )
    {
        this.accessResolver = new CategoryAccessResolver( groupDao );
    }

    void setUser( UserEntity user )
    {
        this.user = user;
    }

    void setCategoryToMove( CategoryEntity categoryToMove )
    {
        this.categoryToMove = categoryToMove;
    }

    void setDestinationCategory( CategoryEntity destinationCategory )
    {
        this.destinationCategory = destinationCategory;
    }

    void moveCategory()
    {

        if ( !accessResolver.hasDeleteCategoryAccess( user, categoryToMove.getParent() ) )
        {
            throw new CategoryAccessException( "Cannot delete sub-categories in category", user.getQualifiedName(),
                                               CategoryAccessType.ADMINISTRATE, categoryToMove.getParent().getKey() );
        }
        if ( !accessResolver.hasAdministrateCategoryAccess( user, destinationCategory ) )
        {
            throw new CategoryAccessException( "Cannot create sub-categories in category", user.getQualifiedName(),
                                               CategoryAccessType.ADMINISTRATE, destinationCategory.getKey() );
        }
        if ( destinationCategory.isSubCategoryOf( categoryToMove ) )
        {
            throw new IllegalArgumentException( "Cannot move a category to a subcategory" );
        }

        doMoveCategory();
    }

    private void doMoveCategory()
    {
        // 1. remove child from existing parent
        CategoryEntity oldParent = categoryToMove.getParent();
        oldParent.removeChild( categoryToMove );

        // 2. add reference to new parent
        categoryToMove.setParent( destinationCategory );

        UnitEntity unit = destinationCategory.getUnitExcludeDeleted();
        categoryToMove.setUnitOnDescendants( unit );
    }
}
