package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.base.Preconditions;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentMap;

class FindContentByKeysCommandExecutor
{
    private ContentExistInCacheResolver contentExistInCacheResolver;

    private HibernateTemplate hibernateTemplate;

    private FindContentByKeysQuerier findContentByKeysQuerier;

    private ContentMap contentMap;

    FindContentByKeysCommandExecutor( final CacheFacade entityCache, final HibernateTemplate hibernateTemplate,
                                      final FindContentByKeysQuerier findContentByKeysQuerier )
    {
        this.contentExistInCacheResolver = new ContentExistInCacheResolver( entityCache );
        this.hibernateTemplate = hibernateTemplate;
        this.findContentByKeysQuerier = findContentByKeysQuerier;
    }

    ContentMap execute( final List<ContentKey> contentKeys, final boolean byPassCache )
    {
        Preconditions.checkState( contentMap == null, "execute can only be invoked once per instance of this class" );

        contentMap = new ContentMap( contentKeys );

        try
        {
            if ( byPassCache )
            {
                contentMap.addAll( findContentByKeysQuerier.queryContent( contentKeys ) );
                return contentMap;
            }
            else
            {
                final List<ContentKey> contentsNotFoundInCache = findContentInCache( contentKeys );
                if ( !contentsNotFoundInCache.isEmpty() )
                {
                    contentMap.addAll( findContentByKeysQuerier.queryContent( contentsNotFoundInCache ) );
                }
                return contentMap;
            }
        }
        finally
        {
            contentMap.removeEntriesWithNullValues();
        }
    }

    private List<ContentKey> findContentInCache( final Iterable<ContentKey> contentKeys )
    {
        final List<ContentKey> contentsNotFoundInCache = new ArrayList<ContentKey>();

        for ( final ContentKey contentKey : contentKeys )
        {
            final boolean contentExistsInCache = contentExistInCacheResolver.contentExistsInCache( contentKey );
            if ( contentExistsInCache )
            {
                final ContentEntity contentFoundInCache = hibernateTemplate.get( ContentEntity.class, contentKey );
                if ( contentFoundInCache != null )
                {
                    contentMap.add( contentFoundInCache );
                }
            }
            else
            {
                contentsNotFoundInCache.add( contentKey );
            }
        }

        return contentsNotFoundInCache;
    }
}
