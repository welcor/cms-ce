package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetMyContentByCategoryHandler
    extends DataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetMyContentByCategoryHandler()
    {
        super( "getMyContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String query = req.param( "query" ).asString( "" );
        Integer[] keys = req.param( "categoryKeys" ).required().asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
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
        keys = req.param( "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );

        XMLDocument document =
            dataSourceService.getMyContentByCategory( req, query, categoryKeys, recursive, orderBy, index, count, titlesOnly,
                                                      childrenLevel, parentLevel, parentChildrenLevel, relatedTitlesOnly, includeTotalCount,
                                                      includeUserRights, contentTypeKeys );
        return document.getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
