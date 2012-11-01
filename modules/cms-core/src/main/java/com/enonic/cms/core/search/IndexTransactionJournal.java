package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentMap;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentEagerFetches;
import com.enonic.cms.store.dao.FindContentByKeysCommand;

import static com.enonic.cms.core.search.ContentIndexServiceImpl.CONTENT_INDEX_NAME;
import static com.enonic.cms.core.search.IndexTransactionJournalEntry.JournalOperation.UPDATE;

public class IndexTransactionJournal
    implements TransactionSynchronization
{
    private final Logger LOG = Logger.getLogger( IndexTransactionJournal.class.getName() );

    private final ContentDao contentDao;

    private final IndexService indexService;

    private final ContentIndexDataFactory contentIndexDataFactory;

    private final ElasticSearchIndexService elasticSearchIndexService;

    private final Set<IndexTransactionJournalEntry> changeHistory;

    public IndexTransactionJournal( ElasticSearchIndexService elasticSearchIndexService, IndexService indexService,
                                    ContentIndexDataFactory contentIndexDataFactory, ContentDao contentDao )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
        this.indexService = indexService;
        this.contentIndexDataFactory = contentIndexDataFactory;
        this.contentDao = contentDao;
        this.changeHistory = new HashSet<IndexTransactionJournalEntry>();
    }

    public void startTransaction()
    {
        registerSynchronization();
        LOG.info( "Index transaction started" );
    }

    private void registerSynchronization()
    {
        if ( !TransactionSynchronizationManager.getSynchronizations().contains( this ) )
        {
            TransactionSynchronizationManager.registerSynchronization( this );
        }
    }

    public void registerUpdate( Collection<ContentKey> contentKeys, boolean updateMetadataOnly )
    {
        if ( TransactionSynchronizationManager.isCurrentTransactionReadOnly() )
        {
            return;
        }

        for ( ContentKey contentKey : contentKeys )
        {
            final IndexTransactionJournalEntry indexTransactionJournalEntry =
                new IndexTransactionJournalEntry( UPDATE, contentKey, updateMetadataOnly );

            changeHistory.add( indexTransactionJournalEntry );
        }
    }


    public void registerUpdate( ContentKey contentKey, boolean updateMetadataOnly )
    {
        if ( TransactionSynchronizationManager.isCurrentTransactionReadOnly() )
        {
            return;
        }

        final IndexTransactionJournalEntry indexTransactionJournalEntry =
            new IndexTransactionJournalEntry( UPDATE, contentKey, updateMetadataOnly );

        changeHistory.add( indexTransactionJournalEntry );
    }

    public void registerRemove( ContentKey contentKey )
    {
        changeHistory.add( new IndexTransactionJournalEntry( IndexTransactionJournalEntry.JournalOperation.DELETE, contentKey ) );
    }

    private void flushIndexChanges()
    {
        if ( changeHistory.isEmpty() )
        {
            LOG.info( "No changes found in transaction, skipping index update." );
            return;
        }

        if ( TransactionSynchronizationManager.isCurrentTransactionReadOnly() )
        {
            LOG.info( "Read-only transaction, nothing to do." );
            return;
        }

        final ContentMap contentMap = preloadContent();

        LOG.info( "Flushing index changes from transaction journal" );
        for ( IndexTransactionJournalEntry journalEntry : changeHistory )
        {
            switch ( journalEntry.getOperation() )
            {
                case UPDATE:
                    handleFlushUpdateOperation( journalEntry, contentMap );
                    break;

                case DELETE:
                    handleFlushDeleteOperation( journalEntry );
                    break;
            }
        }
        changeHistory.clear();

        flushIndex();
    }

    private ContentMap preloadContent()
    {
        List<ContentKey> contentToLoad = new ArrayList<ContentKey>();
        for ( IndexTransactionJournalEntry journalEntry : changeHistory )
        {
            if ( journalEntry.getOperation() == UPDATE )
            {
                contentToLoad.add( journalEntry.getContentKey() );
            }
        }

        FindContentByKeysCommand command = new FindContentByKeysCommand().contentKeys( contentToLoad ).eagerFetches(
            ContentEagerFetches.PRESET_FOR_INDEXING ).fetchEntitiesAsReadOnly( true ).byPassCache( false );

        return contentDao.findByKeys( command );
    }

    private void handleFlushUpdateOperation( final IndexTransactionJournalEntry journalEntry, final ContentMap contentMap )
    {
        final ContentEntity content = contentMap.get( journalEntry.getContentKey() );
        if ( content == null )
        {
            LOG.warning(
                "Content to update index for did not exist (removing index for content instead): " + journalEntry.getContentKey() );
            deleteContent( journalEntry.getContentKey() );
        }
        else if ( content.isDeleted() )
        {
            deleteContent( content.getKey() );
        }
        else
        {
            doUpdateContent( content, journalEntry.isUpdateMetadataOnly() );
        }
    }

    private void handleFlushDeleteOperation( final IndexTransactionJournalEntry journalEntry )
    {
        deleteContent( journalEntry.getContentKey() );
    }

    private void doUpdateContent( final ContentEntity content, final boolean updateMetadataOnly )
    {
        final ContentDocument doc = indexService.createContentDocument( content, updateMetadataOnly );
        final ContentIndexData contentIndexData = contentIndexDataFactory.create( doc, updateMetadataOnly );

        LOG.fine( "Updating index for content: " + contentIndexData.getKey().toString() );

        elasticSearchIndexService.index( CONTENT_INDEX_NAME, contentIndexData );
    }

    private void deleteContent( ContentKey contentKey )
    {
        LOG.fine( "Deleting index for content: " + contentKey.toString() );
        elasticSearchIndexService.delete( CONTENT_INDEX_NAME, IndexType.Content, contentKey );
    }

    private void flushIndex()
    {
        elasticSearchIndexService.flush( CONTENT_INDEX_NAME );
    }

    @Override
    public void afterCommit()
    {
        TransactionSynchronizationManager.unbindResourceIfPossible( IndexTransactionServiceImpl.TRANSACTION_JOURNAL_KEY );
        flushIndexChanges();
    }

    @Override
    public void suspend()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void beforeCommit( boolean b )
    {
    }

    @Override
    public void beforeCompletion()
    {
    }

    @Override
    public void afterCompletion( int i )
    {
        //     System.out.println( "Completion status: " + i );
    }

    public void clearJournal()
    {
        TransactionSynchronizationManager.unbindResourceIfPossible( IndexTransactionServiceImpl.TRANSACTION_JOURNAL_KEY );
        changeHistory.clear();
    }
}
