package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.OrderContentKeysByGivenOrderComparator;

class FindContentByKeysCommand
{
    private ContentExistInCacheResolver contentExistInCacheResolver;

    private HibernateTemplate hibernateTemplate;

    private FindContentByKeysQuerier findContentByKeysQuerier;

    FindContentByKeysCommand( CacheFacade entityCache, HibernateTemplate hibernateTemplate,
                              FindContentByKeysQuerier findContentByKeysQuerier )
    {
        this.contentExistInCacheResolver = new ContentExistInCacheResolver( entityCache );
        this.hibernateTemplate = hibernateTemplate;
        this.findContentByKeysQuerier = findContentByKeysQuerier;
    }

    SortedMap<ContentKey, ContentEntity> execute( final List<ContentKey> contentKeys )
    {
        final SortedMap<ContentKey, ContentEntity> contentMapByKey =
            new TreeMap<ContentKey, ContentEntity>( new OrderContentKeysByGivenOrderComparator( contentKeys ) );
        final List<ContentKey> contentsNotFoundInCache = new ArrayList<ContentKey>();

        findContentInCache( contentKeys, contentMapByKey, contentsNotFoundInCache );

        if ( !contentsNotFoundInCache.isEmpty() )
        {
            final List<ContentEntity> contentsFromDB = findContentByKeysQuerier.queryContent( contentsNotFoundInCache );
            for ( ContentEntity c : contentsFromDB )
            {
                contentMapByKey.put( c.getKey(), c );
            }
        }
        return contentMapByKey;
    }

    private void findContentInCache( Iterable<ContentKey> contentKeys, Map<ContentKey, ContentEntity> contentsFoundInCache,
                                     List<ContentKey> contentsNotFoundInCache )
    {
        for ( final ContentKey contentKey : contentKeys )
        {
            final boolean contentExistsInCache = contentExistInCacheResolver.contentExistsInCache( contentKey );
            if ( contentExistsInCache )
            {
                final ContentEntity contentFoundInCache = (ContentEntity) hibernateTemplate.get( ContentEntity.class, contentKey );
                if ( contentFoundInCache != null )
                {
                    contentsFoundInCache.put( contentFoundInCache.getKey(), contentFoundInCache );
                }
            }
            else
            {
                contentsNotFoundInCache.add( contentKey );
            }
        }
    }
}
