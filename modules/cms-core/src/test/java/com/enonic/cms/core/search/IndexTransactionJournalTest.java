/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentMap;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.search.builder.ContentIndexData;
import com.enonic.cms.core.search.builder.ContentIndexDataFactory;
import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.ContentIndexService;
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

    ContentIndexService contentIndexService;

    ContentDao contentDao;

    ContentIndexDataFactory contentIndexDataFactory;

    @Before
    public void setup()
    {
        contentIndexService = mock( ContentIndexService.class );
        indexService = mock( IndexService.class );
        contentIndexDataFactory = mock( ContentIndexDataFactory.class );
        contentDao = mock( ContentDao.class );

        journal = new IndexTransactionJournal( contentIndexService, indexService, contentDao );
    }

    @Test
    public void assert_equal_journal_entries_committed_once_only()
    {
        final ContentKey contentKey_1 = new ContentKey( 1 );
        final ContentMap contentMap = new ContentMap( Lists.newArrayList( contentKey_1 ) );
        ContentEntity content_1 = createContent( contentKey_1 );
        contentMap.add( content_1 );

        when( contentDao.findByKeys( isA( FindContentByKeysCommand.class ) ) ).thenReturn( contentMap );
        when( indexService.createContentDocument( isA( ContentEntity.class ), isA( Boolean.class ) ) ).thenReturn(
            createContentIndexData() );
        when( contentIndexDataFactory.create( isA( ContentDocument.class ), isA( Boolean.class ) ) ).thenReturn(
            new ContentIndexData( contentKey_1 ) );

        journal.registerUpdate( contentKey_1, false );
        journal.registerUpdate( new ContentKey( 1 ), false );
        journal.afterCommit();

        verify( contentIndexService, times( 1 ) ).index( isA( ContentDocument.class ), isA( Boolean.class ) );
    }

    private ContentEntity createContent( ContentKey contentKey )
    {
        ContentEntity c = new ContentEntity();
        c.setKey( contentKey );
        return c;
    }

    private ContentDocument createContentIndexData()
    {
        return new ContentDocument( new ContentKey( 1 ) );
    }
}
