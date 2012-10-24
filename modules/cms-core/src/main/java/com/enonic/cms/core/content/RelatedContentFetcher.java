/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.content.resultset.RelatedParentContent;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTrace;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTracer;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.RelatedParentContentQuery;


public class RelatedContentFetcher
    extends AbstractRelatedContentFetcher
{
    private Integer maxParentLevel;

    private Integer maxParentChildrenLevel;

    private boolean includeOnlyMainVersions = true;

    /**
     * The content which this fetcher should find the related content of.
     */
    private ContentResultSet contentResultSet;


    public RelatedContentFetcher( ContentDao contentDao, final RelatedContentFetchTrace trace )
    {
        super( contentDao, trace );
    }

    public RelatedContentFetcher( ContentDao contentDao )
    {
        super( contentDao, null );
    }

    public RelatedContentResultSet fetch( final ContentEntity content )
    {
        this.contentResultSet = new ContentResultSetNonLazy( content );
        return doFetch( false );
    }

    /**
     * Retrieves both parent and children of the given content, depending on the parentLevel and childrenLevel configuration of this class.
     *
     * @param content        The initial content to get related content for.
     * @param includeVisited If <code>true</code> all content will be retrieved.  If <code>false</code>, content that have already been
     *                       fetched with this fetcher in a previous call, will not be included in the result set
     * @return A complete result set of related content
     */
    public RelatedContentResultSet fetch( final ContentEntity content, boolean includeVisited )
    {
        this.contentResultSet = new ContentResultSetNonLazy( content );
        return doFetch( includeVisited );
    }

    public RelatedContentResultSet fetch( final ContentResultSet contentResultSet )
    {
        this.contentResultSet = contentResultSet;
        return doFetch( false );
    }

    private RelatedContentResultSet doFetch( boolean includeVisited )
    {
        relatedContentResultSet = new RelatedContentResultSetImpl();

        boolean fetchChildren = maxChildrenLevel > 0;
        if ( fetchChildren )
        {
            Collection<RelatedChildContent> rootRelatedChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( contentResultSet.getContents() ) );
            RelatedContentFetchTracer.traceChildrenFetch( 1, rootRelatedChildren.size(), trace );

            if ( rootRelatedChildren.size() > 0 )
            {
                doAddAndFetchChildren( rootRelatedChildren, maxChildrenLevel, includeVisited );
                for ( RelatedChildContent rootRelatedChild : rootRelatedChildren )
                {
                    relatedContentResultSet.addRootRelatedChild( rootRelatedChild );
                }
            }
        }

        boolean fetchParents = maxParentLevel > 0;
        if ( fetchParents )
        {
            Collection<RelatedParentContent> rootRelatedParents = doFindRelatedParents( contentResultSet.getKeys() );
            RelatedContentFetchTracer.traceParentsFetch( 1, rootRelatedParents.size(), trace );

            if ( rootRelatedParents.size() > 0 )
            {
                doAddAndFetchParents( rootRelatedParents, maxParentLevel, includeVisited );
                for ( RelatedParentContent rootRelatedParent : rootRelatedParents )
                {
                    relatedContentResultSet.addRootRelatedParent( rootRelatedParent );
                }
            }
        }

        return relatedContentResultSet;
    }

    private List<RelatedParentContent> doAddAndFetchParents( final Collection<RelatedParentContent> parentsToAdd, final int level,
                                                             final boolean includeVisited )
    {
        final int nextLevel = level - 1;

        List<RelatedParentContent> addedRelatedContent = new ArrayList<RelatedParentContent>();
        final List<ContentEntity> addedContent = new ArrayList<ContentEntity>();

        for ( RelatedParentContent relatedToAdd : parentsToAdd )
        {
            if ( isAddable( relatedToAdd, includeVisited ) )
            {
                addedRelatedContent.add( relatedToAdd );
                addedContent.add( relatedToAdd.getContent() );
                relatedContentResultSet.add( relatedToAdd );

                registerForFastAccess( relatedToAdd );

                doConnectRelatedContent( relatedToAdd );
            }

            visitedParentRelatedContent.add( relatedToAdd.getContent().getKey() );
        }

        // fetch parent children...
        final boolean fetchParentChildren = level == maxParentLevel && maxParentChildrenLevel > 0;
        if ( fetchParentChildren )
        {
            final Collection<RelatedChildContent> nextLevelChildren =
                doFindRelatedChildren( gatherMainVersionsFromContent( addedContent ) );
            if ( nextLevelChildren.size() > 0 )
            {
                doAddAndFetchChildren( nextLevelChildren, maxParentChildrenLevel, includeVisited );
            }
        }

        // fetch more parents...
        final boolean atLastLevel = nextLevel == 0;
        if ( !atLastLevel )
        {
            final Collection<RelatedParentContent> nextLevelParents = doFindRelatedParents( gatherContentKeysFromContent( addedContent ) );
            RelatedContentFetchTracer.traceParentsFetch( level, nextLevelParents.size(), trace );
            if ( nextLevelParents.size() > 0 )
            {
                doAddAndFetchParents( nextLevelParents, nextLevel, includeVisited );
            }
        }

        return addedRelatedContent;
    }

    @Override
    protected boolean isAddable( final RelatedContent relatedToAdd, boolean includeVisited )
    {
        final boolean contentIsAllreadyVisited;
        if ( relatedToAdd instanceof RelatedParentContent )
        {
            contentIsAllreadyVisited = visitedParentRelatedContent.contains( relatedToAdd.getContent().getKey() );
        }
        else
        {
            contentIsAllreadyVisited = visitedChildRelatedContent.contains( relatedToAdd.getContent().getKey() );
        }

        return includeVisited || !contentIsAllreadyVisited;
    }

    private Collection<RelatedParentContent> doFindRelatedParents( List<ContentKey> contentKeys )
    {
        if ( contentKeys.size() == 0 )
        {
            return new ArrayList<RelatedParentContent>();
        }

        final RelatedParentContentQuery relatedParentContentQuery = new RelatedParentContentQuery();
        relatedParentContentQuery.contents( contentKeys );
        relatedParentContentQuery.includeOnlyMainVersions( includeOnlyMainVersions );
        relatedParentContentQuery.now( new DateTime( availableCheckDate ) );
        relatedParentContentQuery.includeOfflineContent( includeOfflineContent() );
        relatedParentContentQuery.securityFilter( securityFilter );
        return contentDao.findRelatedParentByKeys( relatedParentContentQuery );
    }


    public void setMaxParentLevel( Integer value )
    {
        this.maxParentLevel = value;
    }

    public void setMaxParentChildrenLevel( Integer value )
    {
        this.maxParentChildrenLevel = value;
    }

    public void setIncludeOnlyMainVersions( boolean includeOnlyMainVersions )
    {
        this.includeOnlyMainVersions = includeOnlyMainVersions;
    }

    public Integer getMaxParentLevel()
    {
        return maxParentLevel;
    }
}
