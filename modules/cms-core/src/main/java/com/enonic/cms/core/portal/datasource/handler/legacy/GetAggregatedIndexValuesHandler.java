package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetAggregatedIndexValuesHandler")
public final class GetAggregatedIndexValuesHandler
    extends SimpleDataSourceHandler
{
    public GetAggregatedIndexValuesHandler()
    {
        super( "getAggregatedIndexValues" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String field = param( req, "field" ).required().asString();
        Integer[] keys = param( req, "categoryKeys" ).asIntegerArray();
        int[] categoryKeys = ArrayUtils.toPrimitive( keys );
        final boolean recursive = param( req, "recursive" ).asBoolean( false );
        keys = param( req, "contentTypeKeys" ).asIntegerArray();
        int[] contentTypeKeys = ArrayUtils.toPrimitive( keys );

        XMLDocument document =
            dataSourceService.getAggregatedIndexValues( req, field, categoryKeys, recursive, contentTypeKeys );
        return document.getAsJDOMDocument();
    }
}
