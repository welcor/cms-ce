package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

import static org.apache.commons.lang.ArrayUtils.toPrimitive;

public final class FindContentByCategoryHandler
    extends DataSourceHandler
{
    private DataSourceService dataSourceService;

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
        final int[] categories = toPrimitive( req.param( "categories" ).required().asIntegerArray() );
        final boolean includeSubCategories = req.param( "includeSubCategories" ).asBoolean( false );
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
        final int[] contentTypes = toPrimitive( req.param( "contentTypes" ).asIntegerArray() );

        return dataSourceService.findContentByCategory( req, search, operator, categories, includeSubCategories, orderBy, index, count,
                                                        titlesOnly, childrenLevel, parentLevel, parentChildrenLevel, relatedTitlesOnly,
                                                        includeTotalCount, includeUserRights, contentTypes ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
