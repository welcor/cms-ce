/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTrace;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTracer;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.RelatedChildContentQuery;


public abstract class AbstractRelatedContentFetcher
{
    protected final ContentDao contentDao;

    protected Integer maxChildrenLevel;

    protected RelatedContentResultSetImpl relatedContentResultSet;

    private boolean includeOfflineContent;

    protected Collection<GroupKey> securityFilter;

    protected Date availableCheckDate;

    private Map<ContentVersionKey, RelatedContent> relatedContentByVersionKey = new HashMap<ContentVersionKey, RelatedContent>();

    private Map<ContentKey, RelatedParentContent> relatedParentContentByContentKey = new HashMap<ContentKey, RelatedParentContent>();

    protected Set<ContentKey> visitedParentRelatedContent = new HashSet<ContentKey>();

    protected Set<ContentKey> visitedChildRelatedContent = new HashSet<ContentKey>();

    protected final RelatedContentFetchTrace trace;

    protected AbstractRelatedContentFetcher( ContentDao contentDao, final RelatedContentFetchTrace trace )
    {
        this.contentDao = contentDao;
        this.trace = trace;
    }

    public void setMaxChildrenLevel( Integer value )
    {
        maxChildrenLevel = value;
    }

    public void setSecurityFilter( Collection<GroupKey> securityFilter )
    {
        this.securityFilter = securityFilter;
    }

    public void setAvailableCheckDate( Date value )
    {
        availableCheckDate = value;
    }

    public void setIncludeOfflineContent( boolean value )
    {
        includeOfflineContent = value;
    }

    public boolean includeOfflineContent()
    {
        return includeOfflineContent;
    }

    public Integer getMaxChildrenLevel()
    {
        return maxChildrenLevel;
    }

    protected Collection<RelatedChildContent> doFindRelatedChildren( Collection<ContentVersionEntity> versions )
    {
//        new code:
//        if ( versions.size() == 0 )
//        {
//            return new ArrayList<RelatedChildContent>();
//        }
//
//        List<RelatedChildContent> relatedChildContents = new ArrayList<RelatedChildContent>();
//        for ( ContentVersionEntity version : versions )
//        {
//            // TODO: alternative: version.getRelatedChildren( false ) faster?
//            for ( ContentKey relatedChildKey : version.getContentData().resolveRelatedContentKeys() )
//            {
//                ContentEntity relatedChild = contentDao.findByKey( relatedChildKey );
//                if ( !relatedChild.isDeleted() )
//                {
//                    relatedChildContents.add( new RelatedChildContent( version.getKey(), relatedChild ) );
//                }
//            }
//        }
//        // TODO: needs to be sorted on createdAt?
//        return relatedChildContents;

        if ( versions.size() == 0 )
        {
            return new ArrayList<RelatedChildContent>();
        }

        // TODO: This logic could probably be rewritten to resolve RelatedChildContents from version.getContentData instead,
        // this would probably performfaster too, since we then would be using the entity cache instead to retrieve the related content.

        final List<ContentVersionKey> versionKeys = ContentVersionKey.createList( versions );
        RelatedChildContentQuery relatedChildContentQuery = new RelatedChildContentQuery();
        relatedChildContentQuery.contentVersions( versionKeys );
        relatedChildContentQuery.now( new DateTime( availableCheckDate ) );
        relatedChildContentQuery.includeOfflineContent( includeOfflineContent() );
        relatedChildContentQuery.securityFilter( securityFilter );
        return contentDao.findRelatedChildrenByKeys( relatedChildContentQuery );
    }

    protected List<RelatedChildContent> doAddAndFetchChildren( final Collection<RelatedChildContent> children, final int level,
                                                               boolean includeVisited )
    {
        final int nextLevel = level - 1;
        final boolean atLastLevel = nextLevel == 0;

        final List<RelatedChildContent> addedRelatedContent = new ArrayList<RelatedChildContent>();
        final List<ContentEntity> addedContent = new ArrayList<ContentEntity>();

        for ( RelatedChildContent relatedToAdd : children )
        {
            if ( isAddable( relatedToAdd, includeVisited ) )
            {
                addedRelatedContent.add( relatedToAdd );
                addedContent.add( relatedToAdd.getContent() );
                relatedContentResultSet.add( relatedToAdd );

                registerForFastAccess( relatedToAdd );

                // connect the related content to the other related content it belong to
                doConnectRelatedContent( relatedToAdd );
            }

            visitedChildRelatedContent.add( relatedToAdd.getContent().getKey() );
        }

        // fetch more children...
        if ( !atLastLevel )
        {
            final Collection<RelatedChildContent> nextLevelChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( addedContent ) );
            RelatedContentFetchTracer.traceChildrenFetch( level, nextLevelChildren.size(), trace );
            if ( nextLevelChildren.size() > 0 )
            {
                doAddAndFetchChildren( nextLevelChildren, nextLevel, includeVisited );
            }
        }

        return addedRelatedContent;
    }

    protected abstract boolean isAddable( RelatedContent relatedToAdd, boolean includeVisited );

    protected void registerForFastAccess( RelatedChildContent relatedChildContent )
    {
        relatedContentByVersionKey.put( relatedChildContent.getContent().getMainVersion().getKey(), relatedChildContent );
    }

    protected void registerForFastAccess( RelatedParentContent relatedParentContent )
    {
        relatedContentByVersionKey.put( relatedParentContent.getParentMainVersionKey(), relatedParentContent );
        relatedParentContentByContentKey.put( relatedParentContent.getContent().getKey(), relatedParentContent );
    }

    protected void doConnectRelatedContent( RelatedChildContent relatedContent )
    {
        RelatedContent parent = relatedContentByVersionKey.get( relatedContent.getParentVersionKey() );
        if ( parent != null )
        {
            parent.addRelatedChild( relatedContent );
        }
    }

    protected void doConnectRelatedContent( RelatedParentContent relatedContent )
    {
        RelatedParentContent child = relatedParentContentByContentKey.get( relatedContent.getChildContentKey() );
        if ( child != null )
        {
            child.addRelatedParent( relatedContent );
        }
    }

    protected List<ContentVersionEntity> gatherMainVersionsFromContent( final Collection<ContentEntity> contents )
    {
        final List<ContentVersionEntity> versions = new ArrayList<ContentVersionEntity>( contents.size() );
        for ( ContentEntity content : contents )
        {
            versions.add( content.getMainVersion() );
        }
        return versions;
    }

    protected List<ContentKey> gatherContentKeysFromContent( final Collection<ContentEntity> contents )
    {
        final List<ContentKey> keys = new ArrayList<ContentKey>( contents.size() );
        for ( ContentEntity content : contents )
        {
            keys.add( content.getKey() );
        }
        return keys;
    }
}
