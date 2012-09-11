package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
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
    private final static LogFacade LOG = LogFacade.get( IndexTransactionJournal.class );

    private final ContentDao contentDao;

    private final IndexService indexService;

    private final ContentIndexDataFactory contentIndexDataFactory;

    private final ElasticSearchIndexService elasticSearchIndexService;

    private final List<IndexTransactionJournalEntry> changeHistory;

    public IndexTransactionJournal( ElasticSearchIndexService elasticSearchIndexService, IndexService indexService,
                                    ContentIndexDataFactory contentIndexDataFactory, ContentDao contentDao )
    {
        this.elasticSearchIndexService = elasticSearchIndexService;
        this.indexService = indexService;
        this.contentIndexDataFactory = contentIndexDataFactory;
        this.contentDao = contentDao;
        this.changeHistory = new ArrayList<IndexTransactionJournalEntry>();
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

    public void registerUpdate( ContentKey contentKey, boolean skipAttachments )
    {
        if ( TransactionSynchronizationManager.isCurrentTransactionReadOnly() )
        {
            return;
        }

        final IndexTransactionJournalEntry indexTransactionJournalEntry =
            new IndexTransactionJournalEntry( UPDATE, contentKey, skipAttachments );

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

        final SortedMap<ContentKey, ContentEntity> contentMapByKey = preloadContent();

        LOG.info( "Flushing index changes from transaction journal" );
        for ( IndexTransactionJournalEntry journalEntry : changeHistory )
        {
            switch ( journalEntry.getOperation() )
            {
                case UPDATE:
                    handleFlushUpdateOperation( journalEntry, contentMapByKey );
                    break;

                case DELETE:
                    handleFlushDeleteOperation( journalEntry );
                    break;
            }
        }
        changeHistory.clear();

        flushIndex();
    }

    private SortedMap<ContentKey, ContentEntity> preloadContent()
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

    private void handleFlushUpdateOperation( final IndexTransactionJournalEntry journalEntry,
                                             final SortedMap<ContentKey, ContentEntity> contentMapByKey )
    {
        final ContentEntity content = contentMapByKey.get( journalEntry.getContentKey() );
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
            updateContent( content, journalEntry.isSkipAttachments() );
        }
    }

    private void handleFlushDeleteOperation( final IndexTransactionJournalEntry journalEntry )
    {
        deleteContent( journalEntry.getContentKey() );
    }

    private void updateContent( final ContentEntity content, final boolean skipAttachments )
    {
        final ContentDocument doc = indexService.createContentDocument( content );
        final ContentIndexData contentIndexData = contentIndexDataFactory.create( doc, skipAttachments );

        LOG.info( "Updating index for content: " + contentIndexData.getKey().toString() );
        elasticSearchIndexService.index( CONTENT_INDEX_NAME, contentIndexData );
    }

    private void deleteContent( ContentKey contentKey )
    {
        LOG.info( "Deleting index for content: " + contentKey.toString() );
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
    }

    public void clearJournal()
    {
        TransactionSynchronizationManager.unbindResourceIfPossible( IndexTransactionServiceImpl.TRANSACTION_JOURNAL_KEY );
        changeHistory.clear();
    }
}
