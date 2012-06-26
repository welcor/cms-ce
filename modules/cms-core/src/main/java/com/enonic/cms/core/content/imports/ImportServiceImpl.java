/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.search.IndexTransactionService;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;

@Service("importService")
public class ImportServiceImpl
    implements ImportService
{
    @Autowired
    private ContentStorer contentStorer;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private IndexTransactionService indexTransactionService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public boolean importData( ImportDataReader importDataReader, ImportJob importJob )
    {
        return doImportData( importDataReader, importJob );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public boolean importDataWithoutRequiresNewPropagation( ImportDataReader importDataReader, ImportJob importJob )
    {
        return doImportData( importDataReader, importJob );
    }

    private boolean doImportData( ImportDataReader importDataReader, ImportJob importJob )
    {
        try
        {
            ContentImporterImpl contentImporter = new ContentImporterImpl( importJob, importDataReader, indexTransactionService );
            contentImporter.setContentStorer( contentStorer );
            contentImporter.setContentDao( contentDao );

            RelatedContentFinder relatedContentFinder = new RelatedContentFinder( contentTypeDao, contentIndexService );
            contentImporter.setRelatedContentFinder( relatedContentFinder );

            final boolean result = contentImporter.importData();
            updateIndexWithDeletedContent( importJob.getImportResult() );
            return result;
        }
        finally
        {
            /* Clear all instances in first level cache since the transaction boundary doesn't (single session) */
            contentDao.getHibernateTemplate().clear();
        }
    }

    private void updateIndexWithDeletedContent( ImportResult importResult )
    {
        indexTransactionService.startTransaction();
        final Map<ContentKey, String> deleted = importResult.getDeleted();
        for ( ContentKey contentKey : deleted.keySet() )
        {
            indexTransactionService.deleteContent( contentKey );
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public void archiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doArchiveContent( importer, contentKeys, importResult );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public void archiveContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doArchiveContent( importer, contentKeys, importResult );
    }

    private void doArchiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        for ( ContentKey contentKey : contentKeys )
        {
            final ContentEntity content = contentDao.findByKey( contentKey );

            if ( content == null )
            {
                return;
            }

            boolean contentArchived = contentStorer.archiveMainVersion( importer, content );
            if ( contentArchived )
            {
                importResult.addArchived( content );

                UnassignContentCommand unassignContentCommand = new UnassignContentCommand();
                unassignContentCommand.setContentKey( content.getKey() );
                unassignContentCommand.setUnassigner( importer.getKey() );
                contentStorer.unassignContent( unassignContentCommand );
            }
            else
            {
                importResult.addAlreadyArchived( content );
            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public void deleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doDeleteContent( importer, contentKeys, importResult );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public void deleteContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doDeleteContent( importer, contentKeys, importResult );
    }

    private void doDeleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        for ( ContentKey contentKey : contentKeys )
        {
            final ContentEntity content = contentDao.findByKey( contentKey );

            if ( content == null )
            {
                // content must have been removed by another process during the import
            }
            else
            {
                contentStorer.deleteContent( importer, content );
                importResult.addDeleted( content );
            }
        }
    }
}
