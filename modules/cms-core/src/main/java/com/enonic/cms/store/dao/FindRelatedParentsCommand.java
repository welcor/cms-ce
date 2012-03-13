package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Lists;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.core.security.group.GroupKey;

class FindRelatedParentsCommand
{
    private CacheFacade entityCache;

    private HibernateTemplate hibernateTemplate;

    FindRelatedParentsCommand( CacheFacade entityCache, HibernateTemplate hibernateTemplate )
    {
        this.entityCache = entityCache;
        this.hibernateTemplate = hibernateTemplate;
    }

    Collection<RelatedParentContent> execute( final RelatedParentContentQuery relatedParentContentQuery )
    {
        if ( relatedParentContentQuery.getContents() == null || relatedParentContentQuery.getContents().size() == 0 )
        {
            throw new IllegalArgumentException( "Given childContentKeys must contain values" );
        }

        final String hql = getRelatedParentsByKeyHQL( relatedParentContentQuery );

        final Query compiled = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( false );
        compiled.setReadOnly( true );
        compiled.setParameter( "deleted", 0 );
        if ( !relatedParentContentQuery.isIncludeOfflineContent() )
        {
            compiled.setParameter( "timeNow", relatedParentContentQuery.getNow().minuteOfHour().roundFloorCopy().toDate() );
            compiled.setParameter( "status", ContentStatus.APPROVED.getKey() );
        }

        final List<ContentKey> childContentKeys = relatedParentContentQuery.getContents();
        for ( int i = 0; i < childContentKeys.size(); i++ )
        {
            compiled.setParameter( "c" + i, childContentKeys.get( i ) );
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

        final Set<ContentKey> contentKeys = new LinkedHashSet<ContentKey>( list.size() );
        for ( Object[] row : list )
        {
            contentKeys.add( (ContentKey) row[2] );
        }
        final Map<ContentKey, ContentEntity> contentMapByKey = new HashMap<ContentKey, ContentEntity>( contentKeys.size() );
        FindContentByKeysCommand command = new FindContentByKeysCommand( entityCache, hibernateTemplate, new FindContentByKeysQuerier(
            hibernateTemplate.getSessionFactory().getCurrentSession() ) );
        for ( ContentEntity c : command.execute( new ArrayList<ContentKey>( contentKeys ) ) )
        {
            contentMapByKey.put( c.getKey(), c );
        }
        final List<RelatedParentContent> relatedChildContents = new ArrayList<RelatedParentContent>();
        for ( Object[] row : list )
        {
            ContentKey childContentKey = (ContentKey) row[0];
            ContentVersionKey parentContentMainVersionKey = (ContentVersionKey) row[1];
            ContentKey parentContentKey = (ContentKey) row[2];
            ContentEntity parentContent = contentMapByKey.get( parentContentKey );
            RelatedParentContent relatedParentContent =
                new RelatedParentContent( childContentKey, parentContent, parentContentMainVersionKey );
            relatedChildContents.add( relatedParentContent );
        }

        return relatedChildContents;
    }

    private String getRelatedParentsByKeyHQL( final RelatedParentContentQuery relatedParentContentQuery )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );
        hqlQuery.addSelect( "rc.key.childContentKey" );
        hqlQuery.addSelectColumn( "c.mainVersion.key" );
        hqlQuery.addSelectColumn( "c.key" );
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
                                                sql.append( ":g" ).append( getIndex() );
                                            }
                                        }.toString() );
            hqlQuery.append( "AND c.key IN ( " + securitySubQuery + " )" );
        }
        hqlQuery.addOrderBy( "c.createdAt" );
        return hqlQuery.toString();
    }
}
