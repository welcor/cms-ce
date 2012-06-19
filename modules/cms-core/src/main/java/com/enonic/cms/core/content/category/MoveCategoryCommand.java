package com.enonic.cms.core.content.category;


import com.enonic.cms.core.security.user.UserKey;

public class MoveCategoryCommand
{
    private UserKey user;

    private CategoryKey categoryToMove;

    private CategoryKey destinationCategory;

    public UserKey getUser()
    {
        return user;
    }

    public void setUser( UserKey user )
    {
        this.user = user;
    }

    public CategoryKey getCategoryToMove()
    {
        return categoryToMove;
    }

    public void setCategoryToMove( CategoryKey categoryToMove )
    {
        this.categoryToMove = categoryToMove;
    }

    public void setDestinationCategory( CategoryKey destinationCategory )
    {
        this.destinationCategory = destinationCategory;
    }

    public CategoryKey getDestinationCategory()
    {
        return destinationCategory;
    }
}