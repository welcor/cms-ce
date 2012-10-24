package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentMap;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.security.group.GroupKey;

class FindRelatedChildrenCommand
{
    private CacheFacade entityCache;

    private HibernateTemplate hibernateTemplate;

    FindRelatedChildrenCommand( CacheFacade entityCache, HibernateTemplate hibernateTemplate )
    {
        this.entityCache = entityCache;
        this.hibernateTemplate = hibernateTemplate;
    }

    Collection<RelatedChildContent> execute( RelatedChildContentQuery relatedChildContentQuery )
    {
        if ( relatedChildContentQuery.getContentVersions() == null || relatedChildContentQuery.getContentVersions().size() == 0 )
        {
            throw new IllegalArgumentException( "Given contentVersionKeys must contain values" );
        }

        String hql = getRelatedChildrenByKeyHQL( relatedChildContentQuery );

        Query compiled = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( false );
        compiled.setReadOnly( true );
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
            compiled.setParameter( "one", 1 );
            List<GroupKey> securityFilter = Lists.newArrayList( relatedChildContentQuery.getSecurityFilter() );
            for ( int i = 0; i < securityFilter.size(); i++ )
            {
                compiled.setParameter( "g" + i, securityFilter.get( i ) );
            }
        }

        @SuppressWarnings({"unchecked"}) List<Object[]> list = compiled.list();

        final Set<ContentKey> contentKeys = new LinkedHashSet<ContentKey>( list.size() );
        for ( Object[] row : list )
        {
            contentKeys.add( (ContentKey) row[1] );
        }

        final ContentMap contentMap = retrieveContent( contentKeys );

        final List<RelatedChildContent> relatedChildContrents = new ArrayList<RelatedChildContent>();
        for ( Object[] row : list )
        {
            ContentVersionKey versionKey = (ContentVersionKey) row[0];
            ContentKey contentKey = (ContentKey) row[1];
            ContentEntity content = contentMap.get( contentKey );
            RelatedChildContent relatedChildContent = new RelatedChildContent( versionKey, content );
            relatedChildContrents.add( relatedChildContent );
        }

        return relatedChildContrents;
    }

    private String getRelatedChildrenByKeyHQL( final RelatedChildContentQuery relatedChildContentQuery )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.parentContentVersionKey" );
        hqlQuery.addSelectColumn( "c.key" );
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
            securitySubQuery.addFilter( "AND", "ca.readAccess = :one" );
            securitySubQuery.addFilter( "AND", new InClauseBuilder<GroupKey>( "ca.group.key", relatedChildContentQuery.getSecurityFilter() )
            {
                public void appendValue( final StringBuffer sql, final GroupKey value )
                {
                    sql.append( ":g" ).append( getIndex() );
                }
            }.toString() );
            hqlQuery.append( "AND c.key IN ( " + securitySubQuery + " )" );
        }
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
    }

    private ContentMap retrieveContent( final Set<ContentKey> contentKeys )
    {
        final FindContentByKeysQuerier findContentByKeysQuerier =
            new FindContentByKeysQuerier( hibernateTemplate.getSessionFactory().getCurrentSession(), ContentEagerFetches.PRESET_FOR_PORTAL,
                                          true );

        final FindContentByKeysCommandExecutor commandExecutor =
            new FindContentByKeysCommandExecutor( entityCache, hibernateTemplate, findContentByKeysQuerier );
        return commandExecutor.execute( new ArrayList<ContentKey>( contentKeys ), false );
    }
}
