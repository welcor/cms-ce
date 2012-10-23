package com.enonic.cms.core.portal.datasource2.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetContentBySectionHandler
    extends DataSourceHandler
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
        final int[] menuItemKeys = ArrayUtils.toPrimitive( req.param( "menuItemKeys" ).required().asIntegerArray() );
        final int levels = req.param( "levels" ).asInteger( 1 );
        final String query = req.param( "query" ).asString( "" );
        final String orderBy = req.param( "orderBy" ).asString( "" );
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 10 );
        final boolean includeData = req.param( "includeData" ).asBoolean( true );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );

        return this.dataSourceService.getContentBySection( req, menuItemKeys, levels, query, orderBy, index, count, includeData,
                                                           childrenLevel, parentLevel ).getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
