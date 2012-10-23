package com.enonic.cms.core.portal.datasource2.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetRandomContentByCategoryHandler
    extends DataSourceHandler
{
    public GetRandomContentByCategoryHandler()
    {
        super( "getRandomContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] keys = req.param( "categoryKeys" ).required().asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final int levels = req.param( "levels" ).asInteger( 1 );
        final String query = req.param( "query" ).asString( "" );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean includeData = req.param( "includeData" ).asBoolean( true );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );

        XMLDocument document =
            dataSourceService.getRandomContentByCategory( req, categoryKeys, levels, query, count, includeData, childrenLevel, parentLevel );
        return document.getAsJDOMDocument();
    }
}
