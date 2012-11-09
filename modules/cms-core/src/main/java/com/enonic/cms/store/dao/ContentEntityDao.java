/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentMap;
import com.enonic.cms.core.content.ContentSpecification;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.store.support.EntityPageList;

@Repository("contentDao")
public class ContentEntityDao
    extends AbstractBaseEntityDao<ContentEntity>
    implements ContentDao
{
    private CacheFacade entityCache;

    public ContentEntity findByKey( ContentKey contentKey )
    {
        return get( ContentEntity.class, contentKey );
    }

    public ContentMap findByKeys( final FindContentByKeysCommand command )
    {
        final FindContentByKeysQuerier findContentByKeysQuerier =
            new FindContentByKeysQuerier( getHibernateTemplate().getSessionFactory().getCurrentSession(), command.getContentEagerFetches(),
                                          command.isFetchEntitiesAsReadOnly() );
        final FindContentByKeysCommandExecutor commandExecutor =
            new FindContentByKeysCommandExecutor( entityCache, getHibernateTemplate(), findContentByKeysQuerier );

        return commandExecutor.execute( command.getContentKeys(), command.isByPassCache() );
    }

    @SuppressWarnings("unchecked")
    public List<ContentKey> findBySpecification( ContentSpecification specification, String orderBy, int count )
    {

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(
            getContentKeysHQL( specification, orderBy, false ) );

        compiled.setCacheable( true );
        compiled.setMaxResults( count );

        @SuppressWarnings({"unchecked"}) List<ContentKey> list = compiled.list();

        List<ContentKey> contentKeys = new ArrayList<ContentKey>();
        for ( ContentKey row : list )
        {
            contentKeys.add( row );
        }

        return contentKeys;
    }

    private String getContentKeysHQL( ContentSpecification specification, String orderBy, boolean count )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        if ( count )
        {
            hqlQuery.addSelect( "count (c.key) " );
        }
        else
        {
            hqlQuery.addSelect( "c.key" );
        }
        hqlQuery.addFromTable( "ContentEntity", "c", SelectBuilder.NO_JOIN, null );

        applyIsDeletedFilter( specification, hqlQuery );

        applyAssignedToContentsHql( specification, hqlQuery );

        if ( orderBy != null )
        {
            hqlQuery.addOrderBy( orderBy );
        }

        return hqlQuery.toString();
    }

    private void applyAssignedToContentsHql( ContentSpecification specification, SelectBuilder hqlQuery )
    {
        hqlQuery.addFilter( "AND", "c.assignee = '" + specification.getAssignee().getKey().toString() + "'" );

        if ( specification.assignedDraftsOnly() )
        {
            hqlQuery.addFilter( "AND", "c.draftVersion != null" );
        }
    }

    private void applyIsDeletedFilter( ContentSpecification specification, final SelectBuilder hqlQuery )
    {
        if ( !specification.doIncludeDeleted() )
        {
            hqlQuery.addFilter( "AND", "c.deleted = 0" );
        }
    }

    public Collection<RelatedChildContent> findRelatedChildrenByKeys( RelatedChildContentQuery relatedChildContentQuery )
    {
        final FindRelatedChildrenCommand command = new FindRelatedChildrenCommand( entityCache, getHibernateTemplate() );
        return command.execute( relatedChildContentQuery );
    }

    public Collection<RelatedParentContent> findRelatedParentByKeys( final RelatedParentContentQuery relatedParentContentQuery )
    {
        final FindRelatedParentsCommand command = new FindRelatedParentsCommand( entityCache, getHibernateTemplate() );
        return command.execute( relatedParentContentQuery );
    }

    public List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByContentTypeKey", new String[]{"contentTypeKey"},
                                 new Object[]{contentType.getKey()} );
    }

    public List<ContentKey> findContentKeysByCategory( CategoryKey category )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByCategoryKey", new String[]{"categoryKey"},
                                 new Object[]{category} );
    }

    @Override
    public boolean checkNameExists( CategoryEntity category, String name )
    {
        return 0L != findSingleByNamedQuery( Long.class, "ContentEntity.countContentInCategoryByName", new String[]{"categoryKey", "name"},
                                             new Object[]{category.getKey(), name} );
    }

    @Override
    public long countContentByCategory( CategoryEntity category )
    {
        return findSingleByNamedQuery( Long.class, "ContentEntity.countContentByCategory", new String[]{"categoryKey"},
                                       new Object[]{category.getKey()} );
    }

    public int getNumberOfRelatedParentsByKey( List<ContentKey> contentKeys )
    {
        return doGetNumberOfRelatedParentsByKey( contentKeys );
    }

    private int doGetNumberOfRelatedParentsByKey( List<ContentKey> contentKeys )
    {
        String hql = getNumberOfRelatedParentsByKeyHQL( contentKeys );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( true );

        @SuppressWarnings({"unchecked"}) int count = ( (Number) compiled.uniqueResult() ).intValue();

        return count;
    }

    public String getNumberOfRelatedParentsByKeyHQL( List<ContentKey> contentKeys )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "count(*)" );
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "cv", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "cv.key = rc.key.parentContentVersionKey" );
        hqlQuery.addFilter( "AND", "c.mainVersion.key = cv.key" );
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentKey>( "rc.key.childContentKey", contentKeys )
        {
            public void appendValue( StringBuffer sql, ContentKey value )
            {
                sql.append( value.toString() );
            }
        }.toString() );

        hqlQuery.addFilter( "AND", "c.deleted = 0" );
        return hqlQuery.toString();
    }

    public int findCountBySpecification( ContentSpecification specification )
    {
        List<Long> result = getHibernateTemplate().find( getContentKeysHQL( specification, null, true ) );

        if ( result == null || result.size() == 0 )
        {
            return 0;
        }

        Long count = result.get( 0 );

        return count.intValue();

    }

    public List<ContentKey> findAll()
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findAll" );
    }

    public EntityPageList<ContentEntity> findAll( int index, int count )
    {
        return findPageList( ContentEntity.class, "x.deleted = 0", index, count );
    }

    @Autowired
    public void setCacheManager( CacheManager cacheManager )
    {
        this.entityCache = cacheManager.getEntityCache();
    }
}
