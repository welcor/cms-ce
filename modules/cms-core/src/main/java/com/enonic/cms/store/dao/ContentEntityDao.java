/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentSpecification;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.support.EntityPageList;

@Repository("contentDao")
public class ContentEntityDao
    extends AbstractBaseEntityDao<ContentEntity>
    implements ContentDao
{

    public ContentEntity findByKey( ContentKey contentKey )
    {
        return get( ContentEntity.class, contentKey );
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
        if ( relatedChildContentQuery.getContentVersions() == null || relatedChildContentQuery.getContentVersions().size() == 0 )
        {
            throw new IllegalArgumentException( "Given contentVersionKeys must contain values" );
        }

        String hql = getRelatedChildrenByKeyHQL( relatedChildContentQuery );

        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( false );
        compiled.setParameter( "deleted", 0 );
        if ( !relatedChildContentQuery.isIncludeOfflineContent() )
        {
            compiled.setParameter( "status", ContentStatus.APPROVED.getKey() );
            compiled.setParameter( "timeNow", relatedChildContentQuery.getNow().minuteOfHour().roundFloorCopy().toDate() );
        }

        final List<ContentVersionKey> contentVersionKeys = relatedChildContentQuery.getContentVersions();
        for ( int i = 0; i < contentVersionKeys.size(); i++ )
        {
            compiled.setParameter( "cv" + i, contentVersionKeys.get( i ) );
        }

        if ( relatedChildContentQuery.hasSecurityFilter() )
        {
            List<GroupKey> securityFilter = Lists.newArrayList( relatedChildContentQuery.getSecurityFilter() );
            for ( int i = 0; i < securityFilter.size(); i++ )
            {
                compiled.setParameter( "g" + i, securityFilter.get( i ) );
            }
        }

        @SuppressWarnings({"unchecked"}) List<Object[]> list = compiled.list();

        List<RelatedChildContent> relatedChildContrents = new ArrayList<RelatedChildContent>();
        for ( Object[] row : list )
        {
            ContentVersionKey versionKey = (ContentVersionKey) row[0];
            ContentEntity content = (ContentEntity) row[1];
            RelatedChildContent relatedChildContent = new RelatedChildContent( versionKey, content );
            relatedChildContrents.add( relatedChildContent );
        }

        return relatedChildContrents;
    }

    public Collection<RelatedParentContent> findRelatedParentByKeys( final RelatedParentContentQuery relatedParentContentQuery )
    {
        if ( relatedParentContentQuery.getContents() == null || relatedParentContentQuery.getContents().size() == 0 )
        {
            throw new IllegalArgumentException( "Given contentKeys must contain values" );
        }

        final String hql = getRelatedParentsByKeyHQL( relatedParentContentQuery );

        final Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( false );
        compiled.setParameter( "deleted", 0 );
        if ( !relatedParentContentQuery.isIncludeOfflineContent() )
        {
            compiled.setParameter( "timeNow", relatedParentContentQuery.getNow().minuteOfHour().roundFloorCopy().toDate() );
            compiled.setParameter( "status", ContentStatus.APPROVED.getKey() );
        }

        final List<ContentKey> contentKeys = relatedParentContentQuery.getContents();
        for ( int i = 0; i < contentKeys.size(); i++ )
        {
            compiled.setParameter( "c" + i, contentKeys.get( i ) );
        }

        if ( relatedParentContentQuery.hasSecurityFilter() )
        {
            final List<GroupKey> securityFilter = Lists.newArrayList( relatedParentContentQuery.getSecurityFilter() );
            for ( int i = 0; i < securityFilter.size(); i++ )
            {
                compiled.setParameter( "g" + i, securityFilter.get( i ) );
            }
        }

        @SuppressWarnings({"unchecked"}) List<Object[]> list = compiled.list();

        final List<RelatedParentContent> relatedChildContents = new ArrayList<RelatedParentContent>();
        for ( Object[] row : list )
        {
            ContentKey childContentKey = (ContentKey) row[0];
            ContentVersionKey parentContentMainVersionKey = (ContentVersionKey) row[1];
            ContentEntity parentContent = (ContentEntity) row[2];
            RelatedParentContent relatedParentContent =
                new RelatedParentContent( childContentKey, parentContent, parentContentMainVersionKey );
            relatedChildContents.add( relatedParentContent );
        }

        return relatedChildContents;
    }

    public List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByContentTypeKey", new String[]{"contentTypeKey"},
                                 new Object[]{contentType.getKey()} );
    }

    public List<ContentKey> findContentKeysByCategory( CategoryEntity category )
    {
        return findByNamedQuery( ContentKey.class, "ContentEntity.findContentKeysByCategoryKey", new String[]{"categoryKey"},
                                 new Object[]{category.getKey()} );
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

    private String getRelatedChildrenByKeyHQL( final RelatedChildContentQuery relatedChildContentQuery )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.parentContentVersionKey" );
        hqlQuery.addSelectColumn( "c" );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "mainVersion", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "rc.key.childContentKey = c.key" );
        hqlQuery.addFilter( "AND", "mainVersion.key = c.mainVersion.key" );
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentVersionKey>( "rc.key.parentContentVersionKey",
                                                                           relatedChildContentQuery.getContentVersions() )
        {
            public void appendValue( final StringBuffer sql, final ContentVersionKey value )
            {
                sql.append( ":cv" ).append( getIndex() );
            }
        }.toString() );
        if ( !relatedChildContentQuery.isIncludeOfflineContent() )
        {
            hqlQuery.addFilter( "AND", "mainVersion.status = :status" );
            hqlQuery.addFilter( "AND", "c.availableFrom <= :timeNow" );
            hqlQuery.addFilter( "AND", "(c.availableTo is null OR c.availableTo > :timeNow)" );
        }
        hqlQuery.addFilter( "AND", "c.deleted = :deleted" );
        if ( relatedChildContentQuery.hasSecurityFilter() )
        {
            final SelectBuilder securitySubQuery = new SelectBuilder( 0 );
            securitySubQuery.addSelect( "ca.content.key" );
            securitySubQuery.addFromTable( ContentAccessEntity.class.getName(), "ca", SelectBuilder.NO_JOIN, null );
            securitySubQuery.addFilter( "AND", new InClauseBuilder<GroupKey>( "ca.group.key", relatedChildContentQuery.getSecurityFilter() )
            {
                public void appendValue( final StringBuffer sql, final GroupKey value )
                {
                    sql.append( ":g" + getIndex() );
                }
            }.toString() );
            hqlQuery.append( "AND c.key IN ( " + securitySubQuery + " )" );
        }
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
    }

    private String getRelatedParentsByKeyHQL( final RelatedParentContentQuery relatedParentContentQuery )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.childContentKey" );
        hqlQuery.addSelectColumn( "c.mainVersion.key" );
        hqlQuery.addSelectColumn( "c" );
        hqlQuery.addFromTable( ContentVersionEntity.class.getName(), "mainVersion", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( RelatedContentEntity.class.getName(), "rc", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFromTable( ContentEntity.class.getName(), "c", SelectBuilder.NO_JOIN, null );

        if ( relatedParentContentQuery.isIncludeOnlyMainVersions() )
        {
            hqlQuery.addFilter( "AND", "mainVersion.key = c.mainVersion.key" );
        }
        else
        {
            hqlQuery.addFilter( "AND", "c.key = mainVersion.content.key" );
        }

        hqlQuery.addFilter( "AND", "mainVersion.key = rc.key.parentContentVersionKey" );

        // the content to find parents to
        hqlQuery.addFilter( "AND", new InClauseBuilder<ContentKey>( "rc.key.childContentKey", relatedParentContentQuery.getContents() )
        {
            public void appendValue( final StringBuffer sql, final ContentKey value )
            {
                sql.append( ":c" ).append( getIndex() );
            }
        }.toString() );

        if ( !relatedParentContentQuery.isIncludeOfflineContent() )
        {
            hqlQuery.addFilter( "AND", "mainVersion.status = :status" );
            hqlQuery.addFilter( "AND", "c.availableFrom <= :timeNow" );
            hqlQuery.addFilter( "AND", "(c.availableTo is null OR c.availableTo > :timeNow)" );
        }
        hqlQuery.addFilter( "AND", "c.deleted = :deleted" );
        if ( relatedParentContentQuery.hasSecurityFilter() )
        {
            final SelectBuilder securitySubQuery = new SelectBuilder( 0 );
            securitySubQuery.addSelect( "ca.content.key" );
            securitySubQuery.addFromTable( ContentAccessEntity.class.getName(), "ca", SelectBuilder.NO_JOIN, null );
            securitySubQuery.addFilter( "AND",
                                        new InClauseBuilder<GroupKey>( "ca.group.key", relatedParentContentQuery.getSecurityFilter() )
                                        {
                                            public void appendValue( final StringBuffer sql, final GroupKey value )
                                            {
                                                sql.append( ":g" + getIndex() );
                                            }
                                        }.toString() );
            hqlQuery.append( "AND c.key IN ( " + securitySubQuery + " )" );
        }
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
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
}
