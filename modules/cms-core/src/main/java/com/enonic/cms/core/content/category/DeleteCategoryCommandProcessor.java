package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;

class DeleteCategoryCommandProcessor
{
    private UserEntity deleter;

    private CategoryEntity categoryToDelete;

    private boolean recursive;

    private boolean includeContent;

    private GroupDao groupDao;

    private ContentDao contentDao;

    private CategoryDao categoryDao;

    private ContentStorer contentStorer;

    private List<ContentEntity> deletedContent = new ArrayList<ContentEntity>();

    DeleteCategoryCommandProcessor( GroupDao groupDao, ContentDao contentDao, CategoryDao categoryDao, ContentStorer contentStorer )
    {
        this.groupDao = groupDao;
        this.contentDao = contentDao;
        this.categoryDao = categoryDao;
        this.contentStorer = contentStorer;
    }

    public void setDeleter( UserEntity deleter )
    {
        this.deleter = deleter;
    }

    public void setCategoryToDelete( CategoryEntity categoryToDelete )
    {
        this.categoryToDelete = categoryToDelete;
    }

    public void setIncludeContent( boolean includeContent )
    {
        this.includeContent = includeContent;
    }

    public void setRecursive( boolean recursive )
    {
        this.recursive = recursive;
    }

    void deleteCategory()
    {
        if ( !new CategoryAccessResolver( groupDao ).hasDeleteCategoryAccess( deleter, categoryToDelete ) )
        {
            throw new CategoryAccessException( "Cannot delete category", deleter.getQualifiedName(), CategoryAccessType.ADMINISTRATE,
                                               categoryToDelete.getKey() );
        }

        if ( !recursive && categoryDao.countChildrenByCategory( categoryToDelete ) > 0 )
        {
            throw new IllegalArgumentException( "Category [" + categoryToDelete.getPathAsString() +
                                                    "] contains categories. Deleting a category that contains categories is not allowed when recursive flag is false." );
        }

        if ( recursive )
        {
            doDeleteRecursively( categoryToDelete );
        }
        else
        {
            doDeleteCategory( categoryToDelete );
        }
    }

    private void doDeleteRecursively( CategoryEntity category )
    {
        // delete "leaf nodes" first...
        for ( CategoryEntity childCategory : category.getChildren() )
        {
            doDeleteRecursively( childCategory );
        }

        // if category contains content it cannot be deleted unless includeContent is true
        if ( !includeContent )
        {
            if ( contentDao.countContentByCategory( category ) > 0 )
            {
                throw new IllegalArgumentException( "Category [" + category.getPathAsString() +
                                                        "] contains content. Deleting a category that contains content is not allowed when includeContent is false." );
            }
        }

        doDeleteCategory( category );
    }

    private void doDeleteCategory( final CategoryEntity category )
    {
        // delete content
        if ( includeContent )
        {
            deletedContent.addAll( contentStorer.deleteByCategory( deleter, category ) );
        }

        categoryDao.deleteCategory( category );

        if ( category.getParent() == null )
        {
            // delete unit if top category
            final UnitEntity unitToDelete = category.getUnit();
            if ( unitToDelete != null )
            {
                unitToDelete.setDeleted( true );
                unitToDelete.removeContentTypes();
            }
        }
    }

    public UserEntity getDeleter()
    {
        return deleter;
    }

    public List<ContentEntity> getDeletedContent()
    {
        return deletedContent;
    }
}
