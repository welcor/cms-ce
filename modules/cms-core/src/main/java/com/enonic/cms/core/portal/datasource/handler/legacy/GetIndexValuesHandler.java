package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;
import com.enonic.cms.core.service.DataSourceService;

@Component("ds.GetIndexValuesHandler")
public final class GetIndexValuesHandler
    extends ParamDataSourceHandler
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
        final String field = param( req, "field" ).required().asString();
        Integer[] keys = param( req, "categoryKeys" ).required().asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final boolean recursive = param( req, "recursive" ).asBoolean( false );
        keys = param( req, "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );
        final int index = param( req, "index" ).asInteger( 0 );
        final int count = param( req, "count" ).asInteger( 200 );
        final boolean distinct = param( req, "distinct" ).asBoolean( true );
        final String order = param( req, "order" ).asString( "ASC" );

        XMLDocument document =
            dataSourceService.getIndexValues( req, field, categoryKeys, recursive, contentTypeKeys, index, count, distinct, order );
        return document.getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
