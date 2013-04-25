package com.enonic.cms.core.search;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.store.dao.ContentDao;

@Service
@Scope("singleton")
public class IndexTransactionServiceImpl
    implements IndexTransactionService
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexTransactionServiceImpl.class );

    public static final Object TRANSACTION_JOURNAL_KEY = IndexTransactionJournal.class;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ContentIndexService contentIndexService;

    @Override
    public void startTransaction()
    {
        IndexTransactionJournal indexTransactionJournal = newTransactionJournal();
        indexTransactionJournal.startTransaction();
    }

    @Override
    public void registerUpdate( final Collection<ContentKey> contentKeys, final boolean updateMetadataOnly )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        indexTransactionJournal.registerUpdate( contentKeys, updateMetadataOnly );
    }

    @Override
    public void registerUpdate( final ContentKey contentKey, final boolean updateMetadataOnly )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        indexTransactionJournal.registerUpdate( contentKey, updateMetadataOnly );
    }

    @Override
    public void deleteContent( final ContentKey contentKey )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        indexTransactionJournal.registerRemove( contentKey );
    }

    @Override
    public void commit()
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        indexTransactionJournal.afterCommit();
    }

    @Override
    public boolean isActive()
    {
        IndexTransactionJournal indexTransactionJournal =
            (IndexTransactionJournal) TransactionSynchronizationManager.getResource( TRANSACTION_JOURNAL_KEY );
        return ( indexTransactionJournal != null );
    }

    @Override
    public void clearJournal()
    {
        // note than indexTransactionJournal is kept between mvn test on one VM
        IndexTransactionJournal indexTransactionJournal =
            (IndexTransactionJournal) TransactionSynchronizationManager.getResource( TRANSACTION_JOURNAL_KEY );

        if ( indexTransactionJournal != null )
        {
            indexTransactionJournal.clearJournal();
        }
    }

    private IndexTransactionJournal newTransactionJournal()
    {
        IndexTransactionJournal indexTransactionJournal =
            (IndexTransactionJournal) TransactionSynchronizationManager.getResource( TRANSACTION_JOURNAL_KEY );
        if ( indexTransactionJournal != null )
        {
            return indexTransactionJournal;
        }
        indexTransactionJournal = new IndexTransactionJournal( contentIndexService, indexService, contentDao );
        TransactionSynchronizationManager.bindResource( TRANSACTION_JOURNAL_KEY, indexTransactionJournal );
        return indexTransactionJournal;
    }

    private IndexTransactionJournal getCurrentTransactionJournal()
    {
        IndexTransactionJournal indexTransactionJournal =
            (IndexTransactionJournal) TransactionSynchronizationManager.getResource( TRANSACTION_JOURNAL_KEY );
        if ( indexTransactionJournal == null )
        {
            throw new IllegalStateException( "No index transaction is currently active" );
        }
        return indexTransactionJournal;
    }

}
