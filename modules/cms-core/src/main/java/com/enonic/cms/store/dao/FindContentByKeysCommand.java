package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

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

    List<ContentEntity> execute( final List<ContentKey> contentKeys )
    {
        final List<ContentEntity> contents = new ArrayList<ContentEntity>();
        final Set<ContentEntity> contentsFoundInCache = new LinkedHashSet<ContentEntity>();
        final List<ContentKey> contentsNotFoundInCache = new ArrayList<ContentKey>();

        findContentInCache( contentKeys, contentsFoundInCache, contentsNotFoundInCache );

        contents.addAll( contentsFoundInCache );

        if ( !contentsNotFoundInCache.isEmpty() )
        {
            final List<ContentEntity> contentsFromDB = findContentByKeysQuerier.queryContent( contentsNotFoundInCache );
            contents.addAll( contentsFromDB );
            Collections.sort( contents, new OrderContentByKeysComparator( contentKeys ) );
        }
        return contents;
    }

    private void findContentInCache( Iterable<ContentKey> contentKeys, Set<ContentEntity> contentsFoundInCache,
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
                    contentsFoundInCache.add( contentFoundInCache );
                }
            }
            else
            {
                contentsNotFoundInCache.add( contentKey );
            }
        }
    }

    private static class OrderContentByKeysComparator
        implements Comparator<ContentEntity>
    {
        private final List<ContentKey> orderMask;

        private OrderContentByKeysComparator( List<ContentKey> orderMask )
        {
            this.orderMask = orderMask;
        }

        public int compare( ContentEntity content1, ContentEntity content2 )
        {
            ContentKey value1 = content1.getKey();
            ContentKey value2 = content2.getKey();

            Integer order1 = orderMask.indexOf( value1 );
            Integer order2 = orderMask.indexOf( value2 );

            order1 = order1 == -1 ? Integer.MAX_VALUE : order1;
            order2 = order2 == -1 ? Integer.MAX_VALUE : order2;

            if ( order1.equals( order2 ) )
            {
                return 0;
            }

            return order1 > order2 ? 1 : -1;
        }
    }
}
