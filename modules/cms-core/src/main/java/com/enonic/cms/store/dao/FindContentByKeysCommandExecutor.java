package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

class FindContentByKeysCommandExecutor
{
    private ContentExistInCacheResolver contentExistInCacheResolver;

    private HibernateTemplate hibernateTemplate;

    private FindContentByKeysQuerier findContentByKeysQuerier;

    FindContentByKeysCommandExecutor( final CacheFacade entityCache, final HibernateTemplate hibernateTemplate,
                                      final FindContentByKeysQuerier findContentByKeysQuerier )
    {
        this.contentExistInCacheResolver = new ContentExistInCacheResolver( entityCache );
        this.hibernateTemplate = hibernateTemplate;
        this.findContentByKeysQuerier = findContentByKeysQuerier;
    }

    Map<ContentKey, ContentEntity> execute( final List<ContentKey> contentKeys, final boolean byPassCache )
    {
        LinkedHashMap<ContentKey, ContentEntity> contentMapByKey = new LinkedHashMap<ContentKey, ContentEntity>();
        for ( ContentKey contentKey : contentKeys )
        {
            contentMapByKey.put( contentKey, null );
        }
        try
        {

            if ( byPassCache )
            {
                final List<ContentEntity> contentsFromDB = findContentByKeysQuerier.queryContent( contentKeys );
                for ( ContentEntity c : contentsFromDB )
                {
                    contentMapByKey.put( c.getKey(), c );
                }
                return contentMapByKey;
            }
            else
            {
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
        }
        finally
        {
            final List<ContentKey> nullEntries = new ArrayList<ContentKey>();
            for ( Map.Entry<ContentKey, ContentEntity> entry : contentMapByKey.entrySet() )
            {
                if ( entry.getValue() == null )
                {
                    nullEntries.add( entry.getKey() );
                }
            }
            for ( ContentKey contentKeyToRemove : nullEntries )
            {
                contentMapByKey.remove( contentKeyToRemove );
            }
        }
    }

    private void findContentInCache( Iterable<ContentKey> contentKeys, Map<ContentKey, ContentEntity> contentsFoundInCache,
                                     List<ContentKey> contentsNotFoundInCache )
    {
        for ( final ContentKey contentKey : contentKeys )
        {
            final boolean contentExistsInCache = contentExistInCacheResolver.contentExistsInCache( contentKey );
            if ( contentExistsInCache )
            {
                final ContentEntity contentFoundInCache = hibernateTemplate.get( ContentEntity.class, contentKey );
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
