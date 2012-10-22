package com.enonic.cms.core.portal.datasource2.handler.content;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.InvalidKeyException;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.CategoryAccessResolver;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.InvalidContentBySectionQueryException;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.store.dao.GroupDao;

public final class GetContentBySectionHandler
    extends DataSourceHandler
{

    private ContentService contentService;

    private GroupDao groupDao;

    public GetContentBySectionHandler()
    {
        super( "getContentBySection" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int[] menuItemKeys = ArrayUtils.toPrimitive( req.param( "menuItemKeys" ).required().asIntegerArray() );
        final int levels = req.param( "levels" ).asInteger( 1 );
        final String query = req.param( "query" ).asString( "" );
        final String orderBy = req.param( "orderBy" ).asString( "" );
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean includeData = req.param( "includeData" ).asBoolean( true );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );

        final PreviewContext previewContext = req.getPreviewContext();
        final UserEntity user = req.getCurrentUser();
        final boolean includeOwnerAndModifierData = true;
        final boolean includeCategoryData = true;
        final boolean includeUserRights = false;

        final ContentXMLCreator xmlCreator = new ContentXMLCreator();

        final ContentBySectionQuery spec = new ContentBySectionQuery();
        final Date now = new Date();

        try
        {
            spec.setMenuItemKeys( MenuItemKey.converToList( menuItemKeys ) );

            spec.setUser( user );
            spec.setSectionFilterStatus( ContentIndexQuery.SectionFilterStatus.APPROVED_ONLY );
            spec.setLevels( levels );
            spec.setIndex( index );
            spec.setCount( count );
            spec.setQuery( query );
            spec.setOrderBy( orderBy );
            spec.setFilterContentOnlineAt( now );

            xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierData );
            xmlCreator.setIncludeContentData( includeData );
            xmlCreator.setIncludeCategoryData( includeCategoryData );
            xmlCreator.setIncludeRelatedContentData( includeData );
            xmlCreator.setIncludeUserRightsInfo( includeUserRights, new CategoryAccessResolver( groupDao ),
                                                 new ContentAccessResolver( groupDao ) );
            xmlCreator.setResultIndexing( index, count );
            xmlCreator.setIncludeVersionsInfoForPortal( false );
            xmlCreator.setIncludeAssignment( true );

            ContentResultSet contents = contentService.queryContent( spec );
            if ( previewContext.isPreviewingContent() )
            {
                contents = previewContext.getContentPreviewContext().overrideContentResultSet( contents );
            }

            RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
            relatedContentQuery.setUser( user );
            relatedContentQuery.setContentResultSet( contents );
            relatedContentQuery.setParentLevel( parentLevel );
            relatedContentQuery.setChildrenLevel( childrenLevel );
            relatedContentQuery.setParentChildrenLevel( 0 );
            relatedContentQuery.setIncludeOnlyMainVersions( true );
            relatedContentQuery.setFilterContentOnlineAt( now );

            RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );
            if ( previewContext.isPreviewingContent() )
            {
                relatedContents = previewContext.getContentPreviewContext().overrideRelatedContentResultSet( relatedContents );
            }

            Document document = xmlCreator.createContentsDocument( user, contents, relatedContents ).getAsJDOMDocument();
            addDataTraceInfo( document );
            return document;
        }
        catch ( InvalidKeyException e )
        {
            return xmlCreator.createEmptyDocument( "Invalid key: " + e.getMessage() ).getAsJDOMDocument();
        }
        catch ( InvalidContentBySectionQueryException e )
        {
            return xmlCreator.createEmptyDocument( e.getMessage() ).getAsJDOMDocument();
        }
    }

    @Autowired
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }
}
