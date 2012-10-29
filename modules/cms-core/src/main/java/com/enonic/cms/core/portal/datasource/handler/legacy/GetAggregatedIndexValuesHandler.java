package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetAggregatedIndexValuesHandler")
public final class GetAggregatedIndexValuesHandler
    extends ParamsDataSourceHandler<GetAggregatedIndexValuesParams>
{
    public GetAggregatedIndexValuesHandler()
    {
        super( "getAggregatedIndexValues", GetAggregatedIndexValuesParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetAggregatedIndexValuesParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getAggregatedIndexValues( req, params.field, params.categoryKeys, params.recursive, params.contentTypeKeys );
        return document.getAsJDOMDocument();
    }
}
