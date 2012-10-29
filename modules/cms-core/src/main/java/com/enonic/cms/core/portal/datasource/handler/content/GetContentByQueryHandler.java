package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetContentByQueryHandler")
public final class GetContentByQueryHandler
    extends SimpleDataSourceHandler
{
    public GetContentByQueryHandler()
    {
        super( "getContentByQuery" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String query = param( req, "query" ).asString( "" );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );

        XMLDocument document = dataSourceService.getContentByQuery( req, query, orderBy, index, count, includeData, childrenLevel, parentLevel );
        return document.getAsJDOMDocument();
    }
}
