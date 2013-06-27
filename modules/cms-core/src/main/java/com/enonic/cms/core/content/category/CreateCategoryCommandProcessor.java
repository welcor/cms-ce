/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.Date;

import org.apache.commons.lang.StringUtils;

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

    private boolean creatingContentArchive;

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
        creatingContentArchive = parentCategory == null;
    }

    void setContentType( ContentTypeEntity contentType )
    {
        this.contentType = contentType;
    }

    CategoryKey process( final StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getName(), "name not specified" );
        Preconditions.checkArgument( StringUtils.isNotBlank( command.getName() ), "name is not valid: %s", command.getName() );

        if ( creatingContentArchive )
        {
            Preconditions.checkNotNull( command.getLanguage(), "language not specified" );
        }

        if ( command.getAllowedContentTypes() != null && command.getAllowedContentTypes().size() > 0 && contentType != null )
        {
            Preconditions.checkArgument( command.getAllowedContentTypes().contains( contentType.getContentTypeKey() ),
                                         "content type must be among the allowed ones: " + contentType.getName() );
        }

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
        if ( command.getCategoryACL() == null && parentCategory != null )
        {
            categoryAccessStorer.applyAccessRightsFromParent( parentCategory, category );
        }
        else if ( command.getCategoryACL() != null )
        {
            categoryAccessStorer.applyGivenAccessRights( command.getCategoryACL(), category );
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