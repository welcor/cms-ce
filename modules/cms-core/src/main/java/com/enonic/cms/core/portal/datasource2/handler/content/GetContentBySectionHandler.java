package com.enonic.cms.core.portal.datasource2.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetContentBySectionHandler
    extends ParamDataSourceHandler
{

    private DataSourceService dataSourceService;

    public GetContentBySectionHandler()
    {
        super( "getContentBySection" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int[] menuItemKeys = ArrayUtils.toPrimitive( param( req, "menuItemKeys" ).required().asIntegerArray() );
        final int levels = param( req, "levels" ).asInteger( 1 );
        final String query = param( req, "query" ).asString( "" );
        final String orderBy = param( req, "orderBy" ).asString( "" );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 10 );
        final boolean includeData = param( req, "includeData" ).asBoolean( true );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );

        return this.dataSourceService.getContentBySection( req, menuItemKeys, levels, query, orderBy, index, count, includeData,
                                                           childrenLevel, parentLevel ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
