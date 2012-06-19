package com.enonic.cms.core.content.category;


import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.security.user.UserEntity;

@Component
public class MoveCategoryCommandProcessorFactory extends AbstractCategoryCommandProcessorFactory
{
    MoveCategoryCommandProcessor create( MoveCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getUser(), "user must be specified" );
        Preconditions.checkNotNull( command.getCategoryToMove(), "categoryKey must be specified" );
        Preconditions.checkNotNull( command.getDestinationCategory(), "destination categoryKey must be specified" );

        final UserEntity user = resolveUser( command.getUser(), "user" );
        final CategoryEntity movedCategory = resolveCategory( command.getCategoryToMove() );
        final CategoryEntity destCategory = resolveCategory( command.getDestinationCategory() );

        final MoveCategoryCommandProcessor processor = new MoveCategoryCommandProcessor( groupDao );

        processor.setUser( user );
        processor.setCategoryToMove( movedCategory );
        processor.setDestinationCategory( destCategory );

        return processor;
    }
}
