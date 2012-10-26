package com.enonic.cms.core.portal.datasource.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetContentHandler")
public final class GetContentHandler
    extends ParamDataSourceHandler
{
    public GetContentHandler()
    {
        super( "getContent" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] keys = param( req, "contentKeys" ).required().asIntegerArray();
        int[] contentKeys = ArrayUtils.toPrimitive( keys );
        final String query = param( req, "query" ).asString( "" );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );

        XMLDocument document = dataSourceService.getContent( req, contentKeys, query, orderBy, index, count, includeData, childrenLevel, parentLevel );
        return document.getAsJDOMDocument();
    }
}
