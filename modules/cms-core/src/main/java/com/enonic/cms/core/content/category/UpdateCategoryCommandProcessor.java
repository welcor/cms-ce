/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content.category;


import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.ContentDao;

class UpdateCategoryCommandProcessor
{
    private TimeService timeService;

    private UpdateCategoryAccessChecker updateCategoryAccessChecker;

    private ContentDao contentDao;

    private UserEntity updater;

    private CategoryEntity categoryToUpdate;

    private UnitEntity unitToUpdate;

    private ContentTypeEntity contentType;

    private LanguageEntity language;

    private Set<ContentTypeEntity> allowedContentTypes;

    UpdateCategoryCommandProcessor( TimeService timeService, ContentDao contentDao,
                                    UpdateCategoryAccessChecker updateCategoryAccessChecker )
    {
        this.timeService = timeService;
        this.contentDao = contentDao;
        this.updateCategoryAccessChecker = updateCategoryAccessChecker;
    }

    void setUpdater( UserEntity updater )
    {
        this.updater = updater;
    }

    void setCategoryToUpdate( CategoryEntity categoryToUpdate )
    {
        this.categoryToUpdate = categoryToUpdate;
        if ( this.categoryToUpdate.isTopCategory() )
        {
            this.unitToUpdate = categoryToUpdate.getUnit();
        }
    }

    void setContentType( final ContentTypeEntity contentType )
    {
        this.contentType = contentType;
    }

    void setLanguage( final LanguageEntity language )
    {
        this.language = language;
    }

    void setAllowedContentTypes( final Set<ContentTypeEntity> allowedContentTypes )
    {
        this.allowedContentTypes = allowedContentTypes;
    }

    CategoryKey process( final UpdateCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getName(), "name not specified" );
        Preconditions.checkArgument( StringUtils.isNotBlank( command.getName() ), "name is not valid: %s", command.getName() );

        if ( categoryToUpdate.isTopCategory() )
        {
            Preconditions.checkNotNull( command.getLanguage(), "language not specified" );
        }
        if ( command.getAllowedContentTypes() != null && command.getAllowedContentTypes().size() > 0 && contentType != null )
        {
            Preconditions.checkArgument( command.getAllowedContentTypes().contains( contentType.getContentTypeKey() ),
                                         "content type must be among the allowed ones: " + contentType.getName() );
        }

        checkUpdateCategoryAccess();

        boolean modifiedUnitProperties = false;
        if ( categoryToUpdate.isTopCategory() )
        {
            modifiedUnitProperties = updateUnitModifyableProperties( command );
        }

        final boolean modifiedCategoryProperties = updateCategoryModifyableProperties( command );

        if ( modifiedUnitProperties )
        {
            unitToUpdate.setTimestamp( timeService.getNowAsDateTime().toDate() );
        }

        if ( modifiedCategoryProperties )
        {
            categoryToUpdate.setModifier( updater );
            categoryToUpdate.setTimestamp( timeService.getNowAsDateTime().toDate() );
        }
        return categoryToUpdate.getKey();
    }

    private boolean updateUnitModifyableProperties( final UpdateCategoryCommand command )
    {
        boolean modified = false;

        if ( !unitToUpdate.getName().equals( command.getName() ) )
        {
            unitToUpdate.setName( command.getName() );
            modified = true;
        }

        if ( !unitToUpdate.getLanguage().equals( language ) )
        {
            unitToUpdate.setLanguage( language );
            modified = true;
        }

        if ( !equals( unitToUpdate.getDescription(), command.getDescription() ) )
        {
            unitToUpdate.setDescription( command.getDescription() );
            modified = true;
        }

        if ( allowedContentTypes != null )
        {
            boolean modifiedContentTypes = unitToUpdate.synchronizeContentTypes( allowedContentTypes );
            modified = modified || modifiedContentTypes;
        }

        return modified;
    }

    private boolean updateCategoryModifyableProperties( final UpdateCategoryCommand command )
    {
        boolean modified = false;

        if ( !equals( categoryToUpdate.getContentType(), contentType ) )
        {
            if ( categoryHasContent() )
            {
                throw new UpdateCategoryException(
                    "Not allowed to change content type of a category that is not empty: " + categoryToUpdate.getKey() );
            }
            categoryToUpdate.setContentType( contentType );
            modified = true;
        }

        if ( !equals( categoryToUpdate.getName(), command.getName() ) )
        {
            categoryToUpdate.setName( command.getName() );
            modified = true;
        }

        if ( !categoryToUpdate.getAutoMakeAvailableAsBoolean() == command.getAutoApprove() )
        {
            categoryToUpdate.setAutoMakeAvailable( command.getAutoApprove() );
            modified = true;
        }

        if ( !equals( categoryToUpdate.getDescription(), command.getDescription() ) )
        {
            categoryToUpdate.setDescription( command.getDescription() );
            modified = true;
        }
        return modified;
    }

    private boolean equals( Object a, Object b )
    {
        if ( a == null && b == null )
        {
            return true;
        }
        else if ( a == null )
        {
            return false;
        }

        return a.equals( b );
    }

    private void checkUpdateCategoryAccess()
        throws CreateCategoryAccessException
    {
        updateCategoryAccessChecker.checkAccessToUpdateCategory( categoryToUpdate );
    }

    private boolean categoryHasContent()
    {
        return contentDao.countContentByCategory( categoryToUpdate ) > 0;
    }

}
