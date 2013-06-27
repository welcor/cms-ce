/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;

@Component("ds.GetIndexValuesHandler")
public final class GetIndexValuesHandler
    extends ParamsDataSourceHandler<GetIndexValuesParams>
{
    public GetIndexValuesHandler()
    {
        super( "getIndexValues", GetIndexValuesParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetIndexValuesParams params )
        throws Exception
    {
        XMLDocument document =
            dataSourceService.getIndexValues( req, params.field, params.categoryKeys, params.recursive, params.contentTypeKeys,
                                              params.index, params.count, params.distinct, params.order );
        return document.getAsJDOMDocument();
    }
}
