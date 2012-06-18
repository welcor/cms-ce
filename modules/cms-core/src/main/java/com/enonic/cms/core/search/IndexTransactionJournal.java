package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.search.builder.ContentIndexData;

import static com.enonic.cms.core.search.ContentIndexServiceImpl.CONTENT_INDEX_NAME;

public class IndexTransactionJournal
    implements TransactionSynchronization
{
    private final static LogFacade LOG = LogFacade.get( IndexTransactionJournal.class );

    private final ElasticSearchIndexService indexService;

    private final List<IndexTransactionJournalEntry> changeHistory;

    public IndexTransactionJournal( ElasticSearchIndexService indexService )
    {
        this.indexService = indexService;
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

    public void addContent( ContentIndexData contentIndexData )
    {
        if ( TransactionSynchronizationManager.isCurrentTransactionReadOnly() )
        {
            return;
        }
        changeHistory.add( new IndexTransactionJournalEntry( IndexTransactionJournalEntry.JournalOperation.UPDATE, contentIndexData ) );
    }

    public void removeContent( ContentKey contentKey )
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

        LOG.info( "Flushing index changes from transaction journal" );
        for ( IndexTransactionJournalEntry journalEntry : changeHistory )
        {
            switch ( journalEntry.getOperation() )
            {
                case UPDATE:
                    final ContentIndexData contentIndexData = journalEntry.getContentIndexData();
                    LOG.info( "Updating index for content: " + contentIndexData.getKey().toString() );
                    indexContent( contentIndexData );
                    break;

                case DELETE:
                    final ContentKey contentKey = journalEntry.getContentKey();
                    LOG.info( "Deleting index for content: " + contentKey.toString() );
                    deleteContent( contentKey );
                    break;
            }
        }
        changeHistory.clear();

        flushIndex();
    }

    private void indexContent( ContentIndexData contentIndexData )
    {
        indexService.index( CONTENT_INDEX_NAME, contentIndexData );
    }

    private void deleteContent( ContentKey contentKey )
    {
        indexService.delete( CONTENT_INDEX_NAME, IndexType.Content, contentKey );
    }

    private void flushIndex()
    {
        indexService.flush( CONTENT_INDEX_NAME );
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

}
