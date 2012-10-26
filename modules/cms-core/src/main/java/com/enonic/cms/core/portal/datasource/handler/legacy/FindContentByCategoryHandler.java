package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

import static org.apache.commons.lang.ArrayUtils.toPrimitive;

public final class FindContentByCategoryHandler
    extends ParamDataSourceHandler
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
        final String search = param( req, "search" ).asString( "" );
        final String operator = param( req, "operator" ).asString( "AND" );
        final int[] categories = toPrimitive( param( req, "categories" ).required().asIntegerArray() );
        final boolean includeSubCategories = param( req, "includeSubCategories" ).asBoolean( false );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean titlesOnly = param( req, "titlesOnly" ).asBoolean( false );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );
        final int parentChildrenLevel = param( req, "parentChildrenLevel" ).asInteger( 0 );
        final boolean relatedTitlesOnly = param( req, "relatedTitlesOnly" ).asBoolean( false );
        final boolean includeTotalCount = param( req, "includeTotalCount" ).asBoolean( false );
        final boolean includeUserRights = param( req, "includeUserRights" ).asBoolean( false );
        final int[] contentTypes = toPrimitive( param( req, "contentTypes" ).asIntegerArray() );

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
