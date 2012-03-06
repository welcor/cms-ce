package com.enonic.cms.core.content.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
public class DeleteCategoryCommandProcessorFactory
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private CategoryDao categoryDao;

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

    private CategoryEntity resolveCategory( final CategoryKey key )
    {
        if ( key != null )
        {
            CategoryEntity category = categoryDao.findByKey( key );
            Preconditions.checkNotNull( category, "given category does not exist: " + key );
            return category;
        }

        return null;
    }

    private UserEntity resolveUser( final UserKey key, final String subject )
    {
        if ( key != null )
        {
            UserEntity user = userDao.findByKey( key );
            Preconditions.checkNotNull( user, "given " + subject + " does not exist: " + key );
            return user;
        }
        return null;
    }
}
