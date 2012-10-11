package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class FindContentByCategoryHandler
    extends DataSourceHandler
{
    public FindContentByCategoryHandler()
    {
        super( "findContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String search = req.param( "search" ).asString( "" );
        final String operator = req.param( "operator" ).asString( "AND" );
        final Integer[] categoryKeys = req.param( "categoryKeys" ).required().asIntegerArray();
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        final String orderBy = req.param( "orderBy" ).asString( "" );
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean titlesOnly = req.param( "titlesOnly" ).asBoolean( false );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );
        final int parentChildrenLevel = req.param( "parentChildrenLevel" ).asInteger( 0 );
        final boolean relatedTitlesOnly = req.param( "relatedTitlesOnly" ).asBoolean( false );
        final boolean includeTotalCount = req.param( "includeTotalCount" ).asBoolean( false );
        final boolean includeUserRights = req.param( "includeUserRights" ).asBoolean( false );
        final Integer[] contentTypeKeys = req.param( "contentTypeKeys" ).asIntegerArray();

        // TODO: Implement based on DataSourceServiceImpl.findContentByCategory(..)
        return null;
    }
}
