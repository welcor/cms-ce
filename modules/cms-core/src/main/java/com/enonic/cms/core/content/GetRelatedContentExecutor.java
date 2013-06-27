/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.content;


import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;

public class GetRelatedContentExecutor
{
    private ContentService contentService;

    private UserEntity user;

    private Date nowAsDate;

    private PreviewContext previewContext;

    private List<ContentKey> contentFilter;

    private List<ContentTypeKey> contentTypeFilter;

    private List<CategoryKey> categoryFilter;

    private boolean categoryFilterRecursive;

    private boolean includeOfflineContent = false;

    private boolean requireAll = false;

    private int relation;

    private String query;

    private String orderBy;

    private int index;

    private int count;

    private int childrenLevel = 0;

    private int parentLevel = 0;

    private int parentChildrenLevel = 0;

    public GetRelatedContentExecutor( ContentService contentService, Date nowAsDate, PreviewContext previewContext )
    {
        this.contentService = contentService;
        this.nowAsDate = nowAsDate;
        this.previewContext = previewContext;
    }

    public GetRelatedContentExecutor user( UserEntity user )
    {
        this.user = user;
        return this;
    }

    public GetRelatedContentExecutor contentFilter( List<ContentKey> contentKeys )
    {
        Preconditions.checkNotNull( contentKeys, "contentKeys cannot be null" );
        Preconditions.checkArgument( contentKeys.size() > 0, "contentKeys is empty" );

        this.contentFilter = contentKeys;
        return this;
    }

    public GetRelatedContentExecutor contentTypeFilter( List<ContentTypeKey> contentTypes )
    {
        Preconditions.checkNotNull( contentTypes, "contentTypes cannot be null" );
        Preconditions.checkArgument( contentTypes.size() > 0, "contentTypes is empty" );

        this.contentTypeFilter = contentTypes;
        return this;
    }

    public GetRelatedContentExecutor categoryFilter( List<CategoryKey> categories, boolean recursive )
    {
        Preconditions.checkNotNull( categories, "categories cannot be null" );
        Preconditions.checkArgument( categories.size() > 0, "categories is empty" );

        this.categoryFilter = categories;
        this.categoryFilterRecursive = recursive;
        return this;
    }

    public GetRelatedContentExecutor includeOfflineContent( boolean value )
    {
        this.includeOfflineContent = value;
        return this;
    }

    public GetRelatedContentExecutor requireAll( boolean value )
    {
        this.requireAll = value;
        return this;
    }

    public GetRelatedContentExecutor relation( int value )
    {
        this.relation = value;
        return this;
    }

    public GetRelatedContentExecutor query( String value )
    {
        this.query = value;
        return this;
    }

    public GetRelatedContentExecutor orderBy( String value )
    {
        this.orderBy = value;
        return this;
    }

    public GetRelatedContentExecutor index( int value )
    {
        this.index = value;
        return this;
    }

    public GetRelatedContentExecutor count( int value )
    {
        this.count = value;
        return this;
    }

    public GetRelatedContentExecutor childrenLevel( int value )
    {
        this.childrenLevel = value;
        return this;
    }

    public GetRelatedContentExecutor parentLevel( int value )
    {
        this.parentLevel = value;
        return this;
    }

    public GetRelatedContentExecutor parentChildrenLevel( int value )
    {
        this.parentChildrenLevel = value;
        return this;
    }

    public GetRelatedContentResult execute()
    {
        Preconditions.checkNotNull( user, "user must be specified" );
        Preconditions.checkNotNull( contentFilter, "contentFilter must be specified" );
        Preconditions.checkArgument( contentFilter.size() > 0, "contentFilter must contain one or more content keys" );
        Preconditions.checkArgument( count > 0, "count must be larger than zero" );
        Preconditions.checkArgument( relation != 0, "relation must either be -1 (parents) or 1 (children)" );

        final ContentResultSet givenContent = getGivenContent();

        final RelatedContentResultSet relatedContentToGivenContent = getRelatedContentToGivenContent( givenContent );

        final ContentResultSet contentResult = applyQueryOnRelatedContentToGivenContent( relatedContentToGivenContent );

        final RelatedContentResultSet relatedContent = getRelatedContentToContentResult( contentResult );

        return new GetRelatedContentResult( contentResult, relatedContent );
    }

    private RelatedContentResultSet getRelatedContentToContentResult( ContentResultSet mainResultContent )
    {
        // Get the related content of the top level content
        final RelatedContentQuery relatedContentSpec = new RelatedContentQuery( nowAsDate );
        relatedContentSpec.setUser( user );
        relatedContentSpec.setContentResultSet( mainResultContent );
        relatedContentSpec.setParentLevel( parentLevel );
        relatedContentSpec.setChildrenLevel( childrenLevel );
        relatedContentSpec.setParentChildrenLevel( parentChildrenLevel );
        relatedContentSpec.setIncludeOnlyMainVersions( true );
        if ( includeOfflineContent || previewContext.isPreviewingContent() )
        {
            relatedContentSpec.setFilterIncludeOfflineContent();
        }
        else
        {
            relatedContentSpec.setFilterContentOnlineAt( nowAsDate );
        }
        RelatedContentResultSet relatedContent = contentService.queryRelatedContent( relatedContentSpec );
        if ( previewContext.isPreviewingContent() )
        {
            relatedContent = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContent );
            previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContent );
        }
        return relatedContent;
    }

    private ContentResultSet applyQueryOnRelatedContentToGivenContent( RelatedContentResultSet relatedContentToGivenContent )
    {
        final ContentByContentQuery mainResultContentQuery = new ContentByContentQuery();
        mainResultContentQuery.setUser( user );
        mainResultContentQuery.setQuery( query );
        mainResultContentQuery.setOrderBy( orderBy );
        mainResultContentQuery.setIndex( index );
        mainResultContentQuery.setCount( count );
        if ( contentFilter != null )
        {
            mainResultContentQuery.setContentKeyFilter( relatedContentToGivenContent.getContentKeys() );
        }
        if ( categoryFilter != null )
        {
            mainResultContentQuery.setCategoryKeyFilter( categoryFilter, categoryFilterRecursive ? Integer.MAX_VALUE : 1 );
        }
        if ( contentTypeFilter != null )
        {
            mainResultContentQuery.setContentTypeFilter( contentTypeFilter );
        }
        if ( includeOfflineContent || previewContext.isPreviewingContent() )
        {
            mainResultContentQuery.setFilterIncludeOfflineContent();
        }
        else
        {
            mainResultContentQuery.setFilterContentOnlineAt( nowAsDate );
        }
        ContentResultSet mainResultContent = contentService.queryContent( mainResultContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            mainResultContent = previewContext.getContentPreviewContext().overrideContentResultSet( mainResultContent );
            previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( mainResultContent );
        }
        return mainResultContent;
    }

    private RelatedContentResultSet getRelatedContentToGivenContent( ContentResultSet givenContent )
    {
        final RelatedContentResultSet relatedContentToGivenContent;
        if ( requireAll && givenContent.getLength() > 1 )
        {
            relatedContentToGivenContent = contentService.getRelatedContentRequiresAll( user, relation, givenContent );
        }
        else
        {
            final RelatedContentQuery relatedContentToBaseContentSpec = new RelatedContentQuery( nowAsDate );
            relatedContentToBaseContentSpec.setUser( user );
            relatedContentToBaseContentSpec.setContentResultSet( givenContent );
            relatedContentToBaseContentSpec.setParentLevel( relation < 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setChildrenLevel( relation > 0 ? 1 : 0 );
            relatedContentToBaseContentSpec.setParentChildrenLevel( 0 );
            relatedContentToBaseContentSpec.setIncludeOnlyMainVersions( true );
            if ( includeOfflineContent )
            {
                relatedContentToBaseContentSpec.setFilterIncludeOfflineContent();
            }
            relatedContentToGivenContent = contentService.queryRelatedContent( relatedContentToBaseContentSpec );

            final boolean previewedContentIsAmongBaseContent = previewContext.isPreviewingContent() &&
                givenContent.containsContent( previewContext.getContentPreviewContext().getContentPreviewed().getKey() );
            if ( previewedContentIsAmongBaseContent )
            {
                // ensuring offline related content to the previewed content to be included when previewing
                RelatedContentQuery relatedSpecForPreviewedContent = new RelatedContentQuery( relatedContentToBaseContentSpec );
                relatedSpecForPreviewedContent.setFilterIncludeOfflineContent();
                relatedSpecForPreviewedContent.setContentResultSet(
                    new ContentResultSetNonLazy( previewContext.getContentPreviewContext().getContentAndVersionPreviewed().getContent() ) );

                RelatedContentResultSet relatedContentsForPreviewedContent =
                    contentService.queryRelatedContent( relatedSpecForPreviewedContent );

                relatedContentToGivenContent.overwrite( relatedContentsForPreviewedContent );
                previewContext.getContentPreviewContext().registerContentToBeAvailableOnline( relatedContentToGivenContent );
            }
        }
        return relatedContentToGivenContent;
    }

    private ContentResultSet getGivenContent()
    {
        final ContentByContentQuery baseContentQuery = new ContentByContentQuery();
        baseContentQuery.setContentKeyFilter( contentFilter );
        baseContentQuery.setUser( user );
        if ( includeOfflineContent )
        {
            baseContentQuery.setFilterIncludeOfflineContent();
        }
        else
        {
            baseContentQuery.setFilterContentOnlineAt( nowAsDate );
        }
        ContentResultSet baseContent = contentService.queryContent( baseContentQuery );
        if ( previewContext.isPreviewingContent() )
        {
            baseContent = previewContext.getContentPreviewContext().applyPreviewedContentOnContentResultSet( baseContent, contentFilter );
        }
        return baseContent;
    }
}