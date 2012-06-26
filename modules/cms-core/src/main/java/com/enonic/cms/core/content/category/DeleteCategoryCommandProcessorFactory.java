package com.enonic.cms.core.content.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.ContentDao;

@Component
public class DeleteCategoryCommandProcessorFactory extends AbstractCategoryCommandProcessorFactory
{
    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentStorer contentStorer;

    DeleteCategoryCommandProcessor create( DeleteCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getDeleter(), "deleter must be specified" );
        Preconditions.checkNotNull( command.getCategoryKey(), "categoryKey must be specified" );

        final UserEntity deleter = resolveUser( command.getDeleter(), "deleter" );
        final CategoryEntity categoryToDelete = resolveCategory( command.getCategoryKey() );

        final DeleteCategoryCommandProcessor processor =
            new DeleteCategoryCommandProcessor( groupDao, contentDao, categoryDao, contentStorer );

        processor.setDeleter( deleter );
        processor.setCategoryToDelete( categoryToDelete );
        processor.setIncludeContent( command.isIncludeContent() );
        processor.setRecursive( command.isRecursive() );
        return processor;
    }
}
