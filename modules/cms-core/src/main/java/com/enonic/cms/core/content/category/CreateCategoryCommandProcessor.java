package com.enonic.cms.core.content.category;


import java.util.Date;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.category.access.CreateCategoryAccessException;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;

public class CreateCategoryCommandProcessor
{
    private TimeService timeService;

    private CategoryDao categoryDao;

    private UnitFactory unitFactory;

    private MemberOfResolver memberOfResolver;

    private UserEntity creator;

    private CategoryEntity parentCategory;

    private ContentTypeEntity contentType;

    private CategoryAccessStorer categoryAccessStorer;

    private CategoryAccessResolver categoryAccessResolver;

    public CreateCategoryCommandProcessor( TimeService timeService, CategoryDao categoryDao, UnitFactory unitFactory,
                                           MemberOfResolver memberOfResolver, CategoryAccessStorer categoryAccessStorer,
                                           CategoryAccessResolver categoryAccessResolver )
    {
        this.timeService = timeService;
        this.categoryDao = categoryDao;
        this.unitFactory = unitFactory;
        this.memberOfResolver = memberOfResolver;
        this.categoryAccessStorer = categoryAccessStorer;
        this.categoryAccessResolver = categoryAccessResolver;
    }

    public void setCreator( UserEntity creator )
    {
        this.creator = creator;
    }

    public void setParentCategory( CategoryEntity parentCategory )
    {
        this.parentCategory = parentCategory;
    }

    public void setContentType( ContentTypeEntity contentType )
    {
        this.contentType = contentType;
    }

    public CategoryKey createCategory( final StoreNewCategoryCommand command )
    {
        checkCreateCategoryAccess();

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

        UnitEntity unit;
        boolean creatingContentArchive = parentCategory == null;
        if ( creatingContentArchive )
        {
            unit = unitFactory.createNewUnit( command );
        }
        else
        {
            Preconditions.checkArgument( command.getAllowedContentTypes() == null,
                                         "Expect allowedContentTypes only to be specified when creating a content archive" );
            Preconditions.checkArgument( command.getLanguage() == null,
                                         "Expect language only to be specified when creating a content archive" );

            category.setParent( parentCategory );
            unit = parentCategory.getUnit();
        }

        category.setUnit( unit );
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

    private void checkCreateCategoryAccess()
        throws CreateCategoryAccessException
    {
        if ( parentCategory == null )
        {
            // needs at least administrator rights
            if ( !memberOfResolver.hasAdministratorPowers( creator ) )
            {
                throw new CreateCategoryAccessException( "To create a top category the user needs to be an administrator",
                                                         creator.getQualifiedName() );
            }
        }
        else
        {
            final boolean noAdministrateAccessByRights =
                !categoryAccessResolver.hasAccess( creator, parentCategory, CategoryAccessType.ADMINISTRATE );

            if ( noAdministrateAccessByRights )
            {
                if ( !memberOfResolver.isMemberOfAdministratorsGroup( creator ) )
                {
                    throw new CreateCategoryAccessException(
                        "To create a category the user needs to have the administrate access on the parent category or be an administrator",
                        creator.getQualifiedName() );
                }
            }
        }
    }
}