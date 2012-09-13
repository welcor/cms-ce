package com.enonic.cms.core.content.category;


import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.ContentACLSynchronizer;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.search.IndexTransactionService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentEagerFetches;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.FindContentByKeysCommand;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.UserDao;

@Component
public class CategoryCommandProcessorFactory
{
    @Autowired
    private ContentStorer contentStorer;

    @Autowired
    private UnitFactory unitFactory;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private TimeService timeService;

    @Autowired
    private IndexTransactionService indexTransactionService;

    CreateCategoryCommandProcessor createStoreNewCategoryCommandProcessor( final StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getCreator(), "Creator in command must be specified" );

        final UserEntity creator = resolveUser( command.getCreator(), "creator" );
        final CategoryEntity parentCategory = resolveCategory( command.getParentCategory() );
        final ContentTypeEntity contentType = resolveContentType( command.getContentType() );
        final CategoryAccessStorer categoryAccessStorer = new CategoryAccessStorer( groupDao );
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final CreateCategoryAccessChecker createCategoryAccessChecker =
            new CreateCategoryAccessChecker( memberOfResolver, categoryAccessResolver ).creator( creator );

        final CreateCategoryCommandProcessor createCategoryCommandProcessor =
            new CreateCategoryCommandProcessor( timeService, categoryDao, unitFactory, categoryAccessStorer, createCategoryAccessChecker );

        createCategoryCommandProcessor.setCreator( creator );
        createCategoryCommandProcessor.setParentCategory( parentCategory );
        createCategoryCommandProcessor.setContentType( contentType );

        return createCategoryCommandProcessor;
    }

    UpdateCategoryCommandProcessor createUpdateCategoryCommandProcessor( final UpdateCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getUpdater(), "Updater in command must be specified" );

        final UserEntity updater = resolveUser( command.getUpdater(), "updater" );
        final CategoryEntity category = resolveCategory( command.getCategory() );
        final LanguageEntity language = resolveLanguage( command.getLanguage() );
        final ContentTypeEntity contentType = resolveContentType( command.getContentType() );
        final Set<ContentTypeEntity> allowedContentTypes = resolveAllowedContentTypes( command.getAllowedContentTypes() );

        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final UpdateCategoryAccessChecker updateCategoryAccessChecker =
            new UpdateCategoryAccessChecker( memberOfResolver, categoryAccessResolver ).updater( updater );

        final UpdateCategoryCommandProcessor processor =
            new UpdateCategoryCommandProcessor( timeService, contentDao, updateCategoryAccessChecker );

        processor.setUpdater( updater );
        processor.setCategoryToUpdate( category );
        processor.setContentType( contentType );
        processor.setLanguage( language );
        processor.setAllowedContentTypes( allowedContentTypes );

        return processor;
    }

    DeleteCategoryCommandProcessor createDeleteCategoryCommandProcessor( final DeleteCategoryCommand command )
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

    MoveCategoryCommandProcessor createMoveCategoryCommandProcessor( final MoveCategoryCommand command )
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

    SynchronizeCategoryACLCommandProcessor createSynchronizeCategoryACLProcessor( final SynchronizeCategoryACLCommand command )
    {
        Preconditions.checkNotNull( command.getUpdater(), "Updater in command must be specified" );

        final UserEntity updater = resolveUser( command.getUpdater(), "updater" );
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final UpdateCategoryAccessChecker updateCategoryAccessChecker =
            new UpdateCategoryAccessChecker( memberOfResolver, categoryAccessResolver ).updater( updater );
        final SortedMap<CategoryKey, CategoryEntity> categoryMapBykey = resolveCategories( command.getCategoriesToUpdate() );

        final SynchronizeCategoryACLCommandProcessor processor =
            new SynchronizeCategoryACLCommandProcessor( groupDao, updateCategoryAccessChecker );

        processor.setCategoriesToUpdate( categoryMapBykey );

        return processor;
    }

    SynchronizeContentACLProcessor createSynchronizeContentACLCommandProcessor( final SynchronizeContentACLCommand command )
    {
        final SortedMap<ContentKey, ContentEntity> contentToSynchronize = contentDao.findByKeys(
            new FindContentByKeysCommand().fetchEntitiesAsReadOnly( false ).contentKeys( command.getContentToUpdate() ).eagerFetches(
                ContentEagerFetches.PRESET_FOR_APPLYING_CONTENT_ACCESS ).byPassCache( true ) );

        final SynchronizeContentACLProcessor processor = new SynchronizeContentACLProcessor( new ContentACLSynchronizer( groupDao ) );
        processor.setContentToSynchronize( contentToSynchronize );

        return processor;
    }

    ModifyCategoryACLCommandProcessor createModifyCategoryACLCommand( final ModifyCategoryACLCommand command )
    {
        Preconditions.checkNotNull( command.getUpdater(), "Updater in command must be specified" );

        final UserEntity updater = resolveUser( command.getUpdater(), "updater" );
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final UpdateCategoryAccessChecker updateCategoryAccessChecker =
            new UpdateCategoryAccessChecker( memberOfResolver, categoryAccessResolver ).updater( updater );
        final SortedMap<CategoryKey, CategoryEntity> categoryMapBykey = resolveCategories( command.getCategoriesToUpdate() );

        final ModifyCategoryACLCommandProcessor processor =
            new ModifyCategoryACLCommandProcessor( groupDao, updateCategoryAccessChecker, indexTransactionService );

        processor.setCategoriesToUpdate( categoryMapBykey );

        return processor;
    }

    ModifyContentACLCommandProcessor createModifyContentACLCommandProcessor( final ModifyContentACLCommand command )
    {
        final SortedMap<ContentKey, ContentEntity> contentToSynchronize = contentDao.findByKeys(
            new FindContentByKeysCommand().fetchEntitiesAsReadOnly( false ).contentKeys( command.getContentToUpdate() ).eagerFetches(
                ContentEagerFetches.PRESET_FOR_APPLYING_CONTENT_ACCESS ).byPassCache( true ) );

        final ModifyContentACLCommandProcessor prosessor = new ModifyContentACLCommandProcessor( groupDao );
        prosessor.setContentToSynchronize( contentToSynchronize );
        return prosessor;
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

    private LanguageEntity resolveLanguage( final LanguageKey key )
    {
        if ( key != null )
        {
            LanguageEntity language = languageDao.findByKey( key );
            Preconditions.checkNotNull( language, "given language does not exist: " + key );
            return language;
        }

        return null;
    }


    private SortedMap<CategoryKey, CategoryEntity> resolveCategories( final List<CategoryKey> keys )
    {
        return categoryDao.findByKeys( keys );
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

    private Set<ContentTypeEntity> resolveAllowedContentTypes( final List<ContentTypeKey> allowedContentTypes )
    {
        if ( allowedContentTypes != null )
        {
            final Set<ContentTypeEntity> list = new LinkedHashSet<ContentTypeEntity>();
            for ( final ContentTypeKey allowedContentTypeKey : allowedContentTypes )
            {
                final ContentTypeEntity allowedContentType = contentTypeDao.findByKey( allowedContentTypeKey );
                Preconditions.checkNotNull( allowedContentType,
                                            "Specified content type to allow does not exist: " + allowedContentTypeKey );
                list.add( allowedContentType );
            }
            return list;
        }
        return null;
    }
}
