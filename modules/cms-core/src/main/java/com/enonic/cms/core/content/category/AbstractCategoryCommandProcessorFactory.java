package com.enonic.cms.core.content.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
public abstract class AbstractCategoryCommandProcessorFactory
{
    @Autowired
    UserDao userDao;

    @Autowired
    GroupDao groupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ContentTypeDao contentTypeDao;

    protected CategoryEntity resolveCategory( final CategoryKey key )
    {
        if ( key != null )
        {
            CategoryEntity category = categoryDao.findByKey( key );
            Preconditions.checkNotNull( category, "given category does not exist: " + key );
            return category;
        }

        return null;
    }

    protected UserEntity resolveUser( final UserKey key, final String subject )
    {
        if ( key != null )
        {
            UserEntity user = userDao.findByKey( key );
            Preconditions.checkNotNull( user, "given " + subject + " does not exist: " + key );
            return user;
        }
        return null;
    }

    protected ContentTypeEntity resolveContentType( final ContentTypeKey key )
    {
        if ( key != null )
        {
            ContentTypeEntity contentType = contentTypeDao.findByKey( key );
            Preconditions.checkNotNull( contentType, "given content type does not exist: " + key );
            return contentType;
        }

        return null;
    }
}
