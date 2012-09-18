/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.search.query.ContentDocument;
import com.enonic.cms.core.search.query.ContentIndexService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentEagerFetches;
import com.enonic.cms.store.dao.FindContentByKeysCommand;

@Service("indexService")
public final class IndexServiceImpl
    implements IndexService
{

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private ContentDao contentDao;

    private final ContentDocumentFactory contentDocumentFactory = new ContentDocumentFactory();

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    /* timeout: 12 timer (60 sec * 5 min = 300 sec) */
    /* OLD: timeout: 12 timer (3600 * 12 = 43200) */
    public void regenerateIndex( final List<ContentKey> contentKeys )
    {
        final FindContentByKeysCommand command = new FindContentByKeysCommand().contentKeys( contentKeys ).eagerFetches(
            ContentEagerFetches.PRESET_FOR_INDEXING ).fetchEntitiesAsReadOnly( true ).byPassCache( true );

        final SortedMap<ContentKey, ContentEntity> contentMapByKey = contentDao.findByKeys( command );

        for ( ContentKey contentKey : contentKeys )
        {
            final ContentEntity content = contentMapByKey.get( contentKey );

            if ( content.isDeleted() )
            {
                doRemoveIndex( content );
            }
            else
            {
                doIndex( content, true );
            }
        }

        /* Clear all intances in first level cache since the transaction boundary doesn't (single session) */
        contentDao.getHibernateTemplate().clear();
    }


    @Override
    public ContentDocument createContentDocument( ContentEntity content, final boolean skipAttachments )
    {
        return contentDocumentFactory.createContentDocument( content, skipAttachments );
    }

    @Override
    public void optimizeIndex()
    {
        contentIndexService.optimize();
    }

    @Override
    public void initializeMapping()
    {
        contentIndexService.initializeMapping();
    }

    private void doRemoveIndex( ContentEntity content )
    {
        contentIndexService.remove( content.getKey() );
    }

    private void doIndex( ContentEntity content, boolean deleteExisting )
    {
        ContentDocument indexedDoc = contentDocumentFactory.createContentDocument( content, false );
        contentIndexService.index( indexedDoc, deleteExisting );

        contentDao.getHibernateTemplate().flush();
    }

}
