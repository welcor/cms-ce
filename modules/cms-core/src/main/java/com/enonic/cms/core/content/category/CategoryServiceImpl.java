/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentTitleValidator;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.search.IndexTransactionService;
import com.enonic.cms.core.security.user.UserKey;

/**
 * Mar 9, 2010
 */
@Service
public class CategoryServiceImpl
    implements CategoryService
{
    private static final int TIMEOUT_24HOURS = 86400;

    @Autowired
    private CategoryCommandProcessorFactory processorFactory;

    @Autowired
    private IndexTransactionService indexTransactionService;

    @Autowired
    private LogService logService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoryKey storeNewCategory( final StoreNewCategoryCommand command )
    {
        return processorFactory.createStoreNewCategoryCommandProcessor( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateCategory( UpdateCategoryCommand command )
    {
        processorFactory.createUpdateCategoryCommandProcessor( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeCategoryACL_withoutRequiresNewPropagation_for_test_only( SynchronizeCategoryACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createSynchronizeCategoryACLProcessor( command ).process( command );
        indexTransactionService.commit();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void synchronizeCategoryACL( SynchronizeCategoryACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createSynchronizeCategoryACLProcessor( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeContentACL_withoutRequiresNewPropagation_for_test_only( SynchronizeContentACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createSynchronizeContentACLCommandProcessor( command ).process( command );
        indexTransactionService.commit();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void synchronizeContentACL( SynchronizeContentACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createSynchronizeContentACLCommandProcessor( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void modifyCategoryACL_withoutRequiresNewPropagation_for_test_only( ModifyCategoryACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createModifyCategoryACLCommand( command ).process( command );
        indexTransactionService.commit();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void modifyCategoryACL( ModifyCategoryACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createModifyCategoryACLCommand( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void modifyContentACL_withoutRequiresNewPropagation_for_test_only( ModifyContentACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createModifyContentACLCommandProcessor( command ).process( command );
        indexTransactionService.commit();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void modifyContentACL( ModifyContentACLCommand command )
    {
        indexTransactionService.startTransaction();
        processorFactory.createModifyContentACLCommandProcessor( command ).process( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = TIMEOUT_24HOURS)
    public void deleteCategory( final DeleteCategoryCommand command )
    {
        indexTransactionService.startTransaction();
        try
        {
            final DeleteCategoryCommandProcessor processor = processorFactory.createDeleteCategoryCommandProcessor( command );
            processor.deleteCategory();

            for ( ContentEntity deletedContent : processor.getDeletedContent() )
            {
                logEventForDeletedContent( processor.getDeleter().getKey(), deletedContent, LogType.ENTITY_REMOVED );
            }
        }
        catch ( RuntimeException e )
        {
            throw new DeleteCategoryException( e );
        }
    }

    private void logEventForDeletedContent( final UserKey actor, final ContentEntity content, final LogType type )
    {
        String title = content.getMainVersion().getTitle();
        String titleKey = " (" + content.getKey().toInt() + ")";
        if ( title.length() + titleKey.length() > ContentTitleValidator.CONTENT_TITLE_MAX_LENGTH )
        {
            title = title.substring( 0, ContentTitleValidator.CONTENT_TITLE_MAX_LENGTH - titleKey.length() );
        }
        title = title + titleKey;
        final StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setUser( actor );
        command.setTableKeyValue( content.getKey().toInt() );
        command.setTableKey( Table.CONTENT );
        command.setType( type );
        command.setTitle( title );
        command.setPath( content.getPathAsString() );
        command.setXmlData( content.getMainVersion().getContentDataAsJDomDocument() );

        logService.storeNew( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void moveCategory( MoveCategoryCommand command )
    {
        final MoveCategoryCommandProcessor processor = processorFactory.createMoveCategoryCommandProcessor( command );
        processor.moveCategory();
    }
}
