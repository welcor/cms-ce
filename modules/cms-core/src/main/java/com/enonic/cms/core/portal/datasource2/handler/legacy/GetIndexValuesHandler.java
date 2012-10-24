package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetIndexValuesHandler
    extends DataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetIndexValuesHandler()
    {
        super( "getIndexValues" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String path = req.param( "path" ).required().asString();
        Integer[] keys = req.param( "categoryKeys" ).required().asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        keys = req.param( "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );
        final int index = req.param( "index" ).asInteger( 0 );
        final int count = req.param( "count" ).asInteger( 200 );
        final boolean distinct = req.param( "distinct" ).asBoolean( true );
        final String order = req.param( "order" ).asString( "ASC" );

        XMLDocument document =
            dataSourceService.getIndexValues( req, path, categoryKeys, recursive, contentTypeKeys, index, count, distinct, order );
        return document.getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
