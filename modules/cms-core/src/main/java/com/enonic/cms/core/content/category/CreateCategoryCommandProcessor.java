package com.enonic.cms.core.content.category;


import java.util.Date;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;

class CreateCategoryCommandProcessor
{
    private TimeService timeService;

    private CategoryDao categoryDao;

    private UnitFactory unitFactory;

    private CreateCategoryAccessChecker createCategoryAccessChecker;

    private CategoryAccessStorer categoryAccessStorer;

    private UserEntity creator;

    private CategoryEntity parentCategory;

    private ContentTypeEntity contentType;

    CreateCategoryCommandProcessor( TimeService timeService, CategoryDao categoryDao, UnitFactory unitFactory,
                                    CategoryAccessStorer categoryAccessStorer, CreateCategoryAccessChecker createCategoryAccessChecker )
    {
        this.timeService = timeService;
        this.categoryDao = categoryDao;
        this.unitFactory = unitFactory;
        this.categoryAccessStorer = categoryAccessStorer;
        this.createCategoryAccessChecker = createCategoryAccessChecker;
    }

    void setCreator( UserEntity creator )
    {
        this.creator = creator;
    }

    void setParentCategory( CategoryEntity parentCategory )
    {
        this.parentCategory = parentCategory;
    }

    void setContentType( ContentTypeEntity contentType )
    {
        this.contentType = contentType;
    }

    CategoryKey createCategory( final StoreNewCategoryCommand command )
    {
        final boolean creatingContentArchive = parentCategory == null;
        checkCreateCategoryAccess( creatingContentArchive );

        final Date now = timeService.getNowAsDateTime().toDate();

        final CategoryEntity category = new CategoryEntity();
        category.setTimestamp( now );
        category.setContentType( contentType );
        category.setCreated( now );
        category.setDeleted( false );
        category.setModifier( creator );
        category.setName( command.getName() );
        category.setOwner( creator );
        category.setAutoMakeAvailable( command.getAutoApprove() );
        category.setDescription( command.getDescription() );

        if ( creatingContentArchive )
        {
            category.setUnit( unitFactory.createNewUnit( command ) );
        }
        else
        {
            Preconditions.checkArgument( command.getAllowedContentTypes() == null,
                                         "Expect allowedContentTypes only to be specified when creating a content archive" );
            Preconditions.checkArgument( command.getLanguage() == null,
                                         "Expect language only to be specified when creating a content archive" );

            category.setParent( parentCategory );
            category.setUnit( parentCategory.getUnit() );
        }

        categoryDao.storeNew( category );
        applyAccessRights( command, category );
        return category.getKey();
    }

    private void applyAccessRights( StoreNewCategoryCommand command, CategoryEntity category )
    {
        if ( command.getAccessRights() == null && parentCategory != null )
        {
            categoryAccessStorer.applyAccessRightsFromParent( parentCategory, category );
        }
        else if ( command.getAccessRights() != null )
        {
            categoryAccessStorer.applyGivenAccessRights( command.getAccessRights(), category );
        }
        else
        {
            categoryAccessStorer.ensureAccessRightForAdministratorGroup( category );
        }
    }

    private void checkCreateCategoryAccess( final boolean creatingContentArchive )
        throws CreateCategoryAccessException
    {
        if ( creatingContentArchive )
        {
            createCategoryAccessChecker.checkAccessToCreateContentArchive();
        }
        else
        {
            createCategoryAccessChecker.checkAccessToCreateCategory( parentCategory );
        }
    }
}