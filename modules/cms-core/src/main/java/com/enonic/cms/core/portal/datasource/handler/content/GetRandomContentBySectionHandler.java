package com.enonic.cms.core.portal.datasource.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetRandomContentBySectionHandler")
public final class GetRandomContentBySectionHandler
    extends SimpleDataSourceHandler
{
    public GetRandomContentBySectionHandler()
    {
        super( "getRandomContentBySection" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] keys = param( req, "menuItemKeys" ).required().asIntegerArray();
        int[] menuItemKeys = ArrayUtils.toPrimitive( keys );
        final int levels = param( req, "levels" ).asInteger( 1 );
        final String query = param( req, "query" ).asString( "" );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );

        XMLDocument document =
            dataSourceService.getRandomContentBySection( req, menuItemKeys, levels, query, count, includeData, childrenLevel, parentLevel );
        return document.getAsJDOMDocument();
    }
}
