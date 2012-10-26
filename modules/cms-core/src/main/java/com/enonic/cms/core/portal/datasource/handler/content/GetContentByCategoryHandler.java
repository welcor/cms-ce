package com.enonic.cms.core.portal.datasource.handler.content;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

import static org.apache.commons.lang.ArrayUtils.toPrimitive;

@Component("ds.GetContentByCategoryHandler")
public final class GetContentByCategoryHandler
    extends ParamDataSourceHandler
{
    public GetContentByCategoryHandler()
    {
        super( "getContentByCategory" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] categoryKeys = param( req, "categoryKeys" ).required().asIntegerArray();
        final int levels = param( req, "levels" ).asInteger( 1 );
        final String query = param( req, "query" ).asString( "" );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );

        return dataSourceService.getContentByCategory( req,  toPrimitive( categoryKeys) , levels, query, orderBy,
                                             index, count, includeData, childrenLevel, parentLevel ).getAsJDOMDocument();
    }
}
