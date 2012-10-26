package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetMyContentByCategoryHandler")
public final class GetMyContentByCategoryHandler
    extends ParamDataSourceHandler
{
    public GetMyContentByCategoryHandler()
    {
        super( "getMyContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String query = param( req, "query" ).asString( "" );
        Integer[] keys = param( req, "categoryKeys" ).required().asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final boolean recursive = param( req, "recursive" ).asBoolean( false );
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
        keys = param( req, "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );

        XMLDocument document =
            dataSourceService.getMyContentByCategory( req, query, categoryKeys, recursive, orderBy, index, count, titlesOnly,
                                                      childrenLevel, parentLevel, parentChildrenLevel, relatedTitlesOnly, includeTotalCount,
                                                      includeUserRights, contentTypeKeys );
        return document.getAsJDOMDocument();
    }
}
