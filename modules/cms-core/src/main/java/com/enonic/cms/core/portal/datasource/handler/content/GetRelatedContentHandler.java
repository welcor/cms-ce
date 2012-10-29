package com.enonic.cms.core.portal.datasource.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetRelatedContentHandler")
public final class GetRelatedContentHandler
    extends SimpleDataSourceHandler
{
    public GetRelatedContentHandler()
    {
        super( "getRelatedContent" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int[] contentKeys = ArrayUtils.toPrimitive( param( req, "contentKeys" ).required().asIntegerArray() );
        final int relation = param( req, "relation" ).asInteger( 1 );
        final String query = param( req, "query" ).asString( "" );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );
        final boolean requireAll = param( req, "requireAll" ).asBoolean( false );

        return this.dataSourceService.getRelatedContent( req, contentKeys, relation, query, orderBy, index, count, includeData,
                                                         childrenLevel, parentLevel ).getAsJDOMDocument();
    }
}
