/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

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

    @Autowired
    private ContentDocumentFactory contentDocumentFactory;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    /* timeout: 12 timer (60 sec * 5 min = 300 sec) */
    /* OLD: timeout: 12 timer (3600 * 12 = 43200) */
    public void reindex( final List<ContentKey> contentKeys )
    {
        final FindContentByKeysCommand command = new FindContentByKeysCommand().contentKeys( contentKeys ).eagerFetches(
            ContentEagerFetches.PRESET_FOR_INDEXING ).fetchEntitiesAsReadOnly( true ).byPassCache( true );

        final ContentMap contentMap = contentDao.findByKeys( command );

        for ( ContentEntity content : contentMap )
        {
            if ( content.isDeleted() )
            {
                doRemoveContentFromIndex( content );
            }
            else
            {
                doIndexContent( content );
            }
        }

        /* Clear all intances in first level cache since the transaction boundary doesn't (single session) */
        contentDao.getHibernateTemplate().clear();
    }


    @Override
    public ContentDocument createContentDocument( ContentEntity content, final boolean updateMetadataOnly )
    {
        return contentDocumentFactory.createContentDocument( content, updateMetadataOnly );
    }

    @Override
    public void optimizeIndex()
    {
        contentIndexService.optimize();
    }

    @Override
    public void reinitializeIndex()
    {
        contentIndexService.reinitializeIndex();
    }

    @Override
    public boolean indexExists()
    {
        return contentIndexService.indexExists();
    }

    @Override
    public void createIndex()
    {
        contentIndexService.createIndex();
    }

    private void doRemoveContentFromIndex( ContentEntity content )
    {
        contentIndexService.remove( content.getKey() );
    }

    private void doIndexContent( ContentEntity content )
    {
        ContentDocument indexedDoc = contentDocumentFactory.createContentDocument( content, false );
        contentIndexService.index( indexedDoc );

        contentDao.getHibernateTemplate().flush();
    }

}
