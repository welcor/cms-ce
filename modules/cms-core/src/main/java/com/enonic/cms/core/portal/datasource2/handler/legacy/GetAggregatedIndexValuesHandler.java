package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.service.DataSourceService;

public final class GetAggregatedIndexValuesHandler
    extends DataSourceHandler
{
    private DataSourceService dataSourceService;

    public GetAggregatedIndexValuesHandler()
    {
        super( "getAggregatedIndexValues" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String field = req.param( "field" ).required().asString();
        Integer[] keys = req.param( "categoryKeys" ).asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        keys = req.param( "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );

        XMLDocument document =
            dataSourceService.getAggregatedIndexValues( req, field, categoryKeys, recursive, contentTypeKeys );
        return document.getAsJDOMDocument();
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
