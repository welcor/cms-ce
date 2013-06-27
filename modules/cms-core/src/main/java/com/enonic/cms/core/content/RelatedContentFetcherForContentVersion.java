/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;

import org.springframework.util.Assert;

import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTrace;
import com.enonic.cms.core.portal.livetrace.RelatedContentFetchTracer;
import com.enonic.cms.store.dao.ContentDao;


public class RelatedContentFetcherForContentVersion
    extends AbstractRelatedContentFetcher
{

    private Collection<ContentVersionEntity> originallyRequestedContentVersions;

    public RelatedContentFetcherForContentVersion( ContentDao contentDao, RelatedContentFetchTrace trace )
    {
        super( contentDao, trace );
    }

    public RelatedContentFetcherForContentVersion( ContentDao contentDao )
    {
        super( contentDao, null );
    }

    public RelatedContentResultSet fetch( final Collection<ContentVersionEntity> versions )
    {
        return doFetch( versions, false );
    }

    public RelatedContentResultSet fetch( final Collection<ContentVersionEntity> versions, final boolean includeVisited )
    {
        return doFetch( versions, includeVisited );
    }

    private RelatedContentResultSet doFetch( Collection<ContentVersionEntity> versions, final boolean includeVisited )
    {
        Assert.notNull( versions, "versions cannot be null" );

        originallyRequestedContentVersions = versions;
        relatedContentResultSet = new RelatedContentResultSetImpl();

        boolean fetchChildren = maxChildrenLevel > 0;
        if ( fetchChildren )
        {
            Collection<RelatedChildContent> rootRelatedChildren = doFindRelatedChildren( versions );
            RelatedContentFetchTracer.traceChildrenFetch( 1, rootRelatedChildren.size(), trace );
            if ( versions.size() > 0 )
            {
                doAddAndFetchChildren( rootRelatedChildren, maxChildrenLevel, includeVisited );
                for ( RelatedChildContent rootRelatedChild : rootRelatedChildren )
                {
                    relatedContentResultSet.addRootRelatedChild( rootRelatedChild );
                }
            }
        }

        return relatedContentResultSet;
    }

    @Override
    protected boolean isAddable( final RelatedContent relatedToAdd, final boolean includeVisited )
    {
        final ContentEntity content = relatedToAdd.getContent();
        final boolean contentIsAllreadyVisited = visitedChildRelatedContent.contains( relatedToAdd.getContent().getKey() );

        final boolean contentVersionIsInOriginallyRequestedContentVersionSet =
            originallyRequestedContentVersions.contains( content.getMainVersion() );

        return ( includeVisited || !contentIsAllreadyVisited ) && !contentVersionIsInOriginallyRequestedContentVersionSet;
    }
}
