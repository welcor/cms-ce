package com.enonic.cms.core.content.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Component
public class CreateCategoryCommandProcessorFactory
{
    @Autowired
    private TimeService timeService;

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UnitFactory unitFactory;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private CategoryDao categoryDao;

    public CreateCategoryCommandProcessor create( StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getCreator(), "Creator in command must be specified" );

        final UserEntity creator = resolveUser( command.getCreator(), "creator" );
        final CategoryEntity parentCategory = resolveCategory( command.getParentCategory() );
        final ContentTypeEntity contentType = resolveContentType( command.getContentType() );
        final CategoryAccessStorer categoryAccessStorer = new CategoryAccessStorer( groupDao );
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );

        final CreateCategoryCommandProcessor createCategoryCommandProcessor =
            new CreateCategoryCommandProcessor( timeService, categoryDao, unitFactory, memberOfResolver, categoryAccessStorer,
                                                categoryAccessResolver );

        createCategoryCommandProcessor.setCreator( creator );
        createCategoryCommandProcessor.setParentCategory( parentCategory );
        createCategoryCommandProcessor.setContentType( contentType );

        return createCategoryCommandProcessor;
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

    private ContentTypeEntity resolveContentType( final ContentTypeKey key )
    {
        if ( key != null )
        {
            ContentTypeEntity contentType = contentTypeDao.findByKey( key );
            Preconditions.checkNotNull( contentType, "given content type does not exist: " + key );
            return contentType;
        }

        return null;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }
}
