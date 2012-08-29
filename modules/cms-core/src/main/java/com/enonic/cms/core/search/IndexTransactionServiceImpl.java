package com.enonic.cms.core.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.store.dao.ContentDao;

@Service
@Scope("singleton")
public class IndexTransactionServiceImpl
    implements IndexTransactionService
{

    private final static LogFacade LOG = LogFacade.get( IndexTransactionServiceImpl.class );

    public static final Object TRANSACTION_JOURNAL_KEY = IndexTransactionJournal.class;

    private final ContentIndexDataFactory contentIndexDataFactory;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ElasticSearchIndexService elasticSearchIndexService;

    @Autowired
    private IndexService indexService;

    public IndexTransactionServiceImpl()
    {
        contentIndexDataFactory = new ContentIndexDataFactory();
    }

    @Override
    public void startTransaction()
    {
        IndexTransactionJournal indexTransactionJournal = newTransactionJournal();
        indexTransactionJournal.startTransaction();
    }

    @Override
    public void updateContent( ContentKey contentKey )
    {
        updateContent( contentDao.findByKey( contentKey ) );
    }

    @Override
    public void updateContent( final ContentKey contentKey, final boolean skipAttachments )
    {
        updateContent( contentDao.findByKey( contentKey ), skipAttachments );
    }

    @Override
    public void updateContent( ContentEntity contentEntity )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        ContentIndexData contentIndexData = createContentIndexData( contentEntity, false );
        indexTransactionJournal.addContent( contentIndexData );
    }

    @Override
    public void updateContent( ContentEntity contentEntity, boolean skipAttachments )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        ContentIndexData contentIndexData = createContentIndexData( contentEntity, skipAttachments );
        indexTransactionJournal.addContent( contentIndexData );
    }

    @Override
    public void deleteContent( ContentKey contentKey )
    {
        IndexTransactionJournal indexTransactionJournal = getCurrentTransactionJournal();
        indexTransactionJournal.removeContent( contentKey );
    }

    @Override
    public void updateCategory( CategoryKey categoryKey )
    {
        final List<ContentKey> contentList = contentDao.findContentKeysByCategory( categoryKey );
        for ( ContentKey contentKey : contentList )
        {
            updateContent( contentKey, true );
        }
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
//            TransactionSynchronizationManager.unbindResource( TRANSACTION_JOURNAL_KEY );
            LOG.error( "Index transaction already started" );
            return indexTransactionJournal;
            //throw new IllegalStateException("Index transaction already started");
        }
        indexTransactionJournal = new IndexTransactionJournal( elasticSearchIndexService );
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

    private ContentIndexData createContentIndexData( final ContentEntity content, final boolean skipAttachments )
    {
        ContentDocument doc = indexService.createContentDocument( content );
        return contentIndexDataFactory.create( doc, skipAttachments );
    }
}
