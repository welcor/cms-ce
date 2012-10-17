package com.enonic.cms.core.search;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.FindContentByKeysCommand;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndexTransactionJournalTest
{


    IndexTransactionJournal journal;

    IndexService indexService;

    ElasticSearchIndexService elasticSearchIndexService;

    ContentDao contentDao;

    ContentIndexDataFactory contentIndexDataFactory;

    @Before
    public void setup()
    {
        elasticSearchIndexService = mock( ElasticSearchIndexService.class );
        indexService = mock( IndexService.class );
        contentIndexDataFactory = mock( ContentIndexDataFactory.class );
        contentDao = mock( ContentDao.class );

        journal = new IndexTransactionJournal( elasticSearchIndexService, indexService, contentIndexDataFactory, contentDao );

    }

    @Test
    public void assert_equal_journal_entries_committed_once_only()
    {
        final ContentKey contentKey = new ContentKey( 1 );

        final Map<ContentKey, ContentEntity> contentKeyObjectTreeMap = new LinkedHashMap<ContentKey, ContentEntity>();
        contentKeyObjectTreeMap.put( contentKey, new ContentEntity() );

        when( contentDao.findByKeys( isA( FindContentByKeysCommand.class ) ) ).thenReturn( contentKeyObjectTreeMap );
        when( indexService.createContentDocument( isA( ContentEntity.class ), isA( Boolean.class ) ) ).thenReturn(
            createContentIndexData() );
        when( contentIndexDataFactory.create( isA( ContentDocument.class ), isA( Boolean.class ) ) ).thenReturn(
            new ContentIndexData( contentKey ) );

        journal.registerUpdate( contentKey, false );
        journal.registerUpdate( new ContentKey( 1 ), false );
        journal.afterCommit();

        verify( elasticSearchIndexService, times( 1 ) ).index( isA( String.class ), isA( ContentIndexData.class ) );

    }

    private ContentDocument createContentIndexData()
    {
        final ContentDocument contentDocument = new ContentDocument( new ContentKey( 1 ) );
        return contentDocument;
    }


}
